package sim.app.horde.classifiers.decisiontree.pruning ;

import java.util.* ;
import sim.app.horde.classifiers.* ;
import sim.app.horde.classifiers.decisiontree.*;

public class NodeStatisticsForDepthLimitedPEP extends NodeStatisticsForPEP
    {

    /**
     * The depth limited PEP needs the depth of each node along with
     * all statistics for canonical PEP, the root has depth of 0.
     */     
    public static int maxDepth = -1 ; // Maximum depth of the whole tree, this is a static variable.
    public int depth = -1 ; // Depth of this node, root has a depth of 0.

    /**
     * Constructor
     */
    public NodeStatisticsForDepthLimitedPEP(Domain dom)
        {
        super(dom);
        }

    /**
     * String representation.
     */
    public String toString(Node node)
        {
        String stat = "" ;
        if(node instanceof Leaf)                
            stat = super.toString(node);
        else    
            {
            if(this.depth > 0)                      
                stat = super.toString(node) 
                    + "\\n depth: " + this.depth ;          
            else
                stat = super.toString(node) 
                    + "\\n maxDepth: " + this.maxDepth 
                    + " depth: " + this.depth ;

            }
        return stat ;
        }

    /**
     * Resets all values.
     */
    public void reset()
        {
        this.depth = -1 ;
        maxDepth = -1 ;
        super.reset();
        }
    }
