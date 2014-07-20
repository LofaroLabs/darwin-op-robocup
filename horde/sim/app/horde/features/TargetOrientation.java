package sim.app.horde.features;

import sim.app.horde.*;
import sim.app.horde.targets.*;
import sim.app.horde.agent.Agent;
import sim.app.horde.behaviors.*;
/**
 *
 * @author vittorio
 */
public class TargetOrientation extends ToroidalFeature
    {
    private static final long serialVersionUID = 1;
    public static String getType() { return sim.app.horde.agent.SimAgent.TYPE_SIMAGENT; }
    public TargetOrientation(Target t)
        {
        this();
        targets[0] = t;
        }

    public TargetOrientation()
        {
        super("OrientationOf");
        targets = new Target[1];
        targets[0] = new Me();  //  default
        targetNames = new String[]{ "X" };
        }
        
    public double getValue(Agent agent, Macro parent, Horde horde)
        {
        return targets[0].getOrientation(agent, parent, horde);
        }
    }
