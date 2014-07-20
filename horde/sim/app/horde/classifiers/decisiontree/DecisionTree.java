package sim.app.horde.classifiers.decisiontree;
import sim.app.horde.classifiers.*;

import java.io.*;
import ec.util.MersenneTwisterFast;
import sim.app.horde.classifiers.*;
import sim.app.horde.classifiers.decisiontree.pruning.*; /** added by khaled **/

/**
 * A decision tree is basically a wrapper for a Node object, which is the root 
 * of the decision tree.  
 */

public class DecisionTree extends Classifier
    {
    private static final long serialVersionUID = 1L;

    private Node root; 
    /** I needed these two functions -- khaled **/
    public Node getRoot() { return this.root ;}
    public void setRoot(Node rt) { this.root = rt ;}
    
    /** The pruning algorithm object -- khaled **/
    transient PruningAlgorithm pruning = null ;
    
    public void setPruningAlgorithm(PruningAlgorithm prune)
        {
        pruning = prune;
        }
        
    public Object clone()
        {
        DecisionTree dt = (DecisionTree)super.clone(); 
        dt.root = (Node)root.clone(); 
        return dt; 
        }

    public DecisionTree() { super(); }
    public DecisionTree(PruningAlgorithm prune)
        {
        super();
        setPruningAlgorithm(prune);
        }

    /** this procedure calls the pruning mechanism -- khaled **/        
    // already have checked examples array in LearnedTransition 
    public void learn(Example[] examples, MersenneTwisterFast random)
        {
        root = Node.learn(domain, examples, Integer.MAX_VALUE, random); 

        if (this.pruning != null)
            {
            this.pruning.gatherTrainingStats(this, examples, random);
            // the original PEP implementation
            this.pruning.pruneTree(this);
            this.pruning.deleteTrainingStats(this);

            /** Depth Limited PEP process -- added by khaled **/
            /*this.pruning = new DepthLimitedPEP(0.4);
              this.pruning.gatherTrainingStats(this, examples, random);
              // the depth limited PEP implementation
              this.pruning.pruneTree(this);
              this.pruning.deleteTrainingStats(this);
              this.pruning = null ;*/
            }
        }
        
    public int classify(Example e, MersenneTwisterFast random)
        {
        if (root == null) throw new RuntimeException("Trying to classify on a null tree!");
        return root.classify(e, random);
        }
    
    public void updateSplit(Example e)
        {
        if (root == null) throw new RuntimeException("Tring to updateNode from a null tree");

        if (root.successors[0] instanceof Leaf || root.successors[1] instanceof Leaf)
            {
            Example[] ex = new Example[root.myExamples.length + 1];
            ex[0] = (Example) e.clone();
            for (int i = 0; i < root.myExamples.length; i++)
                ex[i + 1] = (Example) root.myExamples[i].clone();
            root = Node.learn(root.dom, ex, Integer.MAX_VALUE, new MersenneTwisterFast());
            }
        else
            root.updateSplit(e);
        }

    public void updateNode(Node n, Example e)
        {
        if (root == null) throw new RuntimeException("Tring to updateNode from a null tree");
        root.updateNode(n, e);
        }
    
    public Example[] getExamples(Example e)
        {
        if (root == null) throw new RuntimeException("Tring to getExamples from a null tree"); 
        return root.getExamples(e); 
        }

    public void write(PrintWriter writer)
        {
        if (root == null) throw new RuntimeException("Trying to write a null tree!");
        root.write(writer); 
        }

    public void writeDotFile(String file, String label) throws IOException
        {
        if (root == null) throw new RuntimeException("Trying to write a dot file for  a null tree!");
        root.writeDotFile(file, label);
        }

    public void writeClassifier(PrintWriter writer, boolean writeDomain)
        {
        if (root == null) throw new RuntimeException("Trying to write a null tree!");
        root.writeTree(writer, writeDomain); 
        }
        
    public void show(int index, String label) throws IOException, InterruptedException
        {
        String file = Classifier.getPathInDirectory(examplesFilename); 
        writeDotFile(file, "" + index + ": " + label);
        super.show(index, label); 
        }
    
    public int getDepth()
        {
        if (root == null) return 0; 
        return root.getDepth();
        }
        

    public String toString()
        {
        if (root == null) return "[Empty Decision Tree]!"; 
        return root.toString(); 
        }

    }
