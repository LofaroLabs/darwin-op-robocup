package sim.app.horde.classifiers.decisiontree.pruning ;

import java.io.* ;
import java.util.* ;
import ec.util.MersenneTwisterFast ;
import sim.app.horde.classifiers.* ;
import sim.app.horde.classifiers.decisiontree.*;

public class TestPruning
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
                
        // learn the decision tree
        dTree.setDomain(domain);        
        Node root = Node.learn(domain, examples, Integer.MAX_VALUE, mrand);
        dTree.setRoot((Node)root.clone());
                
        // copies of the original tree for different tests
        DecisionTree pepTree = (DecisionTree)dTree.clone();
        DecisionTree dlpepTree = (DecisionTree)dTree.clone() ;
        DecisionTree lgpTree = (DecisionTree)dTree.clone() ;
        PruningAlgorithm pruningalgo = null ;
                
        
        /** ------------ Original PEP test ---------------- **/
        pruningalgo = new PessimisticErrorPruning();
        // gather stats from the training samples
        System.out.println("\nGathering stats from the examples again for PEP ... ");           
        pruningalgo.gatherTrainingStats(pepTree, examples, mrand);
        // and save it
        snapshot.setLearnedModel(pepTree, examples);
        snapshot.saveTreeSnapshot(location, "test-00", true);                                           
                
        pruningalgo.pruneTree(pepTree);

        pruningalgo.resetTrainingStats(pepTree);
        pruningalgo.gatherTrainingStats(pepTree, examples, mrand);
        snapshot.setLearnedModel(pepTree, examples);
        snapshot.saveTreeSnapshot(location, "test-00-PEPed", true);                                             
        pruningalgo.deleteTrainingStats(pepTree);

        /** ---------------- Depth limited PEP test ------------- **/
        pruningalgo = new DepthLimitedPEP(0.4);
        // gather stats from the training samples
        System.out.println("\nGathering stats from the examples again Depth Limited PEP... ");                  
        pruningalgo.gatherTrainingStats(dlpepTree, examples, mrand);
        // and save it
        snapshot.setLearnedModel(dlpepTree, examples);
        snapshot.saveTreeSnapshot(location, "test-01", true);                                           
                
        pruningalgo.pruneTree(dlpepTree);

        pruningalgo.resetTrainingStats(dlpepTree);
        pruningalgo.gatherTrainingStats(dlpepTree, examples, mrand);
        snapshot.setLearnedModel(dlpepTree, examples);
        snapshot.saveTreeSnapshot(location, "test-01-DLPEPed", true);                                           
        pruningalgo.deleteTrainingStats(dlpepTree);

        /** ---------------- Local Global Pruning test ------------- **/
        pruningalgo = new LocalGlobalPruning(examples.length);
        // gather stats from the training samples
        System.out.println("\nGathering stats from the examples again for Local-Global Pruning... ");                   
        pruningalgo.gatherTrainingStats(lgpTree, examples, mrand);
        // and save it
        snapshot.setLearnedModel(lgpTree, examples);
        snapshot.saveTreeSnapshot(location, "test-02", true);
                
        /*pruningalgo.pruneTree(lgpTree);

          pruningalgo.resetTrainingStats(lgpTree);
          pruningalgo.gatherTrainingStats(lgpTree, examples, mrand);
          snapshot.setLearnedModel(lgpTree, examples);
          snapshot.saveTreeSnapshot(location, "test-01-DLPEPed", true);                                           
          pruningalgo.deleteTrainingStats(lgpTree);*/
                
        return ;        
        }
    }
