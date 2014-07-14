/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.horde.scenarios.robot.darwin.targets;

import sim.app.horde.Horde;
import sim.app.horde.agent.Agent;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.scenarios.robot.darwin.agent.Real;
import sim.app.horde.targets.Target;
import sim.util.Double2D;

/**
 *
 * @author drew
 */
public class SafetyLocation extends Target implements Real{
    
    // seems to be important to have the same so setting it to 1
    private static final long serialVersionUID = 1;
    Double2D center = new Double2D(0, 0);
    
    @Override
    public String toString() {
        return "Safety Location (0,0)"; 
    }

    @Override
    public Double2D getLocation(Agent agent, Macro parent, Horde horde) {
        return center;
    }
    @Override
    public Double2D getRealTargetLocation(Agent agent, Macro parent, Horde horde) {
        return center;
    }
    
}
