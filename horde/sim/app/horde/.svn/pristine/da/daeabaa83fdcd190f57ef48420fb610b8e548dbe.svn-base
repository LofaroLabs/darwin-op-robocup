package sim.app.horde.scenarios.forage.hardcoded.behaviors;

import sim.app.horde.*;
import sim.app.horde.agent.Agent;
import sim.app.horde.features.Feature;
import sim.app.horde.scenarios.forage.hardcoded.*;

public class Wander extends CodedBehavior
    {
    private static final long serialVersionUID = 1L;

    public Wander()
        {
        super(new Feature[0], CodedAgent.homeInBehaviors );
        name = "Wander";
        }

    public int getNewBehavior(Agent agent, Horde horde, double[] currentFeatureVector)
        {
        double d = horde.random.nextDouble(); 
        if (d < 0.2)
            return CodedAgent.ROTATE; 
        else if (d > 0.8) 
            return CodedAgent.ROTATE_NEG; 
                
        return CodedAgent.FORWARD; 
        }
    }
