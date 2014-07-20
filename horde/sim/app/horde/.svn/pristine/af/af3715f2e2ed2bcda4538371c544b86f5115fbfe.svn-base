package sim.app.horde.classifiers.decisiontree.pruning ;

import java.util.* ;
import sim.app.horde.classifiers.* ;
import sim.app.horde.classifiers.decisiontree.*;

public class NodeStatistics
    {
    public int totalTrainingExamplesOccurrence = 0 ; // n(t): Total number of examples reaching to this node.
    public int majorityClassLabel = -1 ;             // Majority class label for this node,
    // required to see if this node is replaced 
    // by the majority-class labeled leaf.
    public int leafCount = 0 ; // L(t): Total number of leaves branched out from this node.
    public boolean tobePruned = false ; // Flag for this node, if it needs to be pruned.
    public int nonApparentError = 0 ; // Non-apparent error: total number of misclassified 
    // examples at this node after pruning.
    private Domain dom = null ;     // Domain object from the Node
        
    // A Hashtable object to store example counts for each class label
    public Hashtable<Integer, Integer> classLabelToExampleCount = new Hashtable<Integer, Integer>() {
        public String toString()
            {
            String str = "" ;
            Enumeration<Integer> e = this.keys() ;
            Integer classLabel = null ;
            int exampleCount = 0 ;
                
            for(int i = 0 ; i < this.size() - 1 ; i++)
                {
                classLabel = e.nextElement();
                exampleCount = this.get(classLabel);
                str += dom.classes[classLabel.intValue()] + ":" + exampleCount + ", " ;
                }
            classLabel = e.nextElement();
            exampleCount = this.get(classLabel);
            str += dom.classes[classLabel] + ":" + exampleCount ;
            str = "{" + str + "}" ;
            return str ;            
            }
        };

    /**
     * Constructor
     */
    public NodeStatistics(Domain dom)
        {
        this.dom = dom ;
        }

    /**
     * String representation.
     */
    public String toString(Node node)
        {
        String stat = "" ;
        if(node instanceof Leaf)                
            stat = "\\n n(t): " + this.totalTrainingExamplesOccurrence 
                + " e(t): " + this.nonApparentError ;                                   
        else
            {                       
            stat = "\\n" ;
            for(Enumeration<Integer> e = this.classLabelToExampleCount.keys(); e.hasMoreElements();)
                {
                Integer classLabel = e.nextElement();
                int exampleCount = this.classLabelToExampleCount.get(classLabel).intValue();
                stat += this.dom.classes[classLabel.intValue()] + " :: " 
                    + exampleCount + "/" + this.totalTrainingExamplesOccurrence + "\\n" ;
                }
            stat += "Majority Label: " + this.dom.classes[this.majorityClassLabel]
                + "\\n Leaf count: " + this.leafCount
                + " n(t): " + this.totalTrainingExamplesOccurrence 
                + " e(t): " + this.nonApparentError ;                                                           
            }
        return stat ;
        }

    /**
     * Resets all values.
     */
    public void reset()
        {
        this.totalTrainingExamplesOccurrence = 0 ;
        this.majorityClassLabel = -1 ;                                  
        this.tobePruned = false ;
        this.classLabelToExampleCount.clear();
        this.leafCount = 0 ;
        }
    }
