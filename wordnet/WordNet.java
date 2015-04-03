/*************************************************************************
 *  Compilation:  javac WordNet.java
 *  Execution:    java WordNet 
 *
 *  Dependencies: Digraph.java, SAP.java 
 *
 *  To build the wordnet digraph: each vertex v is an integer that represents 
 * a synset, and each directed edge v→w represents that w is a hypernym of v. 
 * The wordnet digraph is a rooted DAG: it is acyclic and has one vertex—the 
 * root—that is an ancestor of every other vertex. However, it is not necessarily 
 * a tree because a synset can have more than one hypernym. 
 *
 *  The input files are in CSV format: each line contains a sequence of fields, 
 * separated by commas. 
 *
 * - List of noun synsets. The file synsets.txt lists all the (noun) synsets 
 * in WordNet. The first field is the synset id (an integer), the second field 
 * is the synonym set (or synset), and the third field is its dictionary  
 * definition (or gloss). 
 * 
 * - List of hypernyms. The file hypernyms.txt contains the hypernym relationships: 
 * The first field is a synset id; subsequent fields are the id numbers of the 
 * synset's hypernyms.
 * 
 *************************************************************************/
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class WordNet {
    private final SAP sap;
    private final HashMap<String, Bag<Integer>> nounsTable;
    private final List<String> synset;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        nounsTable = new HashMap<String, Bag<Integer>>();
        synset = new ArrayList<String>();

        readSynsets(synsets);
        Digraph digraph = readHypernyms(hypernyms);

        if (!isRootedDAG(digraph)) {
            throw new IllegalArgumentException();
        }

        sap = new SAP(digraph);
    }

    private void readSynsets(String synsetsFile) {
        In in = new In(synsetsFile);
        Bag<Integer> bag;

        while (in.hasNextLine()) {
            String[] a = in.readLine().split(",");
            int id = Integer.parseInt(a[0]);
            synset.add(a[1]);
            
            for (String noun : a[1].split(" ")) {
                bag = nounsTable.get(noun);
                if (bag == null) {
                    bag = new Bag<Integer>();
                    bag.add(id);
                    nounsTable.put(noun, bag);
                } else {
                    bag.add(id);
                }
            }
        }
    }

    private Digraph readHypernyms(String hypernymsFile) {
        Digraph graph = new Digraph(synset.size());
        In in = new In(hypernymsFile);

        while (in.hasNextLine()) {
            String[] a = in.readLine().split(",");
            int origin = Integer.parseInt(a[0]);
            for (int i = 1; i < a.length; i++) {
                graph.addEdge(origin, Integer.parseInt(a[i]));
            }
        }
        return graph;
    }

    private boolean isRootedDAG(Digraph graph) {
        DirectedCycle directedCycle = new DirectedCycle(graph);
        if (directedCycle.hasCycle()) {
            return false;
        }

        int rootNum = 0;
        for (int i = 0; i < graph.V(); i++) {
            if (!graph.adj(i).iterator().hasNext()) {
                rootNum++;
            }
        }
        if (rootNum != 1) {
            return false;
        }

        return true;
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nounsTable.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
            throw new NullPointerException();
        }
        return nounsTable.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (isNoun(nounA) && isNoun(nounB)) {
            return sap.length(nounsTable.get(nounA), nounsTable.get(nounB));
        } else {
            throw new IllegalArgumentException();
        }
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (isNoun(nounA) && isNoun(nounB)) {
            return synset.get(sap.ancestor(nounsTable.get(nounA), nounsTable.get(nounB)));
        } else {
            throw new IllegalArgumentException();
        }
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);

        while (!StdIn.isEmpty()) {
            String nounA = StdIn.readString();
            String nounB = StdIn.readString();

            if (wordnet.isNoun(nounA) && wordnet.isNoun(nounB)) {
                int dist = wordnet.distance(nounA, nounB);
                String ancestor = wordnet.sap(nounA, nounB);
                StdOut.printf("distance = %d, ancestor = %s\n", dist, ancestor);
            } else {
                StdOut.println(nounA + " and/or " + nounB + " is not a noun!");
            }
        }
    }
}