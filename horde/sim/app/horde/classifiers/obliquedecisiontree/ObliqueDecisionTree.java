package sim.app.horde.classifiers.obliquedecisiontree;


import sim.app.horde.classifiers.*; 
import sim.app.horde.classifiers.decisiontree.*;
import ec.util.MersenneTwisterFast;

public class ObliqueDecisionTree extends DecisionTree
    {
    private static final long serialVersionUID = 1L;

    //protected ObliqueNode root;

    public Object clone()
        {
        ObliqueDecisionTree dt = (ObliqueDecisionTree) super.clone();
        dt.setRoot((ObliqueNode) getRoot().clone());
        return dt;
        }

    // already have checked examples array in LearnedTransition
    public void learn(Example[] examples, MersenneTwisterFast random)
        {
        System.out.println("YO"); 
        setRoot(ObliqueNode.learn(domain, examples, Integer.MAX_VALUE, random));
        System.out.println(getRoot()); 
        }

    }
