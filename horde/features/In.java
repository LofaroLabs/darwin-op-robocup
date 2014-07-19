package sim.app.horde.features;

import sim.app.horde.Horde;
import sim.app.horde.Targetable;
import sim.app.horde.agent.Agent;
import sim.app.horde.agent.SimAgent;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.targets.Me;
import sim.app.horde.targets.Target;

/**
 *
 * @author sean
 */
public class In extends CategoricalFeature
    {
    private static final long serialVersionUID = 1;

    public static String getType() { return sim.app.horde.agent.SimAgent.TYPE_SIMAGENT; }

    public In()
        {
        super("In", new String[] {"Not In", "In"});
        targets = new Target[1];
        targets[0] = new Me();  //  default
        targetNames = new String[]{ "X" };
        }
                
    public double getValue(Agent agent, Macro parent, Horde horde)
        {
        SimAgent simagent = (SimAgent)agent;
        Targetable targetable = targets[0].getTargetable(agent, parent, horde);
        if (targetable == null) return 0.0;
        else return targetable.getTargetIntersects(agent, horde, simagent.getLocation(), 0) ? 1.0 : 0.0;
        }
    }
