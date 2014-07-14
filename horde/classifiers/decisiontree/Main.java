package sim.app.horde.classifiers.decisiontree;
import sim.app.horde.classifiers.*;

import java.io.IOException;
import ec.util.MersenneTwisterFast;
import sim.util.Bag;
import java.io.File;

/**
 *
 * @author vittorio
 */
public class Main
    {
    private static final long serialVersionUID = 1;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InterruptedException
        {
        if (args.length < 2)
            {
            System.err.println("Example usage:   java sim.app.horde.decisiontree.Main yo Goto");
            System.exit(1);
            }
        
        MersenneTwisterFast mtf = new MersenneTwisterFast();
        final int MAX_DEPTH = Integer.MAX_VALUE;
        Domain d = Domain.loadDomain(/*Node.getPathInDirectory(args[0]+".dom")*/new File(args[0]+".dom").getCanonicalPath(), mtf);
        Example[] e = Example.loadExamples(/*Node.getPathInDirectory(args[1]+".examples")*/new File(args[1]+".examples").getCanonicalPath(), d);
        int NUM_TESTS=1;
        for(int i=0;i<NUM_TESTS;i++){
            evaluate(args[1],d,e, MAX_DEPTH,mtf);
            }
                
        /*
          MersenneTwisterFast mtf = new MersenneTwisterFast();
          
          // Domain d = Domain.loadDomain(Node.getPathInDirectory("tests/iris.dom"), mtf);
          // Domain d = Domain.loadDomain(Node.getPathInDirectory("tests/recycle_dep.dom"), mtf);
          //   Domain d = Domain.loadDomain(Node.getPathInDirectory("tests/abalone.dom"), mtf);
          //  Domain d = Domain.loadDomain(Node.getPathInDirectory("tests/restaurant.dom"), mtf);
          //  Domain d = Domain.loadDomain(Node.getPathInDirectory("tests/car_names.dom"), mtf);
          //   Domain d = Domain.loadDomain(Node.getPathInDirectory("tests/tic-tac-toe.dom"), mtf);
                
          // Example[] e = Example.loadExamples(Node.getPathInDirectory("tests/iris.dat"), d);
          // Example[] e = Example.loadExamples(Node.getPathInDirectory("tests/recycle_dep.dat"), d);
          //   Example[] e = Example.loadExamples(Node.getPathInDirectory("tests/abalone.dat"), d);
          // Example[] e = Example.loadExamples(Node.getPathInDirectory("tests/restaurant.dat"), d);
          //Example[] e = Example.loadExamples(Node.getPathInDirectory("tests/car_names.dat"), d);
          // Example[] e = Example.loadExamples(Node.getPathInDirectory("tests/tic-tac-toe.dat"), d);

          //        Node.deterministic = true;

          //gotoAndRotate/gotoAndRotate

          final int MAX_DEPTH = Integer.MAX_VALUE;
          String name="vittorio3";
          String path="tests/"+name+"/";
          String domain=name;
          String behavior="Rotate(-2.0)";
          //   String behavior="Rotate(2.0)";
          // String behavior="Forward(0.1)";
          // String behavior=">";
          //String behavior="goEat";
          //String behavior="GoTo";
          //String behavior="Loop";
          Domain d = Domain.loadDomain(Node.getPathInDirectory(path+domain+".dom"), mtf);
          Example[] e = Example.loadExamples(Node.getPathInDirectory(path+behavior+".examples"), d);
          int NUM_TESTS=1;
          for(int i=0;i<NUM_TESTS;i++){
          evaluate(behavior,d,e, MAX_DEPTH,mtf);
          }
          //e = Example.loadExamples(Node.getPathInDirectory("tests/gotoAndLoop/Goto.examples"), d);
          //evaluate(d,e, MAX_DEPTH);
          */
        }

   

    private static void evaluate(String name,Domain d, Example[] e, int MAX_DEPTH, MersenneTwisterFast mtf) throws IOException, InterruptedException
        {
        double perc_train=1;
        int num_train=(int)(e.length*perc_train);
        Example[] training_set=new Example[num_train];
        Example[] test_set=new Example[e.length-num_train];
        Bag b=new Bag(e.length);
        b.addAll(0, e);
        b.shuffle(mtf);
        Object[] shuffled_examples=b.toArray();
        System.arraycopy(shuffled_examples, 0, training_set, 0, num_train);
        System.arraycopy(shuffled_examples, num_train, test_set, 0, e.length-num_train);
        if(test_set.length+training_set.length!=e.length) throw new RuntimeException("wrong split in training");
        System.err.println("Learning...");
        Node node = Node.learn(d, training_set, MAX_DEPTH, mtf);

        //System.err.println("Testing Consistency...");
        //test(d, node,training_set);

        //System.err.println("Testing Generalization...");
        //test(d, node,test_set);

        System.err.println("Writing...");
        String file = Node.getPathInDirectory("examples.dot");
        node.writeDotFile(file,name);

        System.err.println("Launching...");
        String cmd = Node.getPathInDirectory("../display.sh");
        System.err.println(cmd);
        //System.err.println("yo");
        Runtime run = Runtime.getRuntime() ;
        Process pr = run.exec(new String[] { cmd, file }) ;

        System.err.println("Waiting 1...");
        pr.waitFor();
        }

    // this doesn't work, since node.classify returns a sample from the node's pdf, thus is not 
    // deterministic.  
    static void  test(Domain d, Node node, Example[] ex, MersenneTwisterFast mtf)
        {
        
        for (int j=0; j < d.classes.length; j++) 
            System.out.println(d.classes[j]); 
        
        int errors=0;
        for (Example i : ex)
            {
            if (node.classify(i, mtf) != i.classification)
                {
                errors++;
                System.err.println("error in classification of:");
                // this version of toString(Domain) was commented out in Example.java 
                // but is invoked here, why ??
                // System.err.println(i.toString(d)); //<-- this line has problem
                System.err.println("should be "+ d.classes[i.classification] + " but is "+d.classes[node.classify(i, mtf)]);
                System.err.println(node.classify(i, mtf) + " \t" + i.classification); 
                System.err.println(d.classes[1] + "\t" + d.classes[2]); 
                System.err.println(d.classes[i.classification] + "\t" + d.classes[node.classify(i, mtf)]); 
                System.err.println(); 
                //throw new RuntimeException("Error in classification!");
                System.exit(-1); 
                }
            }
        if(errors==0)
            System.err.println("Test passed!");
        else
            System.err.println("Test failed with "+errors+ " errors!");
        }
    }
