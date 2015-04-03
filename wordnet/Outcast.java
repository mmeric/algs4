/*************************************************************************
 *  Compilation:  javac Outcast.java
 *  Execution:    java Outcast synsets.txt hyperhyms.txt outcast5.txt
 *
 *  Measuring the semantic relatedness of two nouns. 
 * -- Semantic relatedness refers to the degree to which two concepts are related.
 * -- We define the semantic relatedness of two wordnet nouns A and B as follows:
 *    distance(A, B) = distance is the minimum length of any ancestral path 
 *                   between any synset v of A and any synset w of B.
 *
 *  Outcast detection. 
 * -- Given a list of wordnet nouns A1, A2, ..., An, which noun is the least 
 * related to the others? 
 * -- To identify an outcast, compute the sum of the distances between
 * each noun and every other one:
 *      di = dist(Ai, A1) + dist(Ai, A2) + ... + dist(Ai, An)
 * and return a noun At for which dt is maximum.
 *
 *************************************************************************/

public class Outcast {
    private WordNet wordnet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    } 

    // given an array of WordNet nouns, return an outcast       
    public String outcast(String[] nouns) {
       String outcastWord = "";
        int maxDistance = 0;

        for (String nounA : nouns) {
            int d = 0;
            for (String nounB : nouns) {
                if (!nounA.equals(nounB)) {
                    d += wordnet.distance(nounA, nounB);
                }
            }

            if (d > maxDistance) {
                maxDistance = d;
                outcastWord = nounA;
            }
        }

        return outcastWord; 
    }

    // test client   
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }  
}
