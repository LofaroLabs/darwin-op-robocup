package sim.app.horde.classifiers.decisiontree.pruning ;

import java.util.* ;
import ec.util.* ;
import sim.app.horde.classifiers.*;
import sim.app.horde.classifiers.decisiontree.* ;

public class PessimisticErrorPruning extends PruningAlgorithm
    {

    /**
     * An empty constructor.
     */
    public PessimisticErrorPruning()
        {
        super();
        }

    /**
     * Instantiate the NodeStatistics object for every node including the leaf.
     */
    protected void setStatisticsHolderToNodes(Node node)
        {
        node.statistics = new NodeStatisticsForPEP(super.dom);
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
        double eT = 0.0, et = 0.0, se = 0.0 ;
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
            double n = node.statistics.totalTrainingExamplesOccurrence ;
            eT = node.statistics.leafCount * 0.5 ;
            et = (n - maxList.get(0).getValue().intValue()) + 0.5 ;
            se = Math.sqrt((eT * (n - eT)) / n);
            //se = Math.sqrt((et * (n - et)) / n); // just for test
            ((NodeStatisticsForPEP)node.statistics).eT = eT ;
            ((NodeStatisticsForPEP)node.statistics).et = et ;
            ((NodeStatisticsForPEP)node.statistics).se = se ;
            }
        else // if there are duplicate majority classes
            {
            // take a random class label from the maxList
            int index = mtf.nextInt(maxList.size());
            node.statistics.majorityClassLabel = maxList.get(index).getKey().intValue();
            double n = node.statistics.totalTrainingExamplesOccurrence ;
            eT = node.statistics.leafCount * 0.5 ;
            et = (n - maxList.get(0).getValue().intValue()) + 0.5 ;
            se = Math.sqrt((eT * (n - eT)) / n);
            //se = Math.sqrt((et * (n - et)) / n); // just for test
            ((NodeStatisticsForPEP)node.statistics).eT = eT ;
            ((NodeStatisticsForPEP)node.statistics).et = et ;
            ((NodeStatisticsForPEP)node.statistics).se = se ;
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
        ((NodeStatisticsForPEP)leaf.statistics).et = 0.0 ;
        ((NodeStatisticsForPEP)leaf.statistics).eT = 0.0 ;
        ((NodeStatisticsForPEP)leaf.statistics).se = 0.0 ;                      
        return leaf.statistics.classLabelToExampleCount ;
        }
        
    /**
     * PEP implementation from the Esposito's paper.
     */
    protected void pruneTree(Node node, Domain dom)
        {       
        if(node instanceof Leaf)
            return ;
        else
            {
            for(int i = 0 ; i < node.successors.length ; i++)
                {                                               
                this.pruneTree(node.successors[i], dom);
                // to make the test more human readable
                double et = ((NodeStatisticsForPEP)node.successors[i].statistics).et ;
                double eT = ((NodeStatisticsForPEP)node.successors[i].statistics).eT ;
                double se = ((NodeStatisticsForPEP)node.successors[i].statistics).se ;
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
