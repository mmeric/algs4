/*************************************************************************
 *
 * Compilation:  javac Fast.java
 * Execution:   java Fast input4.txt
 * Dependencies: Point.java, In.java,  StdDraw.java
 *
 * Description: A faster, sorting-based solution. 
 * Remarkably, it is possible to solve the problem much faster than the
 * brute-force solution described above. 
 * Given a point p, the following method determines whether p participates 
 * in a set of 4 or more collinear points.
 * - Think of p as the origin.
 * - For each other point q, determine the slope it makes with p.
 * - Sort the points according to the slopes they makes with p.
 * - Check if any 3 (or more) adjacent points in the sorted order have equal 
 * slopes with respect to p. If so, these points, together with p, are collinear.
 * Applying this method for each of the N points in turn yields an efficient 
 * algorithm to the problem. The algorithm solves the problem because points 
 * that have equal slopes with respect to p are collinear, and sorting brings 
 * such points together. The algorithm is fast because the bottleneck operation 
 * is sorting.
 *
 * The order of growth of the running time of your program should be 
 *  N . N . log N in the worst case and it should use space proportional to N.
 *
 *************************************************************************/

import java.util.Collections;
import java.util.ArrayList;
import java.util.Arrays;

public class Fast {
    public static void main(String[] args) {
        // rescale coordinates and turn on animation mode
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        StdDraw.show(0);    // display to screen all at once
        StdDraw.setPenRadius(0.01);  // make the points a bit larger

        // read points from the input file
        In in = new In(args[0]);
        int N = in.readInt();
        Point[] points = new Point[N];
        for (int i = 0; i < N; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
            points[i].draw();   // draw point
        }

        StdDraw.show(0);       
        StdDraw.setPenRadius(); // reset the pen radius

        Arrays.sort(points);    // natural sort by y value
        Point[] aux = new Point[N];   // aux helper
        ArrayList<ArrayList<Point>> listOfList = new ArrayList<ArrayList<Point>>();

        for (int i = 0; i < N; i++) {
            Point p = points[i];    // pick origin point
            System.arraycopy(points, 0, aux, 0, N); // copy points to aux
            Arrays.sort(aux, i, N, p.SLOPE_ORDER);  // sort from i to N

            ArrayList<Point> collinear = new ArrayList<Point>();
            for (int q = i+1; q < N; q++) {    // scan from i+1
                if (p == aux[q]) {  // same point
                    continue;
                }
                if (collinear.isEmpty()) {
                    collinear.add(aux[q]);
                } else if (p.slopeTo(aux[q-1]) == p.slopeTo(aux[q])) {
                    collinear.add(aux[q]);
                } else if (collinear.size() > 2) {
                    collinear.add(p);   // add origin p
                    Collections.sort(collinear);   // sort collinear
                    if (listOfList.isEmpty() || !isSubSet(collinear, listOfList)) {
                        listOfList.add(collinear); 
                    }
                    collinear = new ArrayList<Point>();
                    collinear.add(aux[q]);
                } else {                   
                    collinear.clear();
                    collinear.add(aux[q]);
                }
            }

            if (collinear.size() > 2) {
                collinear.add(p);   // add origin p
                Collections.sort(collinear);   // sort collinear
                if (listOfList.isEmpty() || !isSubSet(collinear, listOfList)) {
                    listOfList.add(collinear); 
                }
            }             
        }

        // print final result
        StdOut.println("Result: " + listOfList.size() + " collinear points: ");
        for (ArrayList<Point> aList : listOfList) {
            for (int i = 0; i < aList.size(); i++) {
                StdOut.print(aList.get(i));
                if (i == aList.size() - 1) {
                    StdOut.println();
                } else {
                    StdOut.print(" -> ");
                }
            }
            Collections.min(aList).drawTo(Collections.max(aList));
        }
        StdDraw.show(0);
    }

    // check collinear is the subset of listOfList
    private static boolean isSubSet(ArrayList<Point> checkList, 
        ArrayList<ArrayList<Point>> listOfList) {
        for (ArrayList<Point> aList : listOfList) {
            if (aList.contains(Collections.min(checkList))
                && aList.contains(Collections.max(checkList))) {
                    return true;
                }
        }
        return false;
    }
}