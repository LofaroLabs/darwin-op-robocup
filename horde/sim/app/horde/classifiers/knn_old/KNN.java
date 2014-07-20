package sim.app.horde.classifiers.knn_old;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.util.*;

import sim.app.horde.classifiers.*;
import ec.util.MersenneTwisterFast;
import sim.app.horde.classifiers.Example;
import sim.app.horde.classifiers.knn.KDTree.Entry;


public class KNN extends Classifier
    {
    private static final long serialVersionUID = 1L;
        
    // how many neighbors should we use?  this is K. 
    int numNeighbors = 1; 
        
    // the FULL package name of the particular KDTree to use.  The particular tree 
    // handles the distance comparison.  
    String treeClass; 
        
    // K-d tree object for determining the K nearest neighbors 
    private KDTree kdTree; 
        
    public int getNumNeighbors() { return numNeighbors; } 
    public void setNumNeighbors(int n) { numNeighbors = n; } 
        
    public Object clone()
        {
        KNN k = (KNN)super.clone(); 
        k.numNeighbors = numNeighbors;
        k.treeClass = treeClass; 
        k.kdTree = (KDTree)kdTree.clone(); 
                
        return k; 
        }
        
    public KNN(String cls)
        {
        this(cls, 1); 
        }

    public KNN(String cls, int numNeighbors) 
        {
        treeClass = cls; 
        this.numNeighbors = numNeighbors; 
        }
        
    // comparator used to determine majority index of nearest neighbors 
    public Comparator<Map.Entry<Integer, Integer>> comparator = new Comparator<Map.Entry<Integer, Integer>>()
        {
        public int compare (Map.Entry<Integer, Integer> obj1, Map.Entry<Integer, Integer> obj2) 
            {
            return obj1.getValue().compareTo(obj2.getValue()); 
            }
        };
        
    public int classify(Example e, MersenneTwisterFast random)
        {
        if (kdTree == null) 
            throw new RuntimeException("Called KNN.classify with null KDTree!");
                
        // get the K nearest neighbors, according to our kd-tree 
        List<Entry> pts = kdTree.nearestNeighbor(e.values, numNeighbors, true); 
                
                
        // compute a histogram of occurrences of each class index   
        HashMap<Integer, Integer> counts = new HashMap<Integer, Integer>(); 
                
        for (int i=0; i < pts.size(); i ++) { 
            Integer key = pts.get(i).value; 
            int in=1; 
            if (counts.containsKey(key)) { 
                in = counts.get(key).intValue();
                in++; 
                }
            counts.put(key, in);
            }               
                
        // get the class index which occurred most frequently 
        Set<Map.Entry<Integer, Integer>> s = counts.entrySet(); 
        Map.Entry<Integer, Integer> m = Collections.max(s, comparator); 
        return m.getKey(); 
        }

    public void write(PrintWriter writer)
        {
        // TODO Auto-generated method stub
                
        }

    public void writeDotFile(String file, String label) throws IOException
        {
        // TODO Auto-generated method stub
                
        }

    public void writeClassifier(PrintWriter writer, boolean writeDomain)
        {
        // TODO Auto-generated method stub
                
        }

    public String toString()
        {
        // TODO Auto-generated method stub
        return null;
        }

    // creates the particular kd-tree.  The number of dimensions (the D) is 
    // determined from the size of the feature vector of the first example.  
    public void learn(Example[] examples, MersenneTwisterFast random)
        {
        if (examples == null || examples.length == 0) 
            throw new RuntimeException("Called KNN.learn with no examples!"); 
                                
        try
            {
            Class<? extends KDTree> c = Class.forName(treeClass, true, Thread.currentThread().getContextClassLoader()).asSubclass(KDTree.class);
            Constructor<? extends KDTree> con = c.getConstructor(new Class[]{int.class, Integer.class});
            kdTree = con.newInstance(examples[0].values.length, null); 
            } catch (Exception e)
            {
            System.err.println("Failed to learn KNN" + e); 
            e.printStackTrace();
            } 
                
        for (int i=0; i < examples.length; i++) 
            kdTree.addPoint(examples[i].values, examples[i].classification); 
        }

    }
