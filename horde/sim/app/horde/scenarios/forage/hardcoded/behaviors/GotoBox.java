package sim.app.horde.scenarios.forage.hardcoded.behaviors;

import sim.app.horde.scenarios.forage.hardcoded.*;

public class GotoBox extends Goto
    {
    private static final long serialVersionUID = 1L;

    static InterestingFeature[] interestedFeatures = { new InterestingFeature(
            sim.app.horde.features.TargetDirection.class, sim.app.horde.scenarios.forage.targets.ClosestBox.class) };

    public GotoBox()
        {
        super(CodedHorde.initFeatures(interestedFeatures), "Box");
        }
    }
