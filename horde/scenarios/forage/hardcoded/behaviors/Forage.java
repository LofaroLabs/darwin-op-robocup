package sim.app.horde.scenarios.forage.hardcoded.behaviors;

import sim.app.horde.agent.Agent;
import sim.app.horde.behaviors.*;
import sim.app.horde.scenarios.forage.hardcoded.*;
import sim.app.horde.*;
import sim.app.horde.scenarios.forage.behaviors.*;

public class Forage extends CodedBehavior
    {

    private static final long serialVersionUID = 1L;

    int CAN_SEE_BOX = 0;
    int IS_ATTACHED = 1;
    int DIST_CLOSEST_BOX = 2;
    int DONE = 3; 

    int HUNT, RETURN_BOX, GRAB, DONE_BEHAVIOR;

    static InterestingFeature[] interestedFeatures = {
        new InterestingFeature(sim.app.horde.scenarios.forage.features.CanSeeBox.class, null),
        new InterestingFeature(sim.app.horde.scenarios.forage.features.IsAttached.class, null),
        new InterestingFeature(sim.app.horde.features.TargetDistance.class,
            sim.app.horde.scenarios.forage.targets.ClosestBox.class),
        new InterestingFeature(sim.app.horde.features.Done.class, null) };

    public Forage()
        {
        super(CodedHorde.initFeatures(interestedFeatures), new Behavior[] { new Hunt(), new Grab(), new ReturnBox(),
                                                                            new Done() });
        name = "Forage";

        HUNT = indexOfBehaviorNamed("Hunt"); 
        GRAB = indexOfBehaviorNamed("Grab"); 
        RETURN_BOX = indexOfBehaviorNamed("ReturnBox");
        DONE_BEHAVIOR = indexOfBehaviorNamed("Done");
        }

    public int getNewBehavior(Agent agent, Horde horde, double[] currentFeatureVector)
        {
        if (currentFeatureVector[DONE] > 0) 
            return DONE_BEHAVIOR; 
                
        if (currentFeatureVector[IS_ATTACHED] > 0) // got a box
            return RETURN_BOX;

        // i'm close, and can see it, so grab box
        if (currentFeatureVector[CAN_SEE_BOX] > 0 && currentFeatureVector[DIST_CLOSEST_BOX] < CodedAgent.GRAB_RANGE)
            return GRAB;

        // hunt for a box to grab
        return HUNT;
        }
    }
