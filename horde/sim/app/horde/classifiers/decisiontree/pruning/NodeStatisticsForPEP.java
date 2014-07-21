package sim.app.horde.classifiers.decisiontree.pruning ;

import java.util.* ;
import sim.app.horde.classifiers.* ;
import sim.app.horde.classifiers.decisiontree.*;

public class NodeStatisticsForPEP extends NodeStatistics
    {
    // e'(t), e'(T) and SE(T), these are the stats needed for PEP
    // these values are evaluated in the 'gatherTrainingStats()' function.
    // Taken from the Esposito's pruning survey paper as it is.
    public double et = -1.0 ; // e'(t) = e(t) + 1/2
    public double eT = -1.0 ; // e'(T) = L(t)/2
    public double se = -1.0 ; // SE(T) = [e'(T) * (n(t) - e'(T))/n(t)]^(0.5)

    /**
     * Constructor
     */
    public NodeStatisticsForPEP(Domain dom)
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
                + "\\n e'(t): " + String.format("%.2f", this.et) + " "
                + "e'(T): " + String.format("%.2f", this.eT) + " "
                + "SE(T): " + String.format("%.2f", this.se);
            }
        return stat ;
        }

    /**
     * Resets all values.
     */
    public void reset()
        {
        super.reset();
        this.et = -1.0 ;
        this.eT = -1.0 ;
        this.se = -1.0 ;
        }
    }
