package sim.app.horde.scenarios.forage.hardcoded.behaviors;

import sim.app.horde.agent.Agent;
import sim.app.horde.behaviors.*;
import sim.app.horde.scenarios.forage.behaviors.*;
import sim.app.horde.scenarios.forage.hardcoded.*;
import sim.app.horde.*;

public class GrabBox extends CodedBehavior
    {
    private static final long serialVersionUID = 1L;

    static InterestingFeature[] interestedFeatures = {
        new InterestingFeature(sim.app.horde.features.TargetDistance.class,
            sim.app.horde.scenarios.forage.targets.ClosestBiggestAttachedAgent.class),
        new InterestingFeature(sim.app.horde.features.TargetDistance.class,
            sim.app.horde.scenarios.forage.targets.HomeBase.class),
        new InterestingFeature(sim.app.horde.scenarios.forage.features.IsAttached.class, null), 
        new InterestingFeature(sim.app.horde.features.Done.class, null)};

    int  GRAB, RETURN_BOX;
    int DIST_HOME = 1;
    int IS_ATTACHED = 2; 
    int DIST_ATTACHED = 0; 
    int DONE = 3; 
        
    int DONE_BEHAVIOR, GOTO_BIGGEST, RELEASE; 

    public GrabBox()
        {
        super(CodedHorde.initFeatures(interestedFeatures), new Behavior[] { new Grab(), new ReturnBox(), new Done()
                                                                            , new GotoBiggestAttached(), new ReleaseDone()});
        name = "GrabBox";

        RETURN_BOX = indexOfBehaviorNamed("ReturnBox");
        GRAB = indexOfBehaviorNamed("Grab");
        DONE_BEHAVIOR = indexOfBehaviorNamed("Done"); 
        RELEASE = indexOfBehaviorNamed("Release") ;
        GOTO_BIGGEST = indexOfBehaviorNamed("GotoBiggest");
        }

    public int getNewBehavior(Agent agent, Horde horde, double[] currentFeatureVector)
        {
                                
        if (currentFeatureVector[DONE] > 0 )  
            return DONE_BEHAVIOR; 
                
        // am i holding on?
        if (currentFeatureVector[IS_ATTACHED] > 0) { 
            return RETURN_BOX; 
            }
        // close to attached agent?
        if (currentFeatureVector[DIST_ATTACHED] < CodedAgent.ATTACHED_RANGE) return GRAB;
                
        return GOTO_BIGGEST; 
        }
        
    public void stop(Agent agent, Macro parent, Horde horde)
        {
        super.stop(agent, parent, horde);
        parent.finished = true; 
        }
    }
