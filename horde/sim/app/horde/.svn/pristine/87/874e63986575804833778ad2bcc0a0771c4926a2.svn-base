package sim.app.horde.classifiers.obliquedecisiontree;

import java.io.File;
import java.io.IOException;

import sim.app.horde.classifiers.Domain;
import sim.app.horde.classifiers.Example;
import sim.app.horde.classifiers.decisiontree.Node;
import sim.util.Bag;
import ec.util.MersenneTwisterFast;

public class Main
    {
    private static final long serialVersionUID = 1;

    public static void main(String[] args) throws IOException, InterruptedException
        {
        if (args.length < 2)
            {
            System.err
                .println("Usage: java sim.app.horde.classifier.obliquedecisiontree.Main <dom file> <exemplar file>");
            System.exit(1);
            }

        MersenneTwisterFast mtf = new MersenneTwisterFast();
        final int MAX_DEPTH = Integer.MAX_VALUE;
        String domFile = args[0];
        if (!domFile.endsWith(".dom")) domFile += ".dom";
        String exemplarFile = args[1];
        if (!exemplarFile.endsWith(".examples")) exemplarFile += ".examples";
        Domain d = Domain.loadDomain(new File(domFile).getCanonicalPath(), mtf);
                
        int NUM_POINTS = 10; 
        Example e[] = new Example[2*NUM_POINTS];
                
        int j=0; 
        for (int i=0; i < 2*NUM_POINTS; i++) { 
            double x = 0 + 10*j; 
            double y = 90 - 10*j; 
                        
            j++; 
            if (i == NUM_POINTS-1)
                j = 0; 
                        
            int c = 1; 
            if (i > NUM_POINTS -1) { 
                y += 10; 
                x += 10; 
                c = 3; 
                }
            x/=100; 
            y/=100; 
                        
            e[i] = new Example(new double[] { x,y }, c);
                        
            //System.out.print(e[i]); 
                        
            }
                
                
        //Example[] e = Example.loadExamples(new File(exemplarFile).getCanonicalPath(), d);
        evaluate(args[1], d, e, MAX_DEPTH, mtf);
                
        }

    private static void evaluate(String name, Domain d, Example[] e, int MAX_DEPTH, MersenneTwisterFast mtf)
        throws IOException, InterruptedException
        {
        int num_train = e.length; 
        Example[] training_set = new Example[num_train];
        Bag b = new Bag(e.length);
        b.addAll(0, e);
        b.shuffle(mtf);
        Object[] shuffled_examples = b.toArray();
        System.arraycopy(shuffled_examples, 0, training_set, 0, num_train);
        System.err.println("Learning...");
        //ObliqueNode node = (ObliqueNode)ObliqueNode.learn(d, training_set, MAX_DEPTH, mtf);
        ObliqueDecisionTree tree = new ObliqueDecisionTree();
        tree.setDomain(d); 
        tree.learn(training_set, mtf); 

        String file = Node.getPathInDirectory("../examples.dot");
        tree.writeDotFile(file, name);
        System.err.println("Writing: " + file);

                
        //System.err.println("Launching...");
        //String cmd = Node.getPathInDirectory("../display.sh");
        //System.err.println(cmd);
        //Runtime run = Runtime.getRuntime();
        //Process pr = run.exec(new String[] { cmd, file });

        System.err.println("Waiting ...");
        //pr.waitFor();
        }

    }
