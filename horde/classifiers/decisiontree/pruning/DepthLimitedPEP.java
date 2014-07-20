package sim.app.horde.classifiers.decisiontree.pruning ;

import java.util.* ;
import ec.util.* ;
import sim.app.horde.classifiers.*;
import sim.app.horde.classifiers.decisiontree.* ;

public class DepthLimitedPEP extends PessimisticErrorPruning
    {
    /** The depth limit d **/
    private double depthPerc = 0.0 ;        

    /**
     * Constructor.
     */
    public DepthLimitedPEP(double depthPerc)
        {
        super();
        this.depthPerc = depthPerc ;
        }

    /**
     * Instantiate the NodeStatistics object for every node including the leaf.
     */
    protected void setStatisticsHolderToNodes(Node node)
        {
        node.statistics = new NodeStatisticsForDepthLimitedPEP(super.dom);
        if(node.successors != null)
            {
            for(int i = 0 ; i < node.successors.length ; i++)
                this.setStatisticsHolderToNodes(node.successors[i]);
            }                               
        }
        
    /**
     * Gathers all training statistics for depth limited pruning.
     */
    public void gatherTrainingStats(DecisionTree dTree, Example[] examples, MersenneTwisterFast mtf)
        {
        // set the tree depth for each node, the root has a depth of 0
        super.gatherTrainingStats(dTree, examples, mtf);
        if(super.root != null)
            this.setTreeDepthForEachNode(super.root, 0);
        else
            System.out.println("The tree is empty !!!");
        }

    /**
     * Sets the tree depth for each node, the root has depth of 0
     * and the nodes containing only leaves has depth of N-1, 
     * where the total tree is depth is N.
     */
    private void setTreeDepthForEachNode(Node node, int depth)
        {
        if(!(node instanceof Leaf))
            {
            ((NodeStatisticsForDepthLimitedPEP)node.statistics).depth = depth ;
            if(((NodeStatisticsForDepthLimitedPEP)node.statistics).depth > NodeStatisticsForDepthLimitedPEP.maxDepth)
                NodeStatisticsForDepthLimitedPEP.maxDepth = ((NodeStatisticsForDepthLimitedPEP)node.statistics).depth ;
            depth++ ;
            for(int i = 0 ; i < node.successors.length ; i++)               
                this.setTreeDepthForEachNode(node.successors[i], depth);
            }
        }

    /**
     * Depth-limited PEP, where 0 < depthPerc < 1.
     * If the depth of the tree is D and if depthPerc is d (0 < d < 1),
     * then the pruning will be restricted upto the depth of D(1 - d).
     * Where, root has the depth of 0 and the most bottom leave has the 
     * depth of D - 1.
     */
    public void pruneTree(Node node, Domain dom)
        {
        if(node instanceof Leaf)
            return ;
        else
            {
            int maxDepthAllowed = (int)(NodeStatisticsForDepthLimitedPEP.maxDepth * (1.0 - this.depthPerc));
            for(int i = 0 ; i < node.successors.length ; i++)
                {                       
                this.pruneTree(node.successors[i], dom);
                /*System.out.println("At depth: " + node.statistics.depth 
                  + ", checking with maxDepthAllowed: " + maxDepthAllowed);*/
                if(((NodeStatisticsForDepthLimitedPEP)node.statistics).depth > maxDepthAllowed - 1) // -1 to save the root
                    {
                    // to make the test more human readable
                    double et = ((NodeStatisticsForDepthLimitedPEP)node.successors[i].statistics).et ;
                    double eT = ((NodeStatisticsForDepthLimitedPEP)node.successors[i].statistics).eT ;
                    double se = ((NodeStatisticsForDepthLimitedPEP)node.successors[i].statistics).se ;
                    if(et < (eT + se)) // <--- PEP pruning decision is made here 
                        {
                        // you can comment out this message, if you wish
                        /*System.out.println(node.getClass().getSimpleName() 
                          + " Parent :: Replacing " 
                          + node.successors[i].toString()
                          + " with " 
                          + dom.classes[node.successors[i].statistics.majorityClassLabel]);*/
                        node.successors[i].statistics.tobePruned = true ; // although this line has no meaning now
                        // creating a deterministic node on the fly
                        // still not sure for any probabilitistic node, if there is any :-(
                        Leaf leaf = new Leaf(dom, 
                            node.successors[i].statistics.majorityClassLabel, true, node.successors[i].gatherExamples());
                        node.successors[i] = leaf ;
                        }
                    }
                }
            }
        }
    }
