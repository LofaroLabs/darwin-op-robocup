package sim.app.horde.classifiers.decisiontree.pruning ;

import java.util.* ;
import sim.app.horde.classifiers.* ;
import sim.app.horde.classifiers.decisiontree.*;

public class NodeStatisticsForLGPruning extends NodeStatistics
    {
    /** 
     * Statistics for LG pruning, still not sure if we need 
     * the depth information. 
     */
    public double localErrorRate = 0.0 ; 
    public double globalErrorRate = 0.0 ;

    /**
     * Constructor
     */
    public NodeStatisticsForLGPruning(Domain dom)
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
            stat = super.toString(node)
                + "\\n eLocal: " + String.format("%.2f", this.localErrorRate) + " "
                + "eGlobal: " + String.format("%.2f", this.globalErrorRate) ;                           
            }
        return stat ;
        }

    /**
     * Resets all values.
     */
    public void reset()
        {
        super.reset();
        this.localErrorRate = 0.0 ;
        this.globalErrorRate = 0.0 ;
        }
    }
