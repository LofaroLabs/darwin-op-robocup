package sim.app.horde.behaviors;

import sim.app.horde.*;
import sim.app.horde.agent.Agent;
import sim.app.horde.agent.SimAgent;

/**
 *
 * @author josephzelibor
 */
public class Stop extends Behavior
    {
    private static final long serialVersionUID = 1;

    // I am designed for sim agents
    public static String getType() { return sim.app.horde.agent.SimAgent.TYPE_SIMAGENT; }

    public Stop ()
        {
        name = "Stop";
        setKeyStroke(KS_DOWN);
        }

    public void go(Agent agent, Macro parent, Horde horde)
        {
        super.start(agent, parent, horde);
                
        SimAgent simagent = (SimAgent) agent;
        //stay at this location
        simagent.setLocation(simagent.getLocation());
        //stopping resets distance traveled
        simagent.resetDistanceTraveled();
        }
    }
