package sim.app.horde.classifiers.decisiontree;
import sim.app.horde.classifiers.*;
import ec.util.*;
import java.text.DecimalFormat;
import java.io.*;
import java.util.*;


/** A leaf node in a decision tree, that is, a node which simply returns a classification value. */
public class Leaf extends Node
    {
    private static final long serialVersionUID = 1;
    public static final DecimalFormat format = new DecimalFormat(); 
    static { format.setMaximumFractionDigits(2);  format.setMaximumIntegerDigits(0); } //FIXME setMaximumIntegerDigits(1)
        
    /** The distribution of classes in this leaf. */
    double[] pdf;
        
    /** The total number of examples used to compute this leaf. */
    int count;
        
    /** Whether or not all the examples belonged to the exact same class. */
    boolean deterministicLeaf;

    /** I needed this procedure -- khaled **/
    public boolean isDeterministicLeaf() { return deterministicLeaf; } 
    
    /**
     * A Leaf constructor, only creates a deterministic leaf
     * this is required when the pruning mechanism creates a leaf
     * on the fly -- added by khaled 
     */
    public Leaf(Domain dom, int attribute, boolean isDeterministic, Example[] givenExamples)
        {
        super(dom, attribute);
        this.deterministicLeaf = isDeterministic ;
        setMyExamples(givenExamples); 
        }
    
    public int getDepth() 
        { 
        return 0; 
        }
    
    public Node updateSplit(Example e)
        {
        System.err.println("CANNOT UPDATE_SPLIT A LEAF"); 
        return null; 
        }
    
    public void updateNode(Node n, Example e)
        {
        System.err.println("CANNOT UPDATE A LEAF"); 
        throw new RuntimeException();
        }

    
    public Node getParent(Example e) { return parent; } 
    
    public Example[] getExamples(Example e) { return myExamples; } 
    public Example[] getExamples() { return myExamples; } 
    
    public int getCount() {
        return count;
        }
    
    /** getting actual PDF so should not change values. Copy if value changes needed */
    public double[] getPDF() {
        return pdf;
        }
    
    protected Leaf(Domain dom, int attr)
        {
        super(dom, attr);
        }

    @Override 
    public Leaf copy() {            
        Leaf leafNode = new Leaf(this.dom, this.attribute);
        leafNode.count = this.count;
        leafNode.deterministicLeaf = this.deterministicLeaf;
                
        leafNode.pdf = new double[this.pdf.length];
        for(int i = 0 ; i < pdf.length; i++) {
            leafNode.pdf[i] = this.pdf[i];
                        
            }
                
        return leafNode;
        }


    /** Returns true if everyone in the distribution is the same class -- that is, they're all 0's and 1's */
    public boolean allSameClassification(double[] cdf)
        {
        // Works for PDFs or CDFS
        for(int i = 0 ; i < cdf.length; i++)
            if (cdf[i] != 0.0 && cdf[i] != 1.0)
                return false;
        return true;
        }
        
    /** Returns the class which everyone in the distribution belongs to.  Only call this if allSameClassification returns true. */
    public int uniformClass(double[] cdf)
        {
        // Works for PDFs or CDFS
        for(int i = 0 ; i < cdf.length; i++)
            if (cdf[i] == 1.0)
                return i;
        throw new RuntimeException("No valid uniform class");
        }
        
    /** Constructs a leaf.  Called by the constructors because of Java's stupid constructor semantics. */
    protected void buildLeaf(Domain dom, double[] pdf, int count)
        {
        this.pdf = pdf;
        this.count = count;
        // compute a class
        if (
            // deterministic || 
            allSameClassification(pdf))
            {
            attribute = uniformClass(pdf);
            deterministicLeaf = true;
            }
        else deterministicLeaf = false;
        }
    
    /** Constructor used for pruning - count will be set to -1 (perhaps should get full leaf counts*/
    public Leaf(Domain dom, double[] pdf, Example[] givenExamples) {
        super(dom, 0);
        setMyExamples(givenExamples); 
        buildLeaf(dom, normalize(pdf), -1); 
        }
        
    /** Produces a Leaf given a domain, remaining examples. */
    public Leaf(Domain dom, Example[] examples, Example[] givenExamples)
        {
        super(dom, 0);
        setMyExamples(givenExamples); 
        buildLeaf(dom, 
            // build a distribution array, initially just counts
            normalize(buildClassCounts(dom, examples)),
            // use the examples as the count
            examples.length
            );
        }
        
        
    public double[] provideDistribution(Example e)
        {
        return pdf;
        }
        
    public int classify(Example e, MersenneTwisterFast random)
        { 
        if (deterministicLeaf) 
            {
            return attribute;
            }
        else
            {
            // draw from the PDF distribution
            double sum = 0;
            double val = random.nextDouble();
            for (int i = 0; i < pdf.length - 1; i++)
                {
                sum += pdf[i];
                if (val < sum)
                    {
                    return i;
                    }
                }
            return pdf.length - 1;
            }
        }

    public String toString()
        {
        if (deterministicLeaf)
            return dom.classes[attribute] + "(" + myExamples.length + ")"; //<-- I will fix it -khaled
        //return dom.classes[attribute] ; /** added by khaled **/
        else
            {
            String s = "[";
            boolean started = false;
            for(int i = 0; i < pdf.length; i++)
                {
                if (started)  s += " ";
                started = true;
                if (pdf[i] != 0.0)
                    s = s + dom.classes[i] + "(" + (format.format(pdf[i])) + ")";
                }
            return s + "]";
            }
        }

    public String nodeToDot()
        {
        updateUniqueInteger();
        // build the label for me
        return "" + getUniqueInteger() + " [ label=\"" + toString() + "\", shape=ellipse ];\n";
        }






    /***** PRUNING CODE.  More code found in Leaf.java ******/


    /** Produces a Leaf given the following CDF distribution.   This version solely used for pruning. */
    public Leaf(Domain dom, double[] pdf, int count)
        {
        super(dom, 0);
        buildLeaf(dom, pdf, count);
        }

    /** Leaf nodes always prune to themselves. */
    public Node prune(Domain dom, double c, double lambda)
        {
        preprocessForPruning();
        return this; // why prune?
        }
        
    /** Adds into addInto the leaf node counts.  Computed by un-CDF-ifying, then multiplying each distribution
        value by the total count, then adding that in.  This may not quite be an integer but it will be close.
        Which should be fine for making a new CDF out of it. */
    protected double[] getLeafCounts(double[] addInto)
        {
        for(int i = 0; i < pdf.length; i++)
            {
            addInto[i] += count * pdf[i];
            }
        return addInto;
        }
        
    /** Computes the total number of leaves as 1 (of course) and the total error using the Gini index
        on the non-CDF-ified version of the distribution. */
    protected void preprocessForPruning()
        {
        totalLeaves = 1;
        totalError = 0;
        for(int i = 0; i < pdf.length; i++)
            {
            double p = pdf[i];
            // Gini index
            totalError += p * (1 - p);  // all told this is the same as 1 - sum_i p_i^2
            }
        }

    public void write(PrintWriter writer)
        {
        writer.print(" ( leaf");
        writer.print(" " + deterministicLeaf);
        if (deterministicLeaf)
            writer.print(" " + attribute);
        writer.print(" " + count);
        if (!deterministicLeaf)
            for (int i = 0; i < pdf.length; i++)
                writer.print(" " + pdf[i]);
        writer.print(" )\n");
        }
                
                
                
    public Leaf(Scanner scanner, Domain domain)
        {
        super(scanner, domain);
        successors = null;
        attribute = Integer.parseInt(token(scanner));
        count = Integer.parseInt(token(scanner));
        ArrayList a = new ArrayList();
        while(true)
            {
            String token = token(scanner);
            if (token.equals(")")) break;
            a.add(token);
            }
        pdf = new double[a.size()];
        for(int i =0; i < pdf.length; i++)
            pdf[i] = Double.parseDouble((String)(a.get(i)));
        token(scanner, ")");
        }
    }
