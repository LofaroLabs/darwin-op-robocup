package sim.app.horde.scenarios.forage.hardcoded.behaviors;

import sim.app.horde.scenarios.forage.agent.Forager;
import sim.app.horde.scenarios.forage.behaviors.ReleaseDone;
import sim.app.horde.agent.Agent;
import sim.app.horde.behaviors.*;
import sim.app.horde.scenarios.forage.*;
import sim.app.horde.scenarios.forage.hardcoded.*;
import sim.app.horde.*;

public class ReturnBox extends CodedBehavior
    {
    private static final long serialVersionUID = 1L;

    int IS_ATTACHED = 2;
    int DIST_HOME = 0;
    int DONE = 1;

    int RELEASE_DONE, GOTO_HOME, DONE_BEHAVIOR;
    static InterestingFeature[] interestedFeatures = {
        new InterestingFeature(sim.app.horde.features.TargetDistance.class,
            sim.app.horde.scenarios.forage.targets.HomeBase.class),
        new InterestingFeature(sim.app.horde.features.Done.class, null),
        new InterestingFeature(sim.app.horde.scenarios.forage.features.IsAttached.class, null) };

    public ReturnBox()
        {
        super(CodedHorde.initFeatures(interestedFeatures), new Behavior[] { new GotoHome(), new ReleaseDone(),
                                                                            new Done() });
        name = "ReturnBox";

        RELEASE_DONE = indexOfBehaviorNamed("ReleaseDone");
        GOTO_HOME = indexOfBehaviorNamed("GotoHome");
        DONE_BEHAVIOR = indexOfBehaviorNamed("Done");
        }

    public int getNewBehavior(Agent agent, Horde horde, double[] currentFeatureVector)
        {
        if (currentFeatureVector[DONE] > 0) return DONE_BEHAVIOR;

        if (currentFeatureVector[IS_ATTACHED] == 0 || currentFeatureVector[DIST_HOME] < Forager.HOME_RANGE) 
            return RELEASE_DONE;

        return GOTO_HOME; // too far, so still go home
        }
    }
