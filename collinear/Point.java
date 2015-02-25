/*************************************************************************
 *
 * Compilation:  javac Point.java
 * Execution:   java Point input4.txt
 * Dependencies: StdDraw.java
 *
 * Description: An immutable data type for points in the plane.
 *
 *************************************************************************/

import java.util.Comparator;

public class Point implements Comparable<Point> {

    // compare points by slope order
    public final Comparator<Point> SLOPE_ORDER = new SlopeOrder();

    private final int x;                              // x coordinate
    private final int y;                              // y coordinate

    // create the point (x, y)
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // plot this point to standard drawing
    public void draw() {
        StdDraw.point(x, y);
    }

    // draw line between this point and that point to standard drawing
    public void drawTo(Point that) {
        StdDraw.line(this.x, this.y, that.x, that.y);
    }

    // slope between this point and that point
    public double slopeTo(Point that) {
        if (that == null) {
            throw new NullPointerException();
        }
        if (x == that.x) {
            if (y == that.y) {  
                return Double.NEGATIVE_INFINITY;  // same point
            }
            return Double.POSITIVE_INFINITY;  // vertical line
        }
        if (y == that.y) {
            return 0.0;   // horizontal line
        }
        return (double) (that.y - y) / (that.x - x); 
    }

    // is this point lexicographically smaller than that one?
    // comparing y-coordinates and breaking ties by x-coordinates
    public int compareTo(Point that) {
        if (that == null) {
            throw new NullPointerException();
        }
        if (y == that.y && x == that.x) {
            return 0;   // tie
        }
        if (y < that.y || (y == that.y && x < that.x)) {
            return -1;   // less than
        }
        return 1;
    }

    // is this equal to that ?
    public boolean equals(Point that) {
        return this.compareTo(that) == 0;
    }

    // compare two points by slope order
    private class SlopeOrder implements Comparator<Point> {
        public int compare(Point p1, Point p2) {
            double slope1 = slopeTo(p1);
            double slope2 = slopeTo(p2);
            if (slope1 == slope2) {
                return 0;
            }
            if (slope1 < slope2) {
                return -1;
            }
            return 1;
        }
    }

    // return string representation of this point
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    // unit test
    public static void main(String[] args) {
    }
}