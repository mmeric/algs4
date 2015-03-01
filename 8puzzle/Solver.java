/*************************************************************************
 *  Compilation:  javac Solver.java
 *  Execution:    java Solver puzzle01.txt
 *
 *  implement the A* algorithm using MinPQ data type
 *
 *************************************************************************/

import java.util.Iterator;
import java.util.Comparator;

public class Solver {
    private int ansMoves;
    private SearchNode ansLastNode;

    private class SearchNode {
        private Board board;
        private int moves;
        private SearchNode prev;

        void setBoard(Board b) {    // accessor method
            board = b;
        }

        void setMoves(int v) {
            moves = v;
        }

        void setPrevNode(SearchNode sn) {
            prev = sn;
        }
    }

    // find a solution to the initial board (using the A* algorithm)
    public Solver(final Board initial) {  
        ansMoves = -1;
        ansLastNode = null;

        MinPQ<SearchNode> startSearch = initSearchNodes(initial);
        MinPQ<SearchNode> twinSearch = initSearchNodes(initial.twin());

        while (!startSearch.isEmpty() && !twinSearch.isEmpty()) {
            SearchNode startMin = startSearch.delMin();
            SearchNode twinMin = twinSearch.delMin();
            if (startMin.board.isGoal()) {
                this.ansMoves = startMin.moves;
                this.ansLastNode = startMin;
                return;
            } else if (twinMin.board.isGoal()) {
                this.ansMoves = -1;
                this.ansLastNode = null;
                return;
            } else {
                neighboringSearch(startSearch, startMin);
                neighboringSearch(twinSearch, twinMin);
            }
        }
    }

    private class PrioritySearch implements Comparator<SearchNode> {
        public int compare(SearchNode sn1, SearchNode sn2) {
            int priority1 = sn1.board.manhattan() + sn1.moves;
            int priority2 = sn2.board.manhattan() + sn2.moves;
            if (priority1 < priority2) {
                return -1;
            } else if (priority1 == priority2) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    private MinPQ<SearchNode> initSearchNodes(Board init) {
        if (init == null) {
            throw new NullPointerException();
        }
        PrioritySearch priorityOrder = new PrioritySearch();
        MinPQ<SearchNode> openNodes = new MinPQ<SearchNode>(priorityOrder); 
        SearchNode firstNode = new SearchNode();
        firstNode.setBoard(init);
        firstNode.setMoves(0);
        firstNode.setPrevNode(null);
        openNodes.insert(firstNode);
        return openNodes;
    }

    private void neighboringSearch(MinPQ<SearchNode> openNodes, SearchNode curNode) {
        Iterable<Board> iterable = curNode.board.neighbors();
        Iterator<Board> iter = iterable.iterator();
        while (iter.hasNext()) {
            Board curBoard = iter.next();
            if (curNode.prev == null  || !curBoard.equals(curNode.prev.board)) {
                SearchNode newNode = new SearchNode();
                newNode.setBoard(curBoard);
                newNode.setMoves(curNode.moves + 1);
                newNode.setPrevNode(curNode);                
                openNodes.insert(newNode);
            }
        }
    }

    public boolean isSolvable() {   // is the initial board solvable?
        return ansMoves != -1;
    }

    public int moves() {    // min number of moves to solve initial board; -1 if unsolvable
        return ansMoves;
    }

    public Iterable<Board> solution() { // sequence of boards in a shortest solution; null if unsolvable
        if (isSolvable()) {
            return new SolutionIterable();
        } else {
            return null;
        }
    }

    private class SolutionIterable implements Iterable<Board> {
        public Iterator<Board> iterator() {
            return new SolutionIterator();
        }
    }

    private class SolutionIterator implements Iterator<Board> {
        private Stack<Board> solutSeq;

        private SolutionIterator() {
            solutSeq = new Stack<Board>();
            SearchNode curNode = ansLastNode;
            while (curNode != null) {
                solutSeq.push(curNode.board);
                curNode = curNode.prev;
            }
        }

        public boolean hasNext() {
            return !solutSeq.isEmpty();
        }

        public Board next() {
            return solutSeq.pop();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public static void main(String[] args) {    // solve a slider puzzle
        // create initial board from file
        In in = new In(args[0]);
        int N = in.readInt();
        int[][] blocks = new int[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}