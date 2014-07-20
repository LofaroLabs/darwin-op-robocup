package sim.app.horde.scenarios.forage.hardcoded.behaviors;

import sim.app.horde.agent.Agent;
import sim.app.horde.features.*;
import sim.app.horde.scenarios.forage.hardcoded.*;
import sim.app.horde.*;

public class Goto extends CodedBehavior
    {
    private static final long serialVersionUID = 1L;

    public Goto(Feature[] f, String n)
        {
        super(f, CodedAgent.homeInBehaviors);
        name = "Goto" + n;
        }

    public int getNewBehavior(Agent agent, Horde horde, double[] currentFeatureVector)
        {
        return CodedAgent.homeIn(currentFeatureVector[0]);
        }
    }
