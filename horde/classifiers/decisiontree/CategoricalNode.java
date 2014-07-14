package sim.app.horde.classifiers.decisiontree;
import sim.app.horde.classifiers.*;
import ec.util.*;
import java.io.*;
import java.util.*;


/** A categorical node in a decision tree, that is, one whose split attribute is a set of categories rather than a metric range of values. */
public class CategoricalNode extends Node
    {
    private static final long serialVersionUID = 1;
    public static Node provideNode(Domain dom, int attr, Example[] examples, int[] attributes, int maxDepth, int depth, Leaf defaultLeaf, MersenneTwisterFast random)
        {
        Example[][] spexamples = split(dom, attr, examples, random);
        
        if (value(dom, examples, spexamples) < MINIMUM_VALUE)  // uh oh
            {
            System.err.println("Best result was zero information!!!");
            return defaultLeaf;
            }
        return new CategoricalNode(dom, attr, examples, attributes, maxDepth, depth, spexamples, random);
        }
    
    /** Produces a subtree rooted by a CategoricalNode, given the domain, attribute to split on, and a default leaf to use
        if the split isn't very good. In turn calls learn(...) to make each of the subtrees.   The subtree will not exceed maxDepth. */                 
    public CategoricalNode(Domain dom, int attr, Example[] examples, int[] attributes, int maxDepth, int depth,  Example[][] spexamples, MersenneTwisterFast random)
        {
        super(dom, attr);
                        
        // Example[][] spexamples = split(dom, attr, examples);
        successors = new Node[spexamples.length];
                        
        for(int i = 0; i < successors.length; i++)
            successors[i] = learn(dom, spexamples[i], except(attributes, attr), new Leaf(dom, examples, spexamples[i]), maxDepth, depth, random);
        }
    
    
    public CategoricalNode(Domain dom, int attr){
        super(dom, attr);
        
        }
    
    public CategoricalNode copy() {     
        CategoricalNode categoricalNode = new CategoricalNode(this.dom, this.attribute);        
        
        categoricalNode.successors = new Node[this.successors.length];
        for(int i = 0 ; i < successors.length; i++) {
            categoricalNode.successors[i] = this.successors[i].copy();              
            }
        
        return categoricalNode;
        }

    /** Splits the examples into groups by the values they have for the given attribute. */
    public static Example[][] split(Domain dom, final int attribute, Example[] examples, MersenneTwisterFast random)
        {
        // count of number of examples for each value
        int[] counts = new int[dom.values[attribute].length];
        for(int j = 0; j < examples.length; j++)
            counts[(int)(examples[j].values[attribute])]++;
                        
        // Build the new Example arrays
        Example[][] ex = new Example[dom.values[attribute].length][];
        for(int i = 0; i < ex.length; i++)
            ex[i] = new Example[counts[i]];

        // Load the arrays
        for(int j = 0; j < examples.length; j++)
            ex[(int)(examples[j].values[attribute])]
                [--counts[(int)(examples[j].values[attribute])]] = examples[j];
                
        return ex;
        }
    
    
    public Node updateSplit(Example e)
        {
        /*int idx = (int)(e.values[attribute]); 
          if (successors[idx] instanceof Leaf)
          {
          Example[] ex = new Example[myExamples.length + 1]; 
          ex[0] = e;
          System.arraycopy(myExamples, 0, ex, 1, myExamples.length); 
          successors[idx] = learn(dom, ex, attribute, new MersenneTwisterFast()); 
          }
          else successors[idx].updateSplit(e); 
        */
        return null; 
        }
    
    public void updateNode(Node n, Example e)
        {
        int idx = (int)(e.values[attribute]); 
        if (successors[idx] instanceof Leaf) 
            successors[idx] = n; 
        else 
            successors[idx].updateNode(n, e); 
        }
    
    
    public Node getParent(Example e)
        {
        return successors[(int)(e.values[attribute])].getParent(e); 
        }
    
    public Example[] getExamples(Example e)
        {
        return successors[(int)(e.values[attribute])].getExamples(e);
        }

    public int classify(Example e, MersenneTwisterFast random)
        {
        return successors[(int)(e.values[attribute])].classify(e, random);
        }
                
    public double[] provideDistribution(Example e)
        {
        return successors[(int)(e.values[attribute])].provideDistribution(e);
        }
                
    public String toString()
        {
        return dom.attributes[attribute];
        }

    public String nodeToDot()
        {
        updateUniqueInteger();
                
        // build the label for me
        String s = "" + getUniqueInteger() + " [ label=\"" + toString() + "\", shape=box ];\n";
        //build the labeled edges to children
        for(int i = 0; i < successors.length; i++)
            {
            s += successors[i].nodeToDot();  // updates integer also
            s += "" + getUniqueInteger() + "->" + 
                successors[i].getUniqueInteger() + " [ label=\"" + 
                dom.values[attribute][i] + "\"];\n";
            }
                                
        return s;
        }


    public void write(PrintWriter writer)
        {
        writer.print(" ( categorical " + attribute);
        writer.println();
        for (int i = 0; i < successors.length; i++)
            successors[i].write(writer);
        writer.print(" )\n");
        }
                
                
    public CategoricalNode(Scanner scanner, Domain domain)
        {
        super(scanner, domain);
        attribute = Integer.parseInt(token(scanner));
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
