package sim.app.horde.scenarios.forage.hardcoded;

import sim.app.horde.*;
import sim.app.horde.agent.Agent;
import sim.app.horde.agent.SimControllerAgent;
import sim.app.horde.behaviors.*;
import sim.app.horde.features.Feature;
import sim.app.horde.scenarios.forage.*;
import sim.app.horde.targets.Target;
import sim.util.Double2D;

public class CodedHorde extends ForageHorde
    {
    private static final long serialVersionUID = 1L;
        
    int[] initAgents = new int[] { 5, 2, 2, 2, 1} ; 
        
    public Agent buildNewAgent()
        {
        CodedAgent s = new CodedAgent(this);
        s.paint = foragerColors[colorIndex];
        s.setRank(colorIndex); 
        s.stoppable = schedule.scheduleRepeating(s,1, 1);
        return s;
        }

    static int cnt=0;
        
    public SimControllerAgent buildNewControllerAgent(String name)
        {
        CodedSupervisor s = new CodedSupervisor(this, name);
        // s.setPose(new Double2D(10, 50 + level*5*cnt++), 0);
        s.stoppable = schedule.scheduleRepeating(s, 0, 1);              
        return s;
        }

    public CodedHorde(long seed)
        {
        super(seed);
        //setInitNumAgentsPer(initAgents); 
        }

    public void start()
        {
        initParameters();
        super.startBypass(); // calls SimHorde.start()
        initFields(5);
                
        buildAgents(INIT_NUM_AGENTS_PER.length - 1, null);
                
        collector = new DataCollection(this, "coded-data-", INIT_NUM_AGENTS_PER); 
        collector.stoppable = schedule.scheduleRepeating(collector, 2, 1);
        }

    // setup the features array with the features we want. Features are bound to targets of interest.
    public static Feature[] initFeatures(InterestingFeature[] interestedFeatures)
        {
        // bind the features to the targets of interest
        Feature[] features = new Feature[interestedFeatures.length];

        try {
            for (int i = 0; i < interestedFeatures.length; i++) {
                InterestingFeature f = interestedFeatures[i];
                Feature tmpFeature = (Feature) f.feature.newInstance();
                if (f.target != null) {
                    Target tmpTarget = (Target) f.target.newInstance();
                    tmpFeature.setTarget(0, tmpTarget);
                    }
                features[i] = tmpFeature;
                }
            } catch (Exception e) {
            e.printStackTrace();
            }

        return features;

        }
        
    public TrainableMacro getTrainingMacro()
        {
        if (trainingAgent == null)return null; 
        Macro m = (Macro)trainingAgent.getBehavior(); 
        TrainableMacro tm = new TrainableMacro("Forager"); 
        tm.behaviors = m.behaviors;
        return tm; 
        }
        
        
    public static void main(String[] args)
        {
        doLoop(CodedHorde.class, args);
        System.exit(0);
        } 
        
    }
