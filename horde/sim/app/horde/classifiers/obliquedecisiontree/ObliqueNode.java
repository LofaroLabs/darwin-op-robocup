package sim.app.horde.classifiers.obliquedecisiontree;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.*;

import ec.util.MersenneTwisterFast;
import sim.app.horde.classifiers.*;
import sim.app.horde.classifiers.decisiontree.*;

public class ObliqueNode extends sim.app.horde.classifiers.decisiontree.Node
    {
    private static final long serialVersionUID = 1L;

    static final DecimalFormat format = new DecimalFormat();

    static
        {
        format.setMaximumFractionDigits(20);
        }
        
    // hyperplane (axis-parallel or oblique) for this node
    private double[] hyperplane;

    // Stagnation probability for random perturbations
    private final static double pStag = 1;
    private static double pMove = pStag;

    // Different types of impurity measures. The paper suggests these
    // three are the best, with TWOING used throughout the experimental section.
    private static enum Impurity
        {
        TWOING, INFORMATION_GAIN, GINI_INDEX
            };

    private static Impurity IMPURITY_TYPE = Impurity.TWOING;

    /** Returns the pathname of a file in the same directory as this class, regardless of operating system. */
    public static String getPathInDirectory(String s)
        {
        return ObliqueNode.class.getResource("").getPath() + "/" + s;
        }

    public ObliqueNode(Domain domain)
        {
        super(domain, -1);
        }

    public ObliqueNode(Domain domain, Example[][] spexamples, int[] attributes, int maxDepth, int depth,
        MersenneTwisterFast random, double[] plane)
        {
        this(domain);

        hyperplane = new double[plane.length];
        System.arraycopy(plane, 0, hyperplane, 0, plane.length);

        // make recursive call
        // successors = new ObliqueNode[2];
        successors = new Node[2];

        for (int i = 0; i < successors.length; i++)
            successors[i] = ObliqueNode.learn(domain, spexamples[i], attributes, new Leaf(dom, spexamples[i],
                    spexamples[i]), maxDepth, depth, random);
        }

    static double[] determineHyperplane(Domain domain, Example[] examples, int[] attributes, MersenneTwisterFast random)
        {

        // determine hyperplane for this node
        int size = domain.attributes.length + 1;
        double plane[] = new double[size];
        for (int i = 0; i < size; i++)
            plane[i] = 0;

        // get an axis-parallel split
        int bestAttribute = chooseAttribute(domain, attributes, examples, random);
        Example[][] tmpExamples = ContinuousNode.split(domain, bestAttribute, examples, random);

        Example a = tmpExamples[1][0];
        double aVal = a.values[bestAttribute];
        Example b = tmpExamples[0][tmpExamples[0].length - 1];
        double bVal = b.values[bestAttribute];

        double split = (aVal + bVal) / 2.0;;
        /*      if (a.continuation == b.continuation) // do a half-half split
                split = (aVal + bVal) / 2.0;

                else if (a.continuation) // push to the edge of b
                {
                if (aVal < bVal) // we should just split on b because it's < vs. >=
                split = bVal;
                else
                // we need to split on the value just larger than b (that is, on the a side)
                split = Math.nextAfter(bVal, aVal);
                }
                else
                // b.continuation
                {
                if (bVal < aVal) // we should just split on a because it's < vs. >=
                split = aVal;
                else
                // we need to split on the value just larger than a (that is, on the b side)
                split = Math.nextAfter(aVal, bVal);
                }
        */
        plane[bestAttribute] = 1;
        plane[plane.length - 1] = -split;

        for (int i=0; i < plane.length-1; i++)
            System.out.print(plane[i] + "*x[" + i + "] + "); 
        System.out.println( + plane[plane.length-1] + " = 0");
                
        // we have enough examples, so try to find an oblique hyperplane
        // threshold for number of examples is from the paper
        if (examples.length > 2 * (size - 1))
            {
            // System.out.println("OBLIQUE");
            double axisImpurity = impurity(plane, examples, domain, attributes, random);

            System.out.println("Axis: " + axisImpurity);
                        
            double R = 20; // Referred to in Section 3.2 as the number of restarts

            for (int i = 0; i < R; i++)
                {
                double randomPlane[] = new double[plane.length];

                // get a random hyperplane
                if (i == 0)
                    System.arraycopy(plane, 0, randomPlane, 0, plane.length);
                else
                    { // -1 <= r_i <= 1
                    double min = -2; 
                    double max = -min; 
                    for (int j = 0; j < plane.length; j++)
                        randomPlane[j] = min + (max - min) * random.nextDouble();
                    }

                double imp = axisImpurity;

                while (true)
                    {
                    // step 1, using a sequential selection procedure
                    int iter = 0;
                    boolean coeffChanged = true;
                    while (coeffChanged)
                        {                       
                        coeffChanged = false; 
                        for (int j = 0; j < randomPlane.length; j++)
                            {
                            // determine all the U's and then sort them
                            double U[] = new double[examples.length];

                            for (int k = 0; k < examples.length; k++)
                                {
                                double V = computeHyperplane(randomPlane, examples[k]);
                                double x = 1;
                                if (j < examples[k].values.length) x = examples[k].values[j];
                                U[k] = randomPlane[j] - (V / x);
                                }

                            // perturb the coefficient
                            double newCoeff = linear_split(U, randomPlane, examples, j, imp, domain, attributes, random);

                            if (randomPlane[j] != newCoeff) // we changed
                                {
                                double tmpCoeff = randomPlane[j]; 
                                randomPlane[j] = newCoeff;
                                                                
                                // check if new coefficient improves impurity 
                                double tmpImp = impurity(randomPlane, examples, domain, attributes, random);
                                if (tmpImp < imp) 
                                    {
                                    coeffChanged = true;
                                    imp = tmpImp; 
                                    }
                                else 
                                    randomPlane[j] = tmpCoeff; 
                                }
                            }
                                                
                        iter++; 
                        if (iter > 500000)
                            break; 
                        }
                                        

                    // step 2
                    boolean foundBetterRandom = false;
                    double J = 5; // magic number from original OC1 paper
                    for (int cnt = 0; cnt < J; cnt++)
                        {
                        // get a random direction to perturb in, -1 <= r_i <= 1 according to code from OC1 author
                        double r[] = new double[randomPlane.length];
                        for (int k = 0; k < r.length; k++)
                            r[k] = -1 + 2 * random.nextDouble(true, true);

                        // solve for alpha
                        double[] U = new double[examples.length];
                        double[] V = new double[examples.length];
                        double[] ran = new double[examples.length];

                        for (int k = 0; k < U.length; k++)
                            {
                            V[k] = computeHyperplane(randomPlane, examples[k]);
                            ran[k] = computeHyperplane(r, examples[k]);
                            U[k] = -V[k] / ran[k];
                            }

                        double alpha = linear_split(U, randomPlane, examples, 0, imp, domain, attributes, random);

                        // compute new hyperplane
                        double tmpPlane[] = new double[randomPlane.length];
                        for (int k = 0; k < r.length; k++)
                            tmpPlane[k] = alpha * ran[k] + V[k];

                        double tmpImp = impurity(tmpPlane, examples, domain, attributes, random);
                        if (tmpImp < imp)
                            {
                            // found a better plane, so try deterministic perturbations again (step 1)
                            imp = tmpImp;
                            System.arraycopy(tmpPlane, 0, randomPlane, 0, randomPlane.length);
                            foundBetterRandom = true;
                            break;
                            }
                        }
                    if (foundBetterRandom) // back to step 1
                        continue;
                    else
                        // all done
                        break;
                    }

                // check if we found a better hyperplane
                double tmp = impurity(randomPlane, examples, domain, attributes, random);

                // System.out.println("Step 2: " + tmp);

                if (tmp <= imp)
                    {
                    imp = tmp;
                    System.arraycopy(randomPlane, 0, plane, 0, randomPlane.length);
                    }
                }
            }

        return plane;
        }

    public static Node provideNode(Domain domain, Example[] examples, int[] attributes, int maxDepth, int depth,
        Leaf defaultLeaf, MersenneTwisterFast random)
        {

        double[] plane = determineHyperplane(domain, examples, attributes, random);

        Example[][] spexamples = split(examples, domain, attributes, random, plane);

                
        /*if (spexamples != null)
          {
          System.out.println("Left");
          for (int i = 0; i < spexamples[0].length; i++)
          System.out.print(computeHyperplane(plane, spexamples[0][i]) + " => " + spexamples[0][i]);
          System.out.println("Right");
          for (int i = 0; i < spexamples[1].length; i++)
          System.out.print(computeHyperplane(plane, spexamples[1][i]) + " => " + spexamples[1][i]);

          System.out.println("*********");
          }
          else
          System.out.println("NO GOOD SPLIT");
        */
        if (spexamples == null) // no good split
            return new Leaf(domain, examples, examples);

        for (int i = 0; i < plane.length; i++)
            System.out.print(plane[i] + " ");
        System.out.println(" => " + impurity(plane, examples, domain, attributes, random));
        System.out.println(examples.length + " => " + spexamples[0].length + " " + spexamples[1].length); 
                
        return new ObliqueNode(domain, spexamples, attributes, maxDepth, depth, random, plane);
        }

    static public Example[][] split(Example[] examples, Domain domain, int[] attributes, MersenneTwisterFast random,
        double[] plane)
        {
        ArrayList<Example> leftExamples = new ArrayList<Example>();
        ArrayList<Example> rightExamples = new ArrayList<Example>();

        for (int i = 0; i < examples.length; i++)
            {
            if (computeHyperplane(plane, examples[i]) < 0)
                leftExamples.add(examples[i]);
            else
                rightExamples.add(examples[i]);
            }

        // test for an empty split
        if (leftExamples.size() == 0 || rightExamples.size() == 0)
            return null;

        Example[][] bestSplit = new Example[2][];
        bestSplit[0] = new Example[leftExamples.size()];
        bestSplit[1] = new Example[rightExamples.size()];
        System.arraycopy(leftExamples.toArray(), 0, bestSplit[0], 0, bestSplit[0].length);
        System.arraycopy(rightExamples.toArray(), 0, bestSplit[1], 0, bestSplit[1].length);
        return bestSplit;
        }

    public Object clone()
        {
        return copy();
        }

    public ObliqueNode copy()
        {
        ObliqueNode n = new ObliqueNode(dom);
        n.successors = new Node[successors.length];
        for (int i = 0; i < successors.length; i++)
            n.successors[i] = successors[i].copy();

        n.hyperplane = new double[hyperplane.length];
        System.arraycopy(hyperplane, 0, n.hyperplane, 0, hyperplane.length);

        return n;
        }

    protected static double impurity(double[] plane, Example[] examples, Domain domain, int[] attributes,
        MersenneTwisterFast random)
        {

        Example[][] spexamples = split(examples, domain, attributes, random, plane);

        if (spexamples == null) return Double.POSITIVE_INFINITY;

        // check if we have a perfect split 
        if (allSameClassification(spexamples[0]) && allSameClassification(spexamples[1]))
            return 0; 
                
        double TL = spexamples[0].length;
        double TR = spexamples[1].length;
        double[] leftCnt = buildClassCounts(domain, spexamples[0]);
        double[] rightCnt = buildClassCounts(domain, spexamples[1]);

        double returnValue = -1;
        switch (IMPURITY_TYPE)
            {
            case TWOING:
                double tmp = 0;
                for (int i = 0; i < leftCnt.length; i++)
                    tmp += Math.abs(leftCnt[i] / TL - rightCnt[i] / TR);

                        
                returnValue = (TL / examples.length) * (TR / examples.length) * tmp * tmp ;
                if (returnValue ==0 ) 
                    returnValue = Double.POSITIVE_INFINITY; 
                else returnValue = 1.0/returnValue; 
                break;
            case INFORMATION_GAIN:
                returnValue = 1.0 / gain(domain, examples, spexamples);
                break;
            case GINI_INDEX:
                double tmpLeft = 0;
                double tmpRight = 0;

                for (int i = 0; i < leftCnt.length; i++)
                    {
                    tmpLeft += (leftCnt[i] / TL) * (leftCnt[i] / TL);
                    tmpRight += (rightCnt[i] / TR) * (rightCnt[i] / TR);
                    }
                returnValue = (spexamples[0].length * (1.0 - tmpLeft) + spexamples[1].length * (1.0 - tmpRight))
                    / examples.length;
                break;
            }

        return returnValue;
        }

    public String toString()
        {
        String str = "";

        for (int i = 0; i < hyperplane.length - 1; i++)
            {
            if (i != 0)
                {
                if (hyperplane[i] >= 0)
                    str += " + ";
                else
                    str += " - ";
                }
            else if (hyperplane[0] < 0) str += "-";
            str += format.format(Math.abs(hyperplane[i])) + "*" + dom.attributes[i];

            }

        if (hyperplane[hyperplane.length - 1] >= 0)
            str += " + ";
        else
            str += " - ";
        str += format.format(Math.abs(hyperplane[hyperplane.length - 1])) + " < 0";

        return str;
        }

    double computeHyperplane(Example e)
        {
        return computeHyperplane(hyperplane, e);
        }

    static double computeHyperplane(double[] plane, Example e)
        {
        if (plane == null) return 0;

        double val = plane[plane.length - 1];
        for (int i = 0; i < plane.length - 1; i++)
            val += plane[i] * e.values[i];
        return val;
        }

    public Node updateSplit(Example e)
        {
        return null; 
        }

    public void updateNode(Node n, Example e)
        {
        double val = computeHyperplane(e);

        int idx = val < 0 ? 0 : 1;
        if (successors[idx] instanceof Leaf)
            successors[idx] = n;
        else
            successors[idx].updateNode(n, e);
        }

    public Node getParent(Example e)
        {
        double val = computeHyperplane(e);
        return successors[val < 0 ? 0 : 1].getParent(e);
        }
    
    public Example[] getExamples(Example e)
        {
        double val = computeHyperplane(e);
        return successors[val < 0 ? 0 : 1].getExamples(e); 
        }
        
    public int classify(Example e, MersenneTwisterFast random)
        {
        double val = computeHyperplane(e);
        return successors[val < 0 ? 0 : 1].classify(e, random);
        }

    public double[] provideDistribution(Example e)
        {
        double val = computeHyperplane(e);
        return successors[val < 0 ? 0 : 1].provideDistribution(e);
        }

    public String nodeToDot()
        {
        updateUniqueInteger();

        // build the label for me
        String s = "" + getUniqueInteger() + " [ label=\"" + toString() + "\", shape=box ];\n";

        // call my kids -- this also updates their unique integers
        s += successors[0].nodeToDot();
        s += successors[1].nodeToDot();

        // build the labeled edge to children
        s += getUniqueInteger() + "->" + successors[0].getUniqueInteger() + " [ label=\"<\" ];\n";
        s += getUniqueInteger() + "->" + successors[1].getUniqueInteger() + " [ label=\">=\" ];\n";
        return s;
        }

    public void write(PrintWriter writer)
        {
        writer.print(" ( oblique ");
        writer.print(toString());
        writer.println();
        for (int i = 0; i < successors.length; i++)
            successors[i].write(writer);
        writer.print(" )\n");

        }

    /**
     * Perturbation algorithm from the paper. See Figure 8. Returns the potentially new value for plane[m]
     */
    public static double linear_split(double[] U, double[] plane, Example[] examples, int m, double currentImpurity,
        Domain domain, int[] attributes, MersenneTwisterFast random)
        {
        Arrays.sort(U);

        // get best split of the U's
        double bestImp = Double.POSITIVE_INFINITY;
        double bestCoeff = Double.POSITIVE_INFINITY;

        double tmpPlane[] = new double[plane.length];
        System.arraycopy(plane, 0, tmpPlane, 0, plane.length);

        int from, to;
        for (int i = 0; i < U.length - 1; i++)
            {
            // get two different values from U
            from = i;
            for (to = from + 1; to < U.length - 1 && U[to] == U[from]; to++);
            double a = (U[to] - U[from]) / 2;

            i = to;

            double tmp = tmpPlane[m]; 
            tmpPlane[m] = a;
            double tmpImp = impurity(tmpPlane, examples, domain, attributes, random);
            if (tmpImp < bestImp)
                {
                bestImp = tmpImp;
                bestCoeff = a;
                }
            tmpPlane[m] = tmp; 
            }

        // see if replacing plane with bestPlane gets us anything
        if (bestImp > currentImpurity)
            return plane[m];
        else if (bestImp < currentImpurity)
            {
            pMove = pStag;
            return bestCoeff;
            }
        else
            // if (bestImp == currentImpurity)
            {
            if (random.nextBoolean(pMove))
                {
                pMove = pMove - 0.1 * pStag;
                return bestCoeff;
                }
            else
                return plane[m];
            }
        }

    /** Builds a tree based on the given examples. The tree will not exceed the maximum depth. */
    public static Node learn(Domain domain, Example[] examples, int maxDepth, MersenneTwisterFast random)
        {
        // initialize with all attributes
        int[] attributes = new int[domain.attributes.length];
        for (int i = 0; i < attributes.length; i++)
            attributes[i] = i;

        // we throw an exception here so we can pass in null at the top level for defaultLeaf
        if (examples.length == 0) throw new RuntimeException("Top-level tree learned with empty examples!");
        return ObliqueNode.learn(domain, examples, attributes, new Leaf(domain, examples, examples), maxDepth, 0,
            random);
        }

    public static Node learn(Domain domain, Example[] examples, int[] attributes, Leaf defaultLeaf, int maxDepth,
        int depth, MersenneTwisterFast random)
        {
        if (maxDepth == depth)
            return defaultLeaf;
        else if (examples.length <= 1)
            return defaultLeaf;
        else if (allSameClassification(examples) || allSameValues(examples))
            return defaultLeaf;
        else if (attributes.length == 0)
            return defaultLeaf;
        else
            {
            int bestAttribute = chooseAttribute(domain, attributes, examples, random);
            if (bestAttribute == NO_ATTRIBUTE) // all splits had bad information, so insert a leaf instead of splitting
                return new Leaf(domain, examples, examples);
            return provideNode(domain, examples, attributes, maxDepth, depth, defaultLeaf, random);
            }
        }

    }
