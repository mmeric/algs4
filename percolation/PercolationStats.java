/****************************************************************************
 *  Compilation:  javac PercolationStats.java
 *  Execution:  java PercolationStats N T
 *  Dependencies: StdIn.java StdOut.java
 *
 *  Perform T independent computational experiments on an N-by-N grid.
 *
 ****************************************************************************/

public class PercolationStats {
    private double[] attemps;

    // perform T independent experiments on an N-by-N grid
    public PercolationStats(int N, int T) { 
        if (N < 1 || T < 1) throw new IllegalArgumentException();
        attemps = new double[T];
        for (int i = 0; i < T; i++) {
            Percolation perc = new Percolation(N);
            int count = 0;
            while (!perc.percolates()) {
                int row, col;
                do {
                    row = StdRandom.uniform(N) + 1;
                    col = StdRandom.uniform(N) + 1;
                } while(perc.isOpen(row, col));
                perc.open(row, col);
                count++;            
            } 
            attemps[i] = (1. * count) / (N*N);
        }
    }

    public double mean() {  // sample mean of percolation threshold
        return StdStats.mean(attemps);
    }

    public double stddev() {  // sample standard deviation of percolation threshold
        return StdStats.stddev(attemps);
    }

    public double confidenceLo() {  // low endpoint of 95% confidence interval
        return mean()-((1.96*stddev())/Math.sqrt(attemps.length));
    }

    public double confidenceHi() {  // high endpoint of 95% confidence interval
        return mean()+((1.96*stddev())/Math.sqrt(attemps.length));
    }

    public static double timeTrial(int N, int T) {
        Stopwatch timer = new Stopwatch();
        PercolationStats ps = new PercolationStats(N, T);
        return timer.elapsedTime();
    }

    public static void main(String[] args) {  // test client 
        if (args.length < 2) throw new IllegalArgumentException();
        int N = Integer.parseInt(args[0]);
        int T = Integer.parseInt(args[1]);

        PercolationStats ps = new PercolationStats(N, T);
        StdOut.println("mean                    = " + ps.mean());
        StdOut.println("stddev                  = " + ps.stddev());
        StdOut.println("95% confidence interval = " + ps.confidenceLo() + ", " 
                                                    + ps.confidenceHi()); 
    }
}