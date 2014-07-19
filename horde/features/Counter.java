package sim.app.horde.features;

import sim.app.horde.*;
import sim.app.horde.agent.Agent;
import sim.app.horde.behaviors.*;

public class Counter extends Feature
    {
    private static final long serialVersionUID = 1;
        
    public static String getType() { return sim.app.horde.agent.Agent.TYPE_GLOBAL; }

    public Counter()
        {
        super("Counter"); 
        }
        
    public double getValue(Agent agent, Macro parent, Horde horde)
        {
        return parent.getCounter(Macro.COUNTER_BASIC); 
        }
    }
