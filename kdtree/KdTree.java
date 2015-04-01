/*************************************************************************
 *  Compilation:  javac KdTree.java
 *  Execution:    java KdTree 
 *
 *  Dependencies: StdDraw.java StdRandom.java Point2D.java RectHV.java
 *
 *  2d-tree implementation. Write a mutable data type KdTree.java that 
 * uses a 2d-tree to implement the same API
 *
 *  A 2d-tree is a generalization of a BST to two-dimensional keys.  
 * The idea is to build a BST with points in the nodes, using the x- and 
 * y-coordinates of the points as keys in strictly alternating sequence.
 *
 *************************************************************************/

public class KdTree {
    private static final boolean VERT = true;    // vertical
    private static final boolean HORIZ = false;  // horizontal
    private Node root;
    private int size;

    public KdTree() {   // construct an empty set of points
        root = null;
        size = 0;
    }

    // helper KdTree Node
    private class Node {   
        private Point2D point;  // the point
        private RectHV rect;  // axis-aligned rectangle corresponding to this node
        private Node lb;      // left/bottom subtree
        private Node rt;      // right/top subtree

        public Node(Point2D p, RectHV rect) {
            this.point = p;
            this.rect = rect;
        }

        public String toString() {
            StringBuilder s = new StringBuilder();
            s.append(point.toString());
            s.append(" ");
            s.append(rect.toString());
            if (lb != null) {
                s.append("\nlb: ");
                s.append(lb.toString());
            }
            if (rt != null) {
                s.append("\nrt: ");
                s.append(rt.toString());
            }
            return s.toString();
        }
        
    }

    public boolean isEmpty() {  // is the set empty?
        return size == 0;
    }

    public int size() {     // number of points in the set
        return size;
    }

    // add the point p to the set (if it is not already in the set)
    public void insert(Point2D p) {
        root = insert(root, p, VERT, 0, 0, 1, 1);
    }

    private Node insert(Node n, Point2D p, boolean oritation, 
                double xmin, double ymin, double xmax, double ymax) {
        if (n == null) {
            this.size++;
            return new Node(p, new RectHV(xmin, ymin, xmax, ymax));
        }

        if (n.point.equals(p)) {  // already in set
            return n;
        }

        if (oritation == VERT) {
            double cmp = p.x() - n.point.x();
            if (cmp < 0) {
                n.lb = insert(n.lb, p, !oritation, 
                    n.rect.xmin(), n.rect.ymin(), n.point.x(), n.rect.ymax());
            } else {
                n.rt = insert(n.rt, p, !oritation, 
                    n.point.x(), n.rect.ymin(), n.rect.xmax(), n.rect.ymax());                   
            }
        } else {   // HORIZ
            double cmp = p.y() - n.point.y();
            if (cmp < 0) {
                n.lb = insert(n.lb, p, !oritation, 
                    n.rect.xmin(), n.rect.ymin(), n.rect.xmax(), n.point.y());
            } else {
                n.rt = insert(n.rt, p, !oritation, 
                    n.rect.xmin(), n.point.y(), n.rect.xmax(), n.rect.ymax());
            }
        }
        return n;
    }

    // dose the set contain the point p?
    public boolean contains(Point2D p) {
        return contains(root, p, VERT);
    }

    private boolean contains(Node n, Point2D p, boolean oritation) {
        if (n == null) {
            return false;
        }
        if (n.point.equals(p)) {
            return true;
        }

        double cmp;
        if (oritation == VERT) { 
            cmp = p.x() - n.point.x();
        } else {  // HORIZ
            cmp = p.y() - n.point.y();
        }

        if (cmp < 0) {
            return contains(n.lb, p, !oritation);
        } else {
            return contains(n.rt, p, !oritation);
        }
    }

    // draw all of the points to standard draw
    public void draw() {
        draw(root, VERT);
    }

    private void draw(Node n, boolean oritation) {
        if (n == null) {
            return;
        }

        StdDraw.setPenColor(StdDraw.BLACK);  
        StdDraw.setPenRadius(.01);
        n.point.draw();             // draw the point
        StdDraw.setPenRadius();

        if (oritation == VERT) {    // vertical line
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(n.point.x(), n.rect.ymin(), n.point.x(), n.rect.ymax());
        } else {    // HORIZ: horizontal line
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(n.rect.xmin(), n.point.y(), n.rect.xmax(), n.point.y());
        }

        draw(n.lb, !oritation);

        draw(n.rt, !oritation);

    }

    // all points in the set that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        Stack<Point2D> stack = new Stack<Point2D>();
        range(root, rect, stack);
        return stack;
    }

    private void range(Node n, RectHV rect, Stack<Point2D> stack) {
        if (n == null) {
            return;
        }

        if (!n.rect.intersects(rect)) {
            return;
        }

        if (rect.contains(n.point)) {
            stack.push(n.point);
        }
        range(n.lb, rect, stack);
        range(n.rt, rect, stack);

    }

    // a nearest neighbor in the set to p; null if set is empty
    public Point2D nearest(Point2D p) {
        return nearest(root, p, Double.POSITIVE_INFINITY);
    }

    private Point2D nearest(Node n, Point2D p, double distance) {
        if (n == null) {
            return null;
        }
        if (n.rect.distanceTo(p) >= distance) {
            return null;
        }

        Point2D minPoint = null;
        double minDist = distance;
        double d = n.point.distanceTo(p);
        if (d < minDist) {
            minPoint = n.point;
            minDist = d;
        }

        Node node1 = n.lb;
        Node node2 = n.rt;

        if (node1 != null && node2 != null) {
            if (node1.rect.distanceTo(p) > node2.rect.distanceTo(p)) {
                node1 = n.rt;   // node1 is nearer
                node2 = n.lb;
            } 
        } 

        Point2D minPoint1 = nearest(node1, p, minDist);
        if (minPoint1 != null) {
            d = p.distanceTo(minPoint1);
            if (d < minDist) {
                minPoint = minPoint1;
                minDist = d;
            }
        }

        Point2D minPoint2 = nearest(node2, p, minDist);
        if (minPoint2 != null) {
            d = p.distanceTo(minPoint2);
            if (d < minDist) {
                minPoint = minPoint2;
                minDist = d;
            }
        }

        return minPoint;
    }

/*    public String toString() {
        return root.toString();
    } */

    public static void main(String[] args) {
        KdTree kdtree = new KdTree();
        kdtree.insert(new Point2D(.7, .2));
        kdtree.insert(new Point2D(.5, .4));
        kdtree.insert(new Point2D(.2, .3));
        kdtree.insert(new Point2D(.4, .7));
        kdtree.insert(new Point2D(.9, .6));
        assert kdtree.size() == 5;
        StdOut.println(kdtree);
        StdOut.println("KdTree size: " + kdtree.size());

        kdtree.draw();
        StdDraw.show(0);

        RectHV target = new RectHV(.3, .2, .8, .8);
        StdDraw.setPenRadius();
        StdDraw.setPenColor(StdDraw.YELLOW);
        target.draw();

        StdOut.println("Points in " + target);
        StdDraw.setPenRadius(.01);
        StdDraw.setPenColor(StdDraw.RED);
        Iterable<Point2D> selectPts = kdtree.range(target);
        for (Point2D v : selectPts) {
            StdOut.println(v);
            v.draw();
        }

        Point2D p = new Point2D(.5, .5);
        Point2D np = kdtree.nearest(p);

        StdDraw.setPenRadius(.02);
        StdDraw.setPenColor(StdDraw.GREEN); 
        p.draw();

        StdOut.println("Nearest to " + p + " is " + np);
        StdDraw.setPenRadius();
        StdDraw.setPenColor(StdDraw.GREEN); 
        np.drawTo(p);
        StdDraw.show(0);      

    }
}