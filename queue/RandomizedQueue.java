/****************************************************************************
 *  Compilation:  javac RandomizedQueue.java
 *  
 *  A randomized queue using resizing array. (2nd version)
 *  
 *  enqueue: add to the last, then swap with a random select item
 *  dequeue and iterator: same as queue
 *
 ****************************************************************************/
import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private Item[] q;       // queue elements
    private int N = 0;      // number of elements of queue
    private int first = 0;  // index of first element
    private int last = 0;   // index of next available slot

    public RandomizedQueue() {  // construct an empty queue
        q = (Item[]) new Object[4];
    }

    public boolean isEmpty() {  // is the queue empty ?
        return N == 0;
    }

    public int size() {     // return number of items of queue
        return N;
    }

    private void resize(int max) {  // resize the array
        assert max >= N;
        Item[] temp = (Item[]) new Object[max];
        for (int i = 0; i < N; i++) {
            temp[i] = q[(first + i) % q.length];
        }
        q = temp;
        first = 0;
        last = N;
    }

    private void swap(int i, int j) {
        Item temp = q[i];
        q[i] = q[j];
        q[j] = temp;
    }

    // add the item to last, then swap with random selected item
    public void enqueue(Item item) {    // add the item
        if (item == null) {
            throw new NullPointerException();
        }

        if (N == q.length) resize(2*q.length);
        q[last++] = item;
        if (last == q.length) last = 0;
        N++;

        if (N >= 2) {
            int num = StdRandom.uniform(N);
            int index = (first + num) % q.length;
            if (last == 0)
                swap(index, q.length - 1);
            else
                swap(index, last - 1);
        }
    }

    public Item dequeue() { // delete and return a random item
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        Item item = q[first];
        q[first] = null;
        N--;
        first++;
        if (first == q.length) first = 0;

        if (N > 0 && N == q.length/4) resize(q.length/2);
        return item;
    }

    public Item sample() {  // return a random item
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        int num = StdRandom.uniform(N);
        Item item = q[(first + num) % q.length];
        return item;
    }

    public Iterator<Item> iterator() {
        return new RandomQueueIterator();
    }

    private class RandomQueueIterator implements Iterator<Item> {
        private int i = 0;

        public boolean hasNext() {
            return i < N;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Item item = q[(first + i) % q.length];
            i++;
            return item;
        }
    }

    public static void main(String[] args) {
        int k = Integer.parseInt(args[0]);
        String[] a = StdIn.readAllStrings();
        int N = a.length;
        RandomizedQueue<String> q = new RandomizedQueue<String>();

        for (String item: a) {
            q.enqueue(item);
        }

        for (int i = 0; i < k; i++) {
            StdOut.println(q.dequeue());
        }
    }
}