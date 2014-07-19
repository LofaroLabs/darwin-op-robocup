package sim.app.horde.features;
import sim.app.horde.agent.Agent;
import sim.app.horde.agent.SimAgent;
import sim.app.horde.behaviors.*;
import sim.app.horde.*;

public class DistanceTraveled extends Feature
    {
    private static final long serialVersionUID = 1;

    public static String getType() { return sim.app.horde.agent.SimAgent.TYPE_SIMAGENT; }

    public DistanceTraveled ()
        {
        super("DistanceTraveled");
        }

    public double getValue (Agent agent, Macro parent, Horde horde)
        {
        SimAgent simagent = (SimAgent) agent;
        return simagent.getDistanceTraveled();
        }
    }
