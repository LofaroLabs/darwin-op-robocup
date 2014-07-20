package sim.app.horde.classifiers.decisiontree;
import sim.app.horde.classifiers.*;
import ec.util.*;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.io.*;
import java.util.*;

/** A continuous node in a decision tree, that is, one whose split attribute is a metric range of values rather than a set of categories. */
public class ContinuousNode extends Node
    {

    private static final long serialVersionUID = 1;
    static final DecimalFormat format = new DecimalFormat();

    static
        {
        format.setMaximumFractionDigits(4);
        }
    /** The split point for the continuous range used. */
    double split;  // < split or >= split
    int push = 0;  // how we're pushing the pivot point

    public void setSplit(double s) { split = s; }     
    public double getSplit() { return split; } 
    
    /** Makes a blank ContinuousNode.  Solely for the benefit of ToroidalNode so it can call super() */
    protected ContinuousNode(Domain dom, int attr)
        {
        super(dom, attr);
        }
    
     
    public ContinuousNode copy() {      
        ContinuousNode continuousNode = new ContinuousNode(this.dom, this.attribute);
        continuousNode.split = this.split;
        
        continuousNode.successors = new Node[this.successors.length];
        for(int i = 0 ; i < successors.length; i++) {
            continuousNode.successors[i] = this.successors[i].copy();
                
            }
        
        return continuousNode;
        }

    /** Produces a subtree rooted by a ContinuousNode, given the domain,
     * attribute to split on, and a default leaf to use
     * if the split isn't very good. In turn calls learn(...)
     * to make each of the subtrees.  The subtree will not exceed maxDepth. */
    public static Node provideNode(Domain dom, int attr, Example[] examples, int[] attributes, int maxDepth, int depth, Leaf defaultLeaf, MersenneTwisterFast random)
        {
        Example[][] spexamples = split(dom, attr, examples, random); //FIXME what is the best split in xxx0xxx?
        
        if (spexamples == null)  // no valid split, generate a leaf
            return new Leaf(dom, examples, new Example[0]);

        // sanity check -- shouldn't have empty splits on either side
        if (spexamples[0].length == 0 || spexamples[1].length == 0)
            throw new RuntimeException("Empty Split on " + 
                    ((spexamples[0].length == 0 && spexamples[1].length == 0) ? "Both sides" :
                    (spexamples[0].length == 0 ? "Left Side" : "Right Side")));

        if (value(dom, examples, spexamples) < MINIMUM_VALUE) // * Math.pow(2, (depth + 1)))  // uh oh
            {
            System.err.println("Best result was zero information!!!");
            return defaultLeaf;
            }

        return new ContinuousNode(dom, attr, examples, attributes, maxDepth, depth, spexamples, random);
        }

    /** Produces a subtree rooted by a ContinuousNode, given the domain, attribute to split on, and a default leaf to use
        if the split isn't very good. In turn calls learn(...) to make each of the subtrees.  The subtree will not exceed maxDepth. */
    public ContinuousNode(Domain dom, int attr, Example[] examples, int[] attributes, int maxDepth, int depth, Example[][] spexamples, MersenneTwisterFast random)
        {
        super(dom, attr);
        successors = new Node[2];

        if (spexamples[0].length == 0 || spexamples[1].length == 0)
            throw new RuntimeException("Empty Split!!!");
    
        myExamples = new Example[examples.length];
        for (int i = 0; i < myExamples.length; i++)
            myExamples[i] = (Example) examples[i].clone();
        
                
        if (myExamples == null || myExamples.length == 0)
            throw new RuntimeException("EMPTY myExamples"); 
                
        // do a fancier split that takes into consideration whether the values are default or not.
        // Note that we're presently not doing this for Toroidal but maybe we should.
                
        Example a = spexamples[1][0];
        double aVal = a.values[attr];
        Example b = spexamples[0][spexamples[0].length - 1];
        double bVal = b.values[attr];

        if (a.continuation == b.continuation) // do a half-half split
            {
            push = 0;
            split = (aVal + bVal) / 2.0;
            }
        else if (a.continuation)  // push to the edge of b
            {
            push = -1;
            if (aVal < bVal)  // we should just split on b because it's < vs. >=
                split = bVal;
            else                                                              // we need to split on the value just larger than b (that is, on the a side)
                split = Math.nextAfter(bVal, aVal);
            }
        else // b.continuation
            {
            push = 1;
            if (bVal < aVal)  // we should just split on a because it's < vs. >=
                split = aVal;
            else                                                              // we need to split on the value just larger than a (that is, on the b side)
                split = Math.nextAfter(aVal, bVal);
            }
                
        // end fancier split
        for (int i = 0; i < successors.length; i++)
            {
            if (spexamples[i].length == 0)
                throw new RuntimeException("Split example array is zero.  This should never happen.");
            successors[i] = learn(dom, spexamples[i], (allSameClassification(spexamples[i]) ? except(attributes, attr) : attributes),
                new Leaf(dom, examples, spexamples[i]), maxDepth, depth + 1, random);
            }
        }

    /** Sorts the examples by value in this attribute. */
    protected static void sortExamples(final int attr, final Example[] examples)
        {
        Arrays.sort(examples, new Comparator<Example>()
                {
                public int compare(Example o1, Example o2)
                    {
                    double a = o1.values[attr];
                    double b = o2.values[attr];
                    if (a < b)
                        return -1;
                    if (a > b)
                        return 1;
                    return 0;
                    }

                public boolean equals(Object obj)
                    {
                    return obj == this;
                    }  // worthless junk Java makes you add
                });
        }


    public static void testSplit(Example[][] split, Example[][] test, Example[] ex)
        {
        java.util.HashSet<Example> h = new java.util.HashSet<Example>();
        for (int i = 0; i < ex.length; i++)
            {
            h.add(ex[i]);
            }
        for (int i = 0; i < split[0].length; i++)
            {
            if (h.contains(split[0][i]))
                h.remove(split[0][i]);
            else
                throw new RuntimeException("Bug 2");
            }
        for (int i = 0; i < split[1].length; i++)
            {
            if (h.contains(split[1][i]))
                h.remove(split[1][i]);
            else
                throw new RuntimeException("Bug 2");
            }
        if (!h.isEmpty())
            throw new RuntimeException("Bug 3");
        if (split[0].length != test[0].length)
            throw new RuntimeException("Bug 4: " + split[0].length + " " + test[0].length);
        if (split[1].length != test[1].length)
            throw new RuntimeException("Bug 5");
        for (int i = 0; i < split[0].length; i++)
            {
            if (split[0][i] != test[0][i])
                throw new RuntimeException("Bug 6");
            }
        for (int i = 0; i < split[1].length; i++)
            {
            if (split[1][i] != test[1][i])
                throw new RuntimeException("Bug 7");
            }
        }

    public int getDepth()
        {
        int max=-1; 
        for (int i=0; i < successors.length; i++) 
            {
            int tmp = successors[i].getDepth();
            if (tmp > max) 
                max = tmp; 
            }
        return max+1; 
        }
    
    public Node updateSplit(Example e)
        {
        int idx = e.values[attribute] < split ? 0 : 1;
        if (successors[idx] instanceof Leaf)
            {
            Example[] ex = new Example[myExamples.length + 1];
            ex[0] = (Example) e.clone();
            for (int i = 0; i < myExamples.length; i++)
                ex[i + 1] = (Example) myExamples[i].clone();

            Node n = learn(dom, ex, 10, new MersenneTwisterFast());
            idx = e.values[parent.attribute] < ((ContinuousNode) parent).split ? 0 : 1;
            parent.successors[idx] = n;
            return n;
            }
        else
            return successors[idx].updateSplit(e);

        }

    public void updateNode(Node n, Example e)
        {
        int idx = e.values[attribute] < split ? 0 : 1;
        if (successors[idx] instanceof Leaf)
            successors[idx] = n;
        else
            successors[idx].updateNode(n, e);
        }

    public Node getParent(Example e)
        {
        return successors[e.values[attribute] < split ? 0 : 1].getParent(e);
        }

    
    public Example[] getExamples(Example e)
        {
        return successors[e.values[attribute] < split ? 0 : 1].getExamples(e); 
        }
    
    public int classify(Example e, MersenneTwisterFast random)
        {
        return successors[e.values[attribute] < split ? 0 : 1].classify(e, random);
        }

    public double[] provideDistribution(Example e)
        {
        return successors[e.values[attribute] < split ? 0 : 1].provideDistribution(e);
        }

    public String toString()
        {
        return dom.attributes[attribute] + " < " + (push == -1 ? " # " : "") + split + (push == 1 ? " # " : "") ;
        }

    public String nodeToDot()
        {
        updateUniqueInteger();

        // build the label for me
        String s = "" + getUniqueInteger() + " [ label=\"" + toString() + "\", shape=box ];\n";

        // call my kids -- this also updates their unique integers
        s += successors[0].nodeToDot();
        s += successors[1].nodeToDot();

        //build the labeled edge to children
        s += getUniqueInteger() + "->" + successors[0].getUniqueInteger() + " [ label=\"<\" ];\n";
        s += getUniqueInteger() + "->" + successors[1].getUniqueInteger() + " [ label=\">=\" ];\n";
        return s;
        }

    /** Returns null if there is NO useful split. */
    public static Example[][] split(Domain dom, final int attr, Example[] examples, MersenneTwisterFast random)
        {
        // first sort the examples by attribute
        sortExamples(attr, examples);

        // now consider all possible situations
        Example[][] split = new Example[][]
            {
            examples, examples
            };

        // How many splits are there?  // Modified by Sean
        // to disallow [] and [all] as legal splits
        int splitcount = 0;
        double currentAttributeValue=examples[0].values[attr];

        for (int i = 1; i < examples.length; i++)
            {
            if( currentAttributeValue!=examples[i].values[attr])
                splitcount++;
            currentAttributeValue=examples[i].values[attr];
            }
                        
        if (splitcount==0)
            {
            return null;
            }
       
        // gather all the splits
        double[] gains = new double[splitcount];
        int[] splitpoints = new int[splitcount];
        splitcount = 0;
        currentAttributeValue=examples[0].values[attr];
        for (int i = 1; i < examples.length; i++)
            {
            if(currentAttributeValue!=examples[i].values[attr])
                {
                splitpoints[splitcount] = i;
                gains[splitcount] = gain(dom, examples, split, 0, i);
                splitcount++;
                }
            currentAttributeValue=examples[i].values[attr];
            }

        // compute average gain
        double avg = 0;
        for (int i = 0; i < gains.length; i++)
            {
            avg += gains[i];
            }
        avg /= gains.length;

        // How many splits are of average gain or more?
        splitcount = 0;
        for (int i = 0; i < gains.length; i++)
            {
            if (gains[i] >= avg)
                splitcount++;
            }

        // gather all the splits of average gain or more
        double[] goodgains = new double[splitcount];
        double[] goodvalues = new double[splitcount];
        int[] goodsplitpoints = new int[splitcount];
        splitcount = 0;
        for (int i = 0; i < gains.length; i++)
            {
            if (gains[i] >= avg)
                {
                goodgains[splitcount] = gains[i];
                goodsplitpoints[splitcount] = splitpoints[i];
                goodvalues[splitcount] = gains[i] / potential(examples, split, 0, splitpoints[i]);
                splitcount++;
                }
            }

        // find the best one that's not infinity
        splitcount = 0;
        double best = Double.NEGATIVE_INFINITY;
        int bestmax = 0;
        int count = 0;
        for (int i = 0; i < goodgains.length; i++)
            {
            double val = goodvalues[i];
            if (((val > best) && (count = 1) == 1) || (val == best && random.nextBoolean(1.0 / (++count))))
                {
                best = goodvalues[i];
                bestmax = goodsplitpoints[i];
                }
            }


        // build the split
        Example[][] bestSplit = new Example[2][];
        bestSplit[0] = new Example[bestmax];
        System.arraycopy(examples, 0, bestSplit[0], 0, bestSplit[0].length);
        bestSplit[1] = new Example[examples.length - bestSplit[0].length];
        System.arraycopy(examples, bestmax, bestSplit[1], 0, bestSplit[1].length);
        return bestSplit;
        }


    public void write(PrintWriter writer)
        { 
        writer.print(" ( continuous " + attribute);
        writer.print(" " + split);
        writer.print(" " + push);
        writer.println();
        for (int i = 0; i < successors.length; i++)
            successors[i].write(writer);
        writer.print(" )\n");
        }
                
    public ContinuousNode(Scanner scanner, Domain domain)
        {
        super(scanner, domain);
        attribute = Integer.parseInt(token(scanner));
        split = Double.parseDouble(token(scanner));
        push = Integer.parseInt(token(scanner));
        ArrayList _successors = new ArrayList();
        while(true)
            {
            String token = token(scanner);
            if (token.equals(")")) break;
            _successors.add(read(scanner, domain));
            }
        successors = getNodes(_successors);
        }
    }

