package sim.app.horde.features;
import sim.app.horde.*;
import sim.app.horde.agent.Agent;
import sim.app.horde.agent.SimAgent;
import sim.app.horde.behaviors.*;

/** Returns true if the agent has become stuck on an obstacle (he tried to move forward and was unable to do so. */

public class Stuck extends CategoricalFeature
    {
    private static final long serialVersionUID = 1;

    public static String getType() { return sim.app.horde.agent.SimAgent.TYPE_SIMAGENT; }
        
    public Stuck()
        { super("Stuck", new String[] {"Not Stuck", "Stuck"}); }

    public double getValue(Agent agent, Macro parent, Horde horde)
        {
        SimAgent simagent = (SimAgent) agent;
        return (simagent.getStuck() ? 1 : 0); 
        }
    }