/*************************************************************************
 *
 * Compilation:  javac Brute.java
 * Execution:   java Brute input4.txt
 * Dependencies: Point.java, In.java,  StdDraw.java
 *
 * Description: examines 4 points at a time and checks whether they all 
 * lie on the same line segment, printing out any such line segments to 
 * standard output and drawing them using standard drawing. To check 
 * whether the 4 points p, q, r, and s are collinear, check whether the 
 * slopes between p and q, between p and r, and between p and s are all 
 * equal.
 *
 * The order of growth of the running time of your program should be N4 
 * in the worst case and it should use space proportional to N.
 *
 *************************************************************************/

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class Brute {
    public static void main(String[] args) {
        // rescale coordinates and turn on animation mode
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        StdDraw.show(0);
        StdDraw.setPenRadius(0.01);  // make the points a bit larger

        // read points from the input file
        In in = new In(args[0]);
        int N = in.readInt();
        Point[] points = new Point[N];
        for (int i = 0; i < N; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
            points[i].draw();
        }

        // display to screen all at once
        StdDraw.show(0);
        // reset the pen radius
        StdDraw.setPenRadius();

        // Go each 4 points and check whether they all lie on the same line
        for (int p = 0; p < N; p++) {
            for (int q = p + 1; q < N; q++) {
                double slopeToQ = points[p].slopeTo(points[q]);
                for (int r = q + 1; r < N; r++) {
                    double slopeToR = points[p].slopeTo(points[r]);
                    if (slopeToQ == slopeToR) {
                        for (int s = r + 1; s < N; s++) {
                            double slopeToS = points[p].slopeTo(points[s]);
                            if (slopeToS == slopeToQ) {
                                // create list of collinear points 
                                List<Point> collinear = new ArrayList<Point>(4);
                                collinear.add(points[p]);
                                collinear.add(points[q]);
                                collinear.add(points[r]);
                                collinear.add(points[s]);
                                // sort the points in list
                                Collections.sort(collinear);
                                // display collinear points
                                for (int i = 0; i < 4; i++) {
                                    StdOut.print(collinear.get(i));
                                    if (i < 3) {
                                        StdOut.print(" -> ");
                                    } else {
                                        StdOut.println();
                                    }
                                }                               
                                // draw collinear line
                                Collections.min(collinear).drawTo(Collections.max(collinear));
                            }
                        }   
                    }
                }
            }
        }

        StdDraw.show(0);
    }
}