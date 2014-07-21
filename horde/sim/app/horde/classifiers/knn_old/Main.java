package sim.app.horde.classifiers.knn_old;

import sim.app.horde.classifiers.*; 

public class Main
    {

    final int NUM_EXAMPLES = 2; 
    final int NUM_DIMENSIONS = 2; 
        
        
    public static void main(String args[])
        {
        Main m = new Main(); 
        m.run(); 
        }
        
    void run()
        {                                               
        Example examples[] = new Example[NUM_EXAMPLES]; 
        for (int i=0; i < NUM_EXAMPLES; i++) {
            double vals[] = new double[NUM_DIMENSIONS]; 
            if (i < NUM_EXAMPLES/2) { 
                vals[0] = 1; 
                vals[1] = 1; 
                }
            else { 
                vals[0] = 3; 
                vals[1] = 3; 
                }
            examples[i] = new Example(vals); 
            examples[i].classification = i; 
        
            }
                
                
        KNN knn = new KNN("sim.app.horde.classifiers.knn.UnweightedSqEuclidianDistance");
        knn.learn(examples, null); 
                        
        Example e = new Example(new double[]{ 3.5, 3.5}); 
        System.out.println("Class: " + knn.classify(e, null));
                        
                
        }
        
    }
