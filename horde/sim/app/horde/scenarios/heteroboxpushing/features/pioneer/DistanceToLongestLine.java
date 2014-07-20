package sim.app.horde.scenarios.heteroboxpushing.features.pioneer;

import sim.app.horde.*;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.features.*;
import sim.app.horde.scenarios.heteroboxpushing.pioneer.PioneerAgent;

public class DistanceToLongestLine extends Feature
    {

    private static final long       serialVersionUID        = 1L;

    public DistanceToLongestLine()
        {
        super("DistanceToLongestLine");
        }

    public double getValue(Agent agent, Macro parent, Horde horde)
        {
        PioneerAgent pAgent = (PioneerAgent) agent;
        return pAgent.laser.getCurrentData().distanceToLongestLine;
        }

    }
