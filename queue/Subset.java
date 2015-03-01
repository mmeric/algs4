/****************************************************************************
 *  Compilation:  javac Subset.java
 *  
 *  Write a client program Subset.java that takes a command-line integer k; 
 *  reads in a sequence of N strings from standard input using StdIn.readString(); 
 *  and prints out exactly k of them, uniformly at random. Each item from the 
 *  sequence can be printed out at most once. You may assume that 0 ≤ k ≤ N, 
 *  where N is the number of string on standard input.
 *  
 *  % echo A B C D E F G H I | java Subset 3       
 *  C                                              
 *  G                                              
 *  A                                              
 *                                                  
 *  % echo A B C D E F G H I | java Subset 3       
 *  E                                              
 *  F                                              
 *  G                                              
 *
 ****************************************************************************/

public class Subset {
    public static void main(String[] args) {
        int k = Integer.parseInt(args[0]);
        String[] a = StdIn.readAllStrings();
        RandomizedQueue<String> q = new RandomizedQueue<String>();

        for (String item: a) {
            q.enqueue(item);
        } 

        for (int i = 0; i < k; i++) {
            StdOut.println(q.dequeue());
        }
    }
}