package sim.app.horde.targets;


import sim.app.horde.*;
import sim.util.Double2D;
import sim.app.horde.agent.Agent;
import sim.app.horde.agent.SimAgent;
import sim.app.horde.behaviors.*;

/**
 *
 * @author josephzelibor
 */
public class MousePointer extends Target
    {
    private static final long serialVersionUID = 1;

    public Double2D getLocation(Agent agent, Macro parent, Horde horde)
        {
        SimHorde simhorde = (SimHorde) horde;
        //no known mouse position just hang tight
        if (simhorde.mouseLoc.equals(null))
            {
            System.out.println("No known mouse position");
            SimAgent simagent = (SimAgent)agent;
            return simagent.getLocation();
            }
        else return simhorde.mouseLoc;
        }

    public String toString()
        {
        return "Mouse Pointer";
        }
    }
