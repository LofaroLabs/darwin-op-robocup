package sim.app.horde.classifiers.decisiontree;
import sim.app.horde.classifiers.*;
import ec.util.*;
import java.io.*;
import java.util.*;

/** A continuous node in a decision tree, that is, one whose split attribute is a metric range of values rather than a set of categories. */
public class ToroidalNode extends ContinuousNode
    {
    private static final long serialVersionUID = 1;
    
    /** The second split point for the continuous range used. */
    double split2;  // >= split and <= split2 or "not"

    /** I needed this procedure -- khaled **/
    public double getSplit2() { return this.split2; }

    /** Produces a subtree rooted by a ToroidalNode, given the domain, attribute to split on, and a default leaf to use
        if the split isn't very good. In turn calls learn(...) to make each of the subtrees.  The subtree will not exceed maxDepth. */
    public static Node provideNode(Domain dom, int attr, Example[] examples, int[] attributes, int maxDepth, int depth, Leaf defaultLeaf, MersenneTwisterFast random)
        {
        Example[][] spexamples = split(dom, attr, examples, random);
                
        if (spexamples == null)  // no valid split, generate a leaf
            return new Leaf(dom, examples, new Example[0]);
                
        // sanity check -- shouldn't have empty splits on either side
        if (spexamples[0].length == 0 || spexamples[1].length == 0)
            throw new RuntimeException("Empty Split on " + 
                    ((spexamples[0].length == 0 && spexamples[1].length == 0) ? "Both sides" :
                    (spexamples[0].length == 0 ? "Inside" : "Outside")));

        if (value(dom, examples, spexamples) < MINIMUM_VALUE) // * Math.pow(2, (depth + 1)))  // uh oh
            {
            System.err.println("Best result was zero information!!!");
            return defaultLeaf;
            }

        return new ToroidalNode(dom, attr, examples, attributes, maxDepth, depth, spexamples, random);
        }

    /** Produces a subtree rooted by a ToroidalNode, given the domain, attribute to split on, and a default leaf to use
        if the split isn't very good. In turn calls learn(...) to make each of the subtrees.  The subtree will not exceed maxDepth. */
    public ToroidalNode(Domain dom, int attr, Example[] examples, int[] attributes, int maxDepth, int depth, Example[][] spexamples, MersenneTwisterFast random)
        {
        super(dom, attr);
        successors = new Node[2];

        split = spexamples[0][0].values[attr];  // extract the min split value from the inside region
        split2 = spexamples[1][0].values[attr];  // extract the min split value from the outer region

        // now the recursive call
        for (int i = 0; i < successors.length; i++)
            {
            successors[i] = learn(dom, spexamples[i],
                (allSameClassification(spexamples[i]) ? except(attributes, attr) : attributes),
                new Leaf(dom, examples, spexamples[i]), maxDepth, depth + 1, random);
            }
        }
    
    public ToroidalNode(Domain dom, int attr){
        super(dom, attr);
        
        }
    
    public ToroidalNode copy() {        
        ToroidalNode toroidalNode = new ToroidalNode(this.dom, this.attribute);
        
        toroidalNode.split2 = this.split2;
        
        successors = new Node[toroidalNode.successors.length];
        for(int i = 0 ; i < successors.length; i++) {
            toroidalNode.successors[i] = this.successors[i].copy();
                
            }
        
        return toroidalNode;
        }

    public static Example[][] split(Domain dom, final int attr, Example[] examples, MersenneTwisterFast random)
        {
        // first sort the examples
        sortExamples(attr, examples);

        // now consider all possible situations
        Example[][] split = new Example[][]
            {
            examples, examples
            };
                        
        // How many splits are there?
        ArrayList<Integer> splitPoints = new ArrayList<Integer>();
        double currentAttributeValue = examples[examples.length - 1].values[attr];
        for (int i = 0; i < examples.length; i++)  // include toroidal split
            {
            if(  currentAttributeValue != examples[i].values[attr])
                {
                splitPoints.add(i);
                currentAttributeValue = examples[i].values[attr];
                }
            }

        if (splitPoints.size() < 2)
            {
            // return empty split;
            return null;
            }
                        
        // gather all the splits
        int splitcount = splitPoints.size() * splitPoints.size() - splitPoints.size();
        double[] gains = new double[splitcount];
        int[] splitpoints = new int[splitcount];                // i
        int[] splitpoints2 = new int[splitcount];               // j    // remember from j to i
        splitcount = 0;
        for (int i : splitPoints)
            {
            for (int j : splitPoints)
                {
                if (i != j) //empty split not admitted
                    {
                    if (i > j)
                        gains[splitcount] = gain(dom, examples, split, j, i);
                    else
                        gains[splitcount] = gain(dom, examples, split, i, j);
                    splitpoints[splitcount] = i;
                    splitpoints2[splitcount] = j;
                    splitcount++;
                    }
                }
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
        int[] goodsplitpoints2 = new int[splitcount];
        splitcount = 0;
        for (int i = 0; i < gains.length; i++)
            {
            if (gains[i] >= avg)
                {
                goodgains[splitcount] = gains[i];
                goodsplitpoints2[splitcount] = splitpoints2[i];
                goodsplitpoints[splitcount] = splitpoints[i];
                if (splitpoints2[i] < splitpoints[i])
                    goodvalues[splitcount] = gains[i] / potential(examples, split, splitpoints2[i], splitpoints[i]);
                else
                    goodvalues[splitcount] = gains[i] / potential(examples, split, splitpoints[i], splitpoints2[i]);
                splitcount++;
                }
            }

        // find the best one that's not infinity
        splitcount = 0;
        double best = Double.NEGATIVE_INFINITY;
        int bestmax = 0;
        int bestmin = 0;
        int count = 0;
        for (int i = 0; i < goodgains.length; i++)
            {
            double val = goodvalues[i];
            if (((val > best) && (count = 1) == 1) || (val == best && random.nextBoolean(1.0 / (++count))))
                {

                best = goodvalues[i];

                if (goodsplitpoints2[i] > goodsplitpoints[i])// we set it to be bestmin <= x < bestmax
                    {
                    bestmin = goodsplitpoints[i]; //FIXME goodplitpoints not set!!!
                    bestmax = goodsplitpoints2[i];
                    }
                else
                    {
                    bestmin = goodsplitpoints2[i];
                    bestmax = goodsplitpoints[i];
                    }

                }
            }


        // build the split
        Example[][] bestSplit = new Example[2][];
        bestSplit[0] = new Example[bestmax - bestmin];  // inside region
        System.arraycopy(examples, bestmin, bestSplit[0], 0, bestSplit[0].length);

        // build the second split
        bestSplit[1] = new Example[examples.length - bestSplit[0].length];  // outside regions
        // put the HIGHER section first since we WRAP AROUND into the lower region
        System.arraycopy(examples, bestmax, bestSplit[1], 0, examples.length - bestmax);  // upper outer region
        System.arraycopy(examples, 0, bestSplit[1], examples.length - bestmax, bestmin);  // lower outer region
        return bestSplit;
        }

    public int classify(Example e, MersenneTwisterFast random)
        {
        return successors[(e.values[attribute] >= split && e.values[attribute] < split2) ? 0 : 1].classify(e, random);
        }

    public double[] provideDistribution(Example e)
        {
        return successors[(e.values[attribute] >= split && e.values[attribute] < split2) ? 0 : 1].provideDistribution(e);
        }

    public String toString()
        {
        return format.format(split) + " <= " + dom.attributes[attribute] + " < " + format.format(split2);
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
        s += getUniqueInteger() + "->" + successors[0].getUniqueInteger() + " [ label=\"In\" ];\n";
        s += getUniqueInteger() + "->" + successors[1].getUniqueInteger() + " [ label=\"Out\" ];\n";
        return s;
        }


    public void write(PrintWriter writer)
        { 
        writer.print(" ( toroidal " + attribute);
        writer.print(" " + split);
        writer.print(" " + split2);
        writer.print(" " + push);               // dunno if we're using this, probably not
        writer.println();
        for (int i = 0; i < successors.length; i++)
            successors[i].write(writer);
        writer.print(" )\n");
        }
                
                
    public ToroidalNode(Scanner scanner, Domain domain)
        { 
        super(scanner, domain);
        attribute = Integer.parseInt(token(scanner));
        split = Double.parseDouble(token(scanner));
        split2 = Double.parseDouble(token(scanner));
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

