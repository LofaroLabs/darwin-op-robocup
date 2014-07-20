package sim.app.horde.features;

import sim.app.horde.*;
import sim.app.horde.targets.*;
import sim.app.horde.agent.Agent;
import sim.app.horde.agent.SimAgent;
import sim.app.horde.behaviors.*;
import sim.util.*;

/** Returns the ratio of the distance squared between the agent and two other targets. */

public class Closer extends CategoricalFeature
    {
    private static final long serialVersionUID = 1;

    public static String getType() { return sim.app.horde.agent.SimAgent.TYPE_SIMAGENT; }

    public Closer()
        {
        super("Closer", new String[] { "0", "1" });
        targets = new Target[2];
        targets[0] = new Me();  //  default
        targets[1] = new Me();  //  default
        targetNames = new String[]{ "0", "1" };
        }
                                
    public double getValue(Agent agent, Macro parent, Horde horde)
        {
        SimAgent simagent = (SimAgent) agent;
                
        Double2D loc = simagent.getLocation();
        double d1 = loc.distanceSq(targets[0].getLocation(simagent, parent, horde));
        double d2 = loc.distanceSq(targets[1].getLocation(simagent, parent, horde));
        return (d1 <= d2) ? 0.0 : 1.0;
        }
    }
