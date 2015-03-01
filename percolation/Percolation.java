/****************************************************************************
 *  Compilation:  javac Percolation.java
 *  Execution:  java Percolation < input.txt
 *  Dependencies: StdIn.java StdOut.java
 *
 *  Percolation algorithm.
 *
 ****************************************************************************/

public class Percolation {
    private int N;                      // N-by-N grid
    private boolean[] cStates;          // cell states: flase-blocked, true-open
    private WeightedQuickUnionUF uf1, uf2;    // weighted quick union-find data structure
    //private QuickFindUF uf1, uf2;       // quick find union-find data structure
    private int vTop;                   // virtual top cell position: N*N
    private int vBottom;

    // constructor: create N-by-N grid, with all sites blocked
    public Percolation(int N) {
        if (N < 1) throw new IllegalArgumentException();
        this.N = N;
        uf1 = new WeightedQuickUnionUF(N*N+1); // +1 for virtual top cell
        uf2 = new WeightedQuickUnionUF(N*N+2); // +2 for virtual top and bottom cells
        //uf1 = new QuickFindUF(N*N+1);
        //uf2 = new QuickFindUF(N*N+2);
        cStates = new boolean[N*N]; 
        vTop = N * N;
        vBottom = N * N + 1;
    }

    // open site (row i, column j) if it is not already
    public void open(int i, int j) {
        if (isOpen(i, j)) return;

        int block = xyTo1D(i, j);
        cStates[block] = true;
        
        if (i != 1 && isOpen(i-1, j)) { // if not top row
            uf1.union(block, xyTo1D(i-1, j)); 
            uf2.union(block, xyTo1D(i-1, j)); 
        }            
        else if (i == 1) {  // connect to virtual top cell if top row
            uf1.union(block, vTop);  
            uf2.union(block, vTop);
        }
            
        if (i != N && isOpen(i+1, j)) { // if not bottom row
            uf1.union(block, xyTo1D(i+1, j));  
            uf2.union(block, xyTo1D(i+1, j)); 
        } 
        else if (i == N) {  // connect to vitural bottom cell
            uf2.union(block, vBottom);
        }           
            
        if (j != 1 && isOpen(i, j-1)) { // not left border
            uf1.union(block, xyTo1D(i, j-1));  
            uf2.union(block, xyTo1D(i, j-1)); 
        }
            
        if (j != N && isOpen(i, j+1)) { // not right border
            uf1.union(block, xyTo1D(i, j+1));  
            uf2.union(block, xyTo1D(i, j+1));  
        }            
    }

    private void checkRange(int x, int y) {
        if (x < 1 || x > N || y < 1 || y > N) {
            throw new IndexOutOfBoundsException("Values are out of range");
        }           
    }

    // convert site (row, column) to unique 1D integer in uf
    private int xyTo1D(int row, int col) {
        return (row-1)*N + (col-1);
    }

    // is site (row i, column j) open ?
    public boolean isOpen(int i, int j) {
        checkRange(i, j);
        return cStates[xyTo1D(i, j)];
    }

    // is site (row i, column j) full ?
    public boolean isFull(int i, int j) {
        checkRange(i, j);
        if (!isOpen(i, j)) return false;
        return uf1.connected(vTop, xyTo1D(i, j));
    }

    // does the system percolate ?
    public boolean percolates() {
        return uf2.connected(vTop, vBottom);
    }

    public static void main(String[] args) {
        int N = StdIn.readInt();
        Percolation perc = new Percolation(N);
        while (!StdIn.isEmpty()) {
            int x = StdIn.readInt();
            int y = StdIn.readInt();

            if (perc.isOpen(x, y)) 
                continue;

            perc.open(x, y);

            StdOut.print("open (" + x + ", " + y + ") ");

            if (perc.isFull(x, y))
                StdOut.print(" true, ");
            else 
                StdOut.print(" false, ");

            if (perc.percolates()) 
                StdOut.println("percolate !");
            else 
                StdOut.println("not percolate !!!");
        }       
    }
}