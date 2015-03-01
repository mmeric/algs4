/*************************************************************************
 *  Compilation:  javac Board.java
 *  Execution:    java Board puzzle01.txt
 *
 *  Takes the name of a file as a command-line argument.
 *  Reads in an integer N followed by N x N int between 0 ~ N*N - 1
 *
 *************************************************************************/

public class Board {
    private int[][] tiles;      // board state
    private int N;              // N * N total size
    private int zeroX, zeroY;   // zero position 

// construct a board from an N-by-N array of blocks (where blocks[i][j] = block in row i, column j)
    public Board(int[][] blocks) {
        N = blocks.length;  
        tiles = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                tiles[i][j] = blocks[i][j];
                if (blocks[i][j] == 0) {    // zero position
                    zeroX = i;
                    zeroY = j;
                }
            }
        }
    }

    public int dimension() {   // board dimension N
        return N;
    }

    public int hamming() {  // number of blocks out of place
        int outNum = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int num = serialPos(i, j);
                if ((tiles[i][j] != num) && (num < N*N)) {
                    outNum++;
                }
            }
        }
        return outNum;
    }

    // helper
    private int serialPos(int x, int y) {  // return serial position from (x, y)
        return x*N + y + 1;
    }

    private int getX(int serial) {   // get x (row) from serial position
        return (serial - 1) / N;
    } 

    private int getY(int serial) {  // get y (column) from serial position
        return (serial - 1) % N;
    }

    public int manhattan() {    // sum of Manhattan distances between blocks and goal
        int manDist = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (tiles[i][j] == 0) {   // blank
                    continue;
                }
                manDist += Math.abs(i - getX(tiles[i][j])) + Math.abs(j - getY(tiles[i][j]));
            }
        }
        return manDist;
    }

    public boolean isGoal() {    // is this board the goal board?
        return hamming() == 0;
    }

// a board that is obtained by exchanging two adjacent blocks in the same row
    public Board twin() {   
        int[][] twin = clone2Darray(this.tiles);

        int i = zeroX;
        int j = zeroY, oj;
        if (i < N - 1) {    // down
            i = zeroX + 1;
        } else {
            i = zeroX - 1;  // up
        }
        if (j < N - 1) {
            oj = j + 1;     // right
        } else {
            oj = j - 1;     // left 
        }
        swap(twin, i, j, i, oj);
        return new Board(twin);
    }

    private int[][] clone2Darray(int[][] src) {  // clone 2D array
        int[][] target = new int[N][N];
        for (int i = 0; i < N; i++) {
            System.arraycopy(src[i], 0, target[i], 0, N);
        }
        return target;
    }

    public boolean equals(Object y) {   // does this board equal y?
        if (y == this) return true;
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;
        Board that = (Board) y;
        if (this.dimension() != that.dimension()) return false;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (this.tiles[i][j] != that.tiles[i][j]) return false;
            }
        }
        return true;
    }

    public Iterable<Board> neighbors() {    // all neighboring boards
        Queue<Board> boards = new Queue<Board>();

        if (zeroX > 0) {    // up
            int[][] blocks = clone2Darray(this.tiles);
            swap(blocks, zeroX, zeroY, zeroX - 1, zeroY);
            boards.enqueue(new Board(blocks));
        }
        if (zeroX < N - 1) {    // down 
            int[][] blocks = clone2Darray(this.tiles);
            swap(blocks, zeroX, zeroY, zeroX + 1, zeroY);
            boards.enqueue(new Board(blocks));
        }
        if (zeroY > 0) {    // left
            int[][] blocks = clone2Darray(this.tiles);
            swap(blocks, zeroX, zeroY, zeroX, zeroY - 1);
            boards.enqueue(new Board(blocks));
        }
        if (zeroY < N - 1) {    // right
            int[][] blocks = clone2Darray(this.tiles);
            swap(blocks, zeroX, zeroY, zeroX, zeroY + 1);
            boards.enqueue(new Board(blocks));
        }

        return boards;
    }

    // swap 2D blocks between (x,y) and (i,j)
    private void swap(int[][] blocks, int x, int y, int i, int j) {
        int temp = blocks[x][y];
        blocks[x][y] = blocks[i][j];
        blocks[i][j] = temp;
    }
    
    public String toString() {  // string representation of this board 
        StringBuilder s = new StringBuilder();
        s.append(N + "\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s.append(String.format("%2d ", tiles[i][j])); 
            }
            s.append("\n");
        }
        return s.toString();
    }

    public static void main(String[] args) {    // unit tests
        // read in the input
        In in = new In(args[0]);
        int N = in.readInt();

        int[][] blocks = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                blocks[i][j] = in.readInt();
            }
        }
        
        Board initial = new Board(blocks);
        StdOut.println("---- initial board ----");
        StdOut.println(initial + "hamming = " + initial.hamming() 
            + ", manhattan : " + initial.manhattan() + "\n");

        StdOut.println("---- twin board ---- ");
        StdOut.println(initial.twin() + "hamming = " + initial.twin().hamming() 
            + ", manhattan : " + initial.twin().manhattan() + "\n");

        StdOut.println("---- neighboring boards ---- ");
        for (Board bd : initial.neighbors()) {
            StdOut.println(bd + "hamming = " + bd.hamming() + ", manhattan = " + bd.manhattan() + "\n");
        }
    }
}