/****************************************************************************
 *  Compilation:  javac Deque.java
 *  
 *  A double-ended queue or deque("deck") using doubly linked list.
 *  
 *
 ****************************************************************************/
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {
    private int N = 0;          // number of elements
    private DNode<Item> first;   // front of deque 
    private DNode<Item> last;    // end of deque

    private static class DNode<Item> {   // helper doubly linked list class
        private Item item;
        private DNode<Item> prev;
        private DNode<Item> next;
    }
  
    public Deque() {    // construct an empty deque
        first = null;
        last = null;
        N = 0;
    }

    public boolean isEmpty() {  // is the deque empty ?
        return N == 0;
    }

    public int size() { // return the number of items on the deque
        return N;
    }

    public void addFirst(Item item) {   // add item to the front
        if (item == null) {
            throw new NullPointerException();
        }
        DNode<Item> oldfirst = first;
        first = new DNode<Item>();
        first.item = item;
        first.prev = null;
        first.next = null;
        if (isEmpty()) {
            last = first;
        }
        else {
            first.next = oldfirst;
            oldfirst.prev = first;
        }
        N++;
    }

    public void addLast(Item item) {    // add item to the end
        if (item == null) {
            throw new NullPointerException();
        }
        DNode<Item> oldlast = last;
        last = new DNode<Item>();
        last.item = item;
        last.prev = null;
        last.next = null;
        if (isEmpty()) {
            first = last;
        }
        else {
            oldlast.next = last;
            last.prev = oldlast;
        }
        N++;
    }

    public Item removeFirst() { // remove and return first item
        if (isEmpty()) {
            throw new NoSuchElementException("Deque underflow");
        }

        Item item = first.item;
        first = first.next;
        N--;
        if (isEmpty()) {
            last = null;
        }
        else {
            first.prev = null;
        }
        return item;
    }

    public Item removeLast() {  // remove and return last item
        if (isEmpty()) {
            throw new NoSuchElementException("Deque overflow");
        }

        Item item = last.item;
        last = last.prev;
        N--;
        if (isEmpty()) {
            first = null;
        }
        else {
            last.next = null;
        }
        return item;
    }

    // return an iterator that iterates over the deque
    public Iterator<Item> iterator() {
        return new ListIterator<Item>(first);
    }

    // an iterator
    private class ListIterator<Item> implements Iterator<Item> {
        private DNode<Item> current;

        public ListIterator(DNode<Item> first) {
            current = first;
        }

        public boolean hasNext() {
            return current != null;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Item item = current.item;
            current = current.next;
            return item;
        }
    }

    // unit test
    public static void main(String[] args) {
        Deque<String> myDeque = new Deque<String>();
        int sign = 0;
        while (!StdIn.isEmpty()) {
            String s = StdIn.readString();
            if (s.equals("l+")) sign = 0;
            else if (s.equals("r+")) sign = 1;
            else if (s.equals("l-")) {
                myDeque.removeFirst();
            }
            else if (s.equals("r-")) {
                myDeque.removeLast();
            }
            else {
                if (sign == 0) myDeque.addFirst(s);
                else if (sign == 1) myDeque.addLast(s);
            }
            StdOut.print(s + " (N = " + myDeque.size() + "): ");
        }
        StdOut.println("(" + myDeque.size() + " left on the deque): ");
    }
}