package sim.app.horde.classifiers.decisiontree.pruning ;

import java.util.* ;
import ec.util.* ;

import sim.app.horde.classifiers.*;
import sim.app.horde.classifiers.decisiontree.* ;

public abstract class PruningAlgorithm
    {
    /** Root of a decision tree to be pruned **/
    protected Node root  = null ;
    protected Domain dom = null ;

    /**
     * An empty constructor
     */
    public PruningAlgorithm()
        {
        ;
        }

    /**
     * Instantiate the NodeStatistics object for every node except the Leaf.
     * Note: different pruning mechanism requires different node statistics.
     * So, we need corresponding subclass of NodeStatistics.
     */
    protected abstract void setStatisticsHolderToNodes(Node node);
        
    /**
     * Gathers all training statistics for pruning.
     */
    public void gatherTrainingStats(DecisionTree dTree, Example[] examples, MersenneTwisterFast mtf)
        {
        this.root = dTree.getRoot();
        this.dom = dTree.getDomain();
        if(root != null)
            {
            this.setStatisticsHolderToNodes(this.root); // set the statitics object for each node, start from the root
            root.statistics.leafCount = this.getLeafCountForEachNode(root); // get leaf count for each node 
            this.reclassify(this.root, examples, mtf); // reclassify all examples to get the training statistics
            // the statistics constitutes only the total training examples
            // reaching to a particular node.  
            this.gatherTrainingStats(this.root, mtf);            
            }
        else
            System.out.println("The tree is empty !!!");
        }
        
    /**
     * Top level statistics collection procedure
     */     
    protected Hashtable<Integer, Integer>gatherTrainingStats(Node node, MersenneTwisterFast mtf)
        {               
        if(!(node instanceof Leaf))
            return this.setTrainingStats(node, mtf);
        else
            return this.setTrainingStats((Leaf)node, mtf);
        }

    /**
     * Gathers all training statistics at each continuous/toroidal/categorical node for pruning.
     * Note: different pruning mechanism requires different node statistics,
     * hence this procedure is abstract, needs to be implemented depending
     * on the actual pruning mechanism.
     */
    protected abstract Hashtable<Integer, Integer>setTrainingStats(Node node, MersenneTwisterFast mtf);

    /**
     * Gathers training stats for a leaf node.
     * Note: different pruning mechanism requires different node statistics,
     * hence this procedure is abstract, needs to be implemented depending
     * on the actual pruning mechanism.      
     */
    protected abstract Hashtable<Integer, Integer> setTrainingStats(Leaf leaf, MersenneTwisterFast mtf);

    /**
     * Resets the training statistics from the tree, top level call.
     */
    public void resetTrainingStats(DecisionTree dTree)
        {
        Node root = dTree.getRoot();
        this.resetTrainingStats(root);
        }

    /**
     * Resets all statistics values in all nodes.
     */
    private void resetTrainingStats(Node node)
        {
        if(!(node instanceof Leaf))
            {
            node.statistics.reset();
            if(node.successors != null)
                {
                for(int i = 0 ; i < node.successors.length ; i++)
                    this.resetTrainingStats(node.successors[i]);
                }
            }
        }

    /**
     * Deletes the NodeStatistics objects from every
     * node in the decision tree, top level call.
     * (i.e. sets them to null)
     */
    public void deleteTrainingStats(DecisionTree dTree)
        {
        Node root = dTree.getRoot();
        this.deleteTrainingStats(root);
        }       

    /**
     * Deletes all NodeStatistics objects from
     * every node in the decision tree,
     * (i.e. sets them to null)
     */      
    private void deleteTrainingStats(Node node)
        {               
        node.statistics = null ;
        if(node.successors != null)
            {
            for(int i = 0 ; i < node.successors.length ; i++)
                this.deleteTrainingStats(node.successors[i]);                   
            }               
        }
        
    /**
     * Gets the leaf count for each node.
     */
    private int getLeafCountForEachNode(Node node)
        {
        if(node instanceof Leaf)
            return 1 ;
        else
            {                       
            for(int i = 0 ; i < node.successors.length ; i++)                       
                node.statistics.leafCount += this.getLeafCountForEachNode(node.successors[i]);
            return node.statistics.leafCount ;                      
            }
        }

    /**
     * Topmost level of the reclassify procedure.
     */     
    private void reclassify(Node root, Example[] examples, MersenneTwisterFast mtf)
        {
        for(int i = 0 ; i < examples.length ; i++) // reclassify the data to get the training stats     
            this.reclassify(root, examples[i], mtf);                
        }

    /**
     * Next level reclassify procedure, calls corresponding
     * reclassify for each kind of node.
     */
    private int reclassify(Node node, Example e, MersenneTwisterFast random)
        {                               
        String className = node.getClass().getSimpleName();             
        if(className.equalsIgnoreCase("CategoricalNode"))                       
            return reclassify((CategoricalNode)node, e, random);
        else if(className.equalsIgnoreCase("ContinuousNode"))                   
            return reclassify((ContinuousNode)node, e, random);
        else if(className.equalsIgnoreCase("ToroidalNode"))
            return reclassify((ToroidalNode)node, e, random);
        else
            return reclassify((Leaf)node, e, random) ;
        }

    /**
     * Reclassification procedure when the node type 
     * is categorical.
     */
    private int reclassify(CategoricalNode catnode, Example e, MersenneTwisterFast random)
        {
        catnode.statistics.totalTrainingExamplesOccurrence++ ;
        return reclassify(catnode.successors[(int)(e.values[catnode.attribute])], e, random);
        }

    /**
     * Reclassification procedure when the node type 
     * is continous.
     */
    private int reclassify(ContinuousNode cnode, Example e, MersenneTwisterFast random)
        {
        cnode.statistics.totalTrainingExamplesOccurrence++ ;            
        return reclassify(cnode.successors[e.values[cnode.attribute] < cnode.getSplit() ? 0 : 1], e, random);   
        }
        
    /**
     * Reclassification procedure when the node type 
     * is toroidal.
     */
    private int reclassify(ToroidalNode tnode, Example e, MersenneTwisterFast random)
        {
        tnode.statistics.totalTrainingExamplesOccurrence++ ;
        return 
            reclassify(
                tnode.successors[(e.values[tnode.attribute] >= tnode.getSplit() 
                        && e.values[tnode.attribute] < tnode.getSplit2()) ? 0 : 1], 
                e, random);
        }

    /**
     * Reclassification procedure when the node type 
     * is leaf.
     */
    private int reclassify(Leaf leaf, Example e, MersenneTwisterFast random)
        {
        leaf.statistics.totalTrainingExamplesOccurrence++ ; 
        if (leaf.isDeterministicLeaf())
            {
            if(e.classification != leaf.attribute)
                leaf.statistics.nonApparentError++ ; 
            return leaf.attribute;
            }
        else
            {
            // draw from the PDF distribution
            double sum = 0;
            double val = random.nextDouble();
            double[] pdf = leaf.getPDF();
            for (int i = 0 ; i < pdf.length - 1 ; i++)
                {
                sum += pdf[i];
                if (val < sum)
                    {
                    if(e.classification != i)
                        leaf.statistics.nonApparentError++ ; 
                    return i;                                  
                    }
                }
            if(e.classification != pdf.length - 1)
                leaf.statistics.nonApparentError++ ;
            return pdf.length - 1;
            }       
        }

    /**
     * Decision Tree Pruning top level call.
     */
    public void pruneTree(DecisionTree dTree)
        {
        Domain dom = dTree.getDomain();
        Node root = dTree.getRoot();
        this.pruneTree(root, dom);
        }

    /**
     * Actual pruning algorithm.
     * Note: this is an abstract procedure to implement different
     * pruning algorithms.
     */
    protected abstract void pruneTree(Node root, Domain dom);
    }
