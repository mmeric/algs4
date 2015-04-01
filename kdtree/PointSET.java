/*************************************************************************
 *  Compilation:  javac PointSET.java
 *  Execution:    java PointSET 
 *
 *  Dependencies: StdDraw.java StdRandom.java
 *
 *  Brute-force implementation. Write a mutable data type PointSET.java 
 * that represents a set of points in the unit square. Implement the 
 * following API by using a red-black BST (using either SET from algs4.jar 
 * or java.util.TreeSet).
 *
 *************************************************************************/

import java.util.TreeSet;

public class PointSET {
    private TreeSet<Point2D> set;

    public PointSET() {   // construct an empty set of points 
        set = new TreeSet<Point2D>();
    }

    public boolean isEmpty() {  // is the set empty? 
        return size() == 0;
    }

    public int size() {     // number of points in the set 
        return set.size();
    }
    
    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) {
            throw new NullPointerException();
        }
        set.add(p);
    }

    public boolean contains(Point2D p) {  // does the set contain point p? 
        if (p == null) {
            throw new NullPointerException();
        }
        return set.contains(p);
    }

    public void draw() {    // draw all points to standard draw
        for (Point2D p : set) {
            p.draw();
        }
        StdDraw.show(0);
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new NullPointerException();
        }

        Stack<Point2D> ptsInRect = new Stack<Point2D>();
        for (Point2D p : set) {
            if (rect.contains(p)) {
                ptsInRect.push(p);
            }
        }
        return ptsInRect;
    }

    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new NullPointerException();
        }
        if (set.isEmpty()) {
            return null;
        }

        double minDis = p.distanceTo(set.first());
        Point2D minDisPt = set.first();
        for (Point2D v : set) {
            double curDis = v.distanceTo(p);
            if (curDis < minDis) {
                minDis = curDis;
                minDisPt = v;
            }
        }
        return minDisPt;
    }

    // unit testing of the methods
    public static void main(String[] args) {
        String fname = "kdtree/circle1000.txt";
        In in = new In(fname);
        
        PointSET ptSet = new PointSET();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            ptSet.insert(p);
        }

        RectHV rect = new RectHV(0.0, 0.0, 1.0, 1.0);
        rect.draw();    // draw rectangle
        StdDraw.show(0);
        StdDraw.setPenRadius(.005);
        ptSet.draw();   // draw the points set

        RectHV target = new RectHV(0.6, 0.08, 0.99, 0.4);  // target rectangle
        StdDraw.setPenRadius();
        StdDraw.setPenColor(StdDraw.BLUE);
        target.draw();

        StdDraw.setPenRadius(.01);
        Iterable<Point2D> selectPts = ptSet.range(target);
        StdOut.println("Points in range " + target);
        int i = 0;
        for (Point2D v : selectPts) {
            i++;
            StdOut.println(v);
            v.draw();
        }  
        StdOut.println("  total " + i + " points.");    

        StdDraw.setPenColor(StdDraw.RED);
        Point2D p = new Point2D(0.2, 0.3);  // select point p
        p.draw();

        Point2D nP = ptSet.nearest(p);
        StdOut.println("Point nearest to " + p + " is " + nP);
        StdDraw.setPenRadius();
        nP.drawTo(p);
        StdDraw.show(0);
    }
}
