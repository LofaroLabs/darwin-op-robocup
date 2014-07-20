package sim.app.horde.classifiers.decisiontree.pruning ;

import java.util.* ;
import ec.util.* ;
import sim.app.horde.classifiers.*;
import sim.app.horde.classifiers.decisiontree.* ;

public class LocalGlobalPruning extends PruningAlgorithm
    {
    /** Total number of examples **/
    private int n = 0 ;

    /**
     * Constructor.
     */
    public LocalGlobalPruning(int n)
        {               
        super();
        this.n = n ;
        }

    /**
     * Instantiate the NodeStatistics object for every node including the Leaf.
     */
    protected void setStatisticsHolderToNodes(Node node)
        {
        node.statistics = new NodeStatisticsForLGPruning(super.dom);
        if(node.successors != null)
            {
            for(int i = 0 ; i < node.successors.length ; i++)
                this.setStatisticsHolderToNodes(node.successors[i]);
            }                               
        }

    /**
     * Gathers all training statistics at each 
     * continuous/toroidal/categorical node for pruning.
     */
    protected Hashtable<Integer, Integer>setTrainingStats(Node node, MersenneTwisterFast mtf)
        {
        for(int i = 0 ; i < node.successors.length ; i++)
            {
            Hashtable<Integer, Integer> ht 
                = super.gatherTrainingStats(node.successors[i], mtf);
            for(Enumeration<Integer> e = ht.keys(); e.hasMoreElements();)
                {
                Integer classLabel = e.nextElement();
                if(node.statistics.classLabelToExampleCount.containsKey(classLabel))
                    {
                    int exampleCount = 
                        node.statistics.classLabelToExampleCount.remove(classLabel).intValue() 
                        + ht.get(classLabel).intValue() ;
                    node.statistics.classLabelToExampleCount.put(classLabel, new Integer(exampleCount));
                    }
                else                            
                    node.statistics.classLabelToExampleCount.put(classLabel, ht.get(classLabel));
                }

            node.statistics.nonApparentError += node.successors[i].statistics.nonApparentError ;
            }

        Integer maxValue = Integer.MIN_VALUE;
        final List<Map.Entry<Integer, Integer>> maxList = new ArrayList<Map.Entry<Integer, Integer>>();

        for (final Map.Entry<Integer, Integer> entry : node.statistics.classLabelToExampleCount.entrySet()) 
            {
            if (maxValue.intValue() < entry.getValue().intValue()) 
                { 
                maxValue = entry.getValue();
                maxList.clear();
                }
            if (maxValue.equals(entry.getValue()))
                maxList.add(entry);
            }

        if (maxList.size() < 2) // if there is only one majority class
            {
            node.statistics.majorityClassLabel = maxList.get(0).getKey().intValue();
            double ni = node.statistics.totalTrainingExamplesOccurrence ;
            double ei = maxList.get(0).getValue().intValue();
            ((NodeStatisticsForLGPruning)node.statistics).localErrorRate = ei/ni ;
            ((NodeStatisticsForLGPruning)node.statistics).globalErrorRate = ei/this.n ;
            }
        else // if there are duplicate majority classes
            {
            // this code is as same as above, for the time being,
            // needs to be replaced later ...
            node.statistics.majorityClassLabel = maxList.get(0).getKey().intValue();
            double ni = node.statistics.totalTrainingExamplesOccurrence ;
            double ei = maxList.get(0).getValue().intValue();
            ((NodeStatisticsForLGPruning)node.statistics).localErrorRate = ei/ni ;
            ((NodeStatisticsForLGPruning)node.statistics).globalErrorRate = ei/this.n ;
            }
        return node.statistics.classLabelToExampleCount ;
        }

    /**
     * Gathers training stats for a leaf node.
     */
    protected Hashtable<Integer, Integer> setTrainingStats(Leaf leaf, MersenneTwisterFast mtf)
        {
        leaf.statistics.classLabelToExampleCount.put(leaf.attribute,
            new Integer(leaf.statistics.totalTrainingExamplesOccurrence));
        leaf.statistics.majorityClassLabel = leaf.attribute ;
        return leaf.statistics.classLabelToExampleCount ;
        }
        
    /**
     * PEP implementation from the Esposito's paper.
     */
    protected void pruneTree(Node node, Domain dom)
        {       
        /** Not implemented yet **/
        /*if(node instanceof Leaf)
          return ;
          else
          {
                                
          }*/
        }
    }
