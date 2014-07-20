package sim.app.horde.classifiers.decisiontree.pruning ;

import java.io.* ;
import java.util.* ;
import ec.util.MersenneTwisterFast ;
import sim.app.horde.classifiers.* ;
import sim.app.horde.classifiers.decisiontree.*;

public class ProfilePruning
    {
    public static void main(String[] args)
        {
        //System.out.println("Hello world\\n");
        MersenneTwisterFast mrand = new MersenneTwisterFast(123456);
        DecisionTree dTree = new DecisionTree();
        Example[] examples = null ;
        Domain domain = null ;
        String location = "../snaps/" ;
        //String bName = "1-Rotate[-2.0]" ;             
        String bName = "2-Forward[0.1]" ;
        //String bName = "3-Rotate[2.0]" ;
        //String bName = "0-Start" ;

        System.out.println("Working Directory = " + System.getProperty("user.dir"));

        // loading existing horde snapshots
        HordeSnapshot snapshot = new HordeSnapshot();
        examples = snapshot.loadHordeSnapshotExamples(location, bName);
        System.out.println("Loaded examples:");
        for(int i = 0 ; i < examples.length ; i++)
            System.out.print(examples[i].toString());

        domain = snapshot.loadHordeSnapshotDomain(location, bName);                     
        System.out.print("Loaded domain: \n" + snapshot.toString(domain) + "\n");
                
        for(int i = 0 ; i < 500 ; i++)
            {
            // learn the decision tree
            dTree.setDomain(domain);        
            Node root = Node.learn(domain, examples, Integer.MAX_VALUE, mrand);
            dTree.setRoot((Node)root.clone());
                
            // copies of the original tree for different tests
            DecisionTree pepTree = (DecisionTree)dTree.clone();
            PruningAlgorithm pruningalgo = null ;
                
        
            /** ------------ Original PEP test ---------------- **/
            pruningalgo = new PessimisticErrorPruning();
            // gather stats from the training samples
            pruningalgo.gatherTrainingStats(pepTree, examples, mrand);                              
            // prune the tree
            pruningalgo.pruneTree(pepTree);
            // delete them
            pruningalgo.deleteTrainingStats(pepTree);                                               
            }
        return ;        
        }
    }
