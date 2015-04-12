/*************************************************************************
 *  Compilation:  javac SeamCarver.java
 *  Execution:    java SeamCarver 
 *
 *  http://coursera.cs.princeton.edu/algs4/assignments/seamCarving.html
 *
 * Seam-carving is a content-aware image resizing technique where the image 
 * is reduced in size by one pixel of height (or width) at a time. 
 *  - A vertical seam in an image is a path of pixels connected from the top 
 *    to the bottom with one pixel in each row. 
 *  - A horizontal seam is a path of pixels connected from the left to the 
 *    right with one pixel in each column.
 * 
 * 0. Notation: 
 *    In image processing, pixel (x, y) refers to the pixel in column x and
 *  row y, with pixel (0, 0) at the upper left corner and pixel (W − 1, H − 1)
 *  at the bottom right corner. The color of a pixel is represented in RGB 
 *  space, using three integers between 0 and 255. (java.awt.Color)
 *
 * 1. Energy calculation
 *    calculate the energy of each pixel using the dual gradient energy function
 *
 * 2. Seam identification 
 *    to find a vertical seam of minimum total energy. This is similar to the 
 *  classic shortest path problem in an edge-weighted digraph except for the 
 *  following:
 *  - The weights are on the vertices instead of the edges.
 *  - We want to find the shortest path from any of the W pixels in the top row to 
 *    any of the W pixels in the bottom row.
 *  - The digraph is acyclic, where there is a downward edge from pixel (x, y) to 
 *    pixels (x − 1, y + 1), (x, y + 1), and (x + 1, y + 1), assuming that the 
 *    coordinates are in the prescribed range.
 *
 *************************************************************************/

import java.awt.Color;

public class SeamCarver {
    private Picture pic;
    private double[] weights;  
    private double[] distTo;
    private int[] edgeTo;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException();
        }
        this.pic = new Picture(picture);
    }               

    public Picture picture() {      // copy of current picture
        return new Picture(pic);
    } 

    public int width() {    // width of current picture
        return pic.width();
    } 

    public int height() {   // height of current picture
        return pic.height();
    }  

    // energy of pixel at column x and row y                       
    public double energy(int x, int y) {
        if (x < 0 || x >= width() || y < 0 || y >= height()) {
            throw new java.lang.IndexOutOfBoundsException(x + ", " + y);
        }

        if (x == 0 || x == width() - 1 || y == 0 || y == height() - 1) {
            return 255 * 255 * 3;  // border: 255*255 * 3 = 195075
        }

        return squareGradient(pic.get(x-1, y), pic.get(x+1, y))
             + squareGradient(pic.get(x, y-1), pic.get(x, y+1));
    } 

    private int squareGradient(Color one, Color other) {
        int r = one.getRed() - other.getRed();
        int g = one.getGreen() - other.getGreen();
        int b = one.getBlue() - other.getBlue();
        return r*r + g*g + b*b;
    }

    // sequence of indices of minimum-energy for horizontal seam            
    public int[] findHorizontalSeam() {
        int size = width() * height();

        // initialize weights, distTo, and edgeTo, start from col=0
        weights = new double[size];
        distTo = new double[size];
        edgeTo = new int[size];

        for (int row = 0; row < height(); row++) {
            for (int col = 0; col < width(); col++) {
                int p = position(col, row);
                if (col == 0) {
                    distTo[p] = 0;
                } else {
                    distTo[p] = Double.POSITIVE_INFINITY;
                }              
                edgeTo[p] = -1;
                weights[p] = energy(col, row);
            }
        }

        // relax in topological order: from left to right, skip last column
        for (int col = 0; col < width() - 1; col++) {
            for (int row = 0; row < height(); row++) {
                int v = position(col, row);
                if (row - 1 >= 0) {
                    relax(v, position(col+1, row-1));   // v -> right-up
                }
                relax(v, position(col+1, row));         // v -> right
                if (row + 1 < height()) {
                    relax(v, position(col+1, row+1));   // v -> right-down
                }
            }
        }

        // find the minimum distTo at endOfSeam at col = width()-1
        double minEnergy = Double.POSITIVE_INFINITY;
        int endSeam = 0;

        for (int row = 0; row < height(); row++) {
            int col = width() - 1;
            int v = position(col, row);
            if (distTo[v] < minEnergy) {
                minEnergy = distTo[v];
                endSeam = v;
            }
        }

        return horizontalSeam(endSeam);
    }    

    private int[] horizontalSeam(int endSeam) {
        int[] seam = new int[width()];
        for (int v = endSeam; v >= 0; v = edgeTo[v]) {
            seam[p2Column(v)] = p2Row(v);
        }
        return seam;
    }

    // SP relax: min weight from source to the indexed the node found so far
    private void relax(int from, int to) {
        if (distTo[to] > distTo[from] + weights[to]) {
            distTo[to] = distTo[from] + weights[to];
            edgeTo[to] = from;
        }
    }

    private int position(int col, int row) {
        return width() * row + col;
    }

    private int p2Row(int position) {
        return position / width();
    }

    private int p2Column(int position) {
        return position % width();
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        int size = width() * height();

        // initialize weights, distTo, and edgeTo from row=0
        weights = new double[size];
        distTo = new double[size];
        edgeTo = new int[size];

        for (int col = 0; col < width(); col++) {
            for (int row = 0; row < height(); row++) {
                int p = position(col, row);
                if (row == 0) {
                    distTo[p] = 0;
                } else {
                    distTo[p] = Double.POSITIVE_INFINITY;
                }
                edgeTo[p] = -1;
                weights[p] = energy(col, row);
            }
        }

        // relax in topological order: from up to down, skip the last row
        for (int row = 0; row < height() - 1; row++) {
            for (int col = 0; col < width(); col++) {
                int v = position(col, row);
                if (col - 1 >= 0) {
                    relax(v, position(col-1, row+1));   // v -> left-down
                }
                relax(v, position(col, row+1));         // v -> down
                if (col + 1 < width()) {
                    relax(v, position(col+1, row+1));   // v -> right-down
                }
            }
        }

        // find the minimum distTo at endOfSeam at row = height() - 1
        double minEnergy = Double.POSITIVE_INFINITY;
        int endSeam = 0;

        for (int col = 0; col < width(); col++) {
            int row = height() - 1;
            int v = position(col, row);
            if (distTo[v] < minEnergy) {
                minEnergy = distTo[v];
                endSeam = v;
            }
        }

        return verticalSeam(endSeam);
    } 

    private int[] verticalSeam(int endSeam) {
        int[] seam = new int[height()];
        for (int v = endSeam; v >= 0; v = edgeTo[v]) {
            seam[p2Row(v)] = p2Column(v);
        }
        return seam;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (height() <= 1 || seam.length != width()) {
            throw new java.lang.IllegalArgumentException();
        }
        checkSeam(seam);

        Picture p = new Picture(width(), height()-1);
        for (int col = 0; col < width(); col++) {
            if (seam[col] < 0 || seam[col] >= height()) {
                throw new java.lang.IndexOutOfBoundsException();
            }
            for (int row = 0; row < height()-1; row++) {
                if (row < seam[col]) {
                    p.set(col, row, pic.get(col, row));
                } else {
                    p.set(col, row, pic.get(col, row + 1)); // get next row
                }
            }
        }

        pic = p;

        distTo = null;  // free memory
        edgeTo = null;
        weights = null;  
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (width() <= 1 || seam.length != height()) {
            throw new java.lang.IllegalArgumentException();
        }
        checkSeam(seam);

        Picture p = new Picture(width()-1, height());
        for (int row = 0; row < height(); row++) {
            if (seam[row] < 0 || seam[row] >= width()) {
                throw new java.lang.IndexOutOfBoundsException();
            }
            for (int col = 0; col < width() - 1; col++) {
                if (col < seam[row]) {
                    p.set(col, row, pic.get(col, row));
                } else {
                    p.set(col, row, pic.get(col+1, row));
                }
            }
        }

        pic = p;

        distTo = null;  // free memory
        edgeTo = null;
        weights = null;  
    } 

    private void checkSeam(int[] seam) {
        for (int i = 1; i < seam.length; i++) {
            if (Math.abs(seam[i] - seam[i-1]) > 1) {
                throw new java.lang.IllegalArgumentException();
            }
        }
    }
}