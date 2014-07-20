/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.horde.scenarios.robot.darwin.targets;

import sim.app.horde.Horde;
import sim.app.horde.agent.Agent;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.scenarios.robot.darwin.agent.DarwinAgent;
import sim.app.horde.scenarios.robot.darwin.agent.Real;
import sim.app.horde.scenarios.robot.darwin.comm.DarwinParser;
import sim.app.horde.targets.Target;
import sim.util.Double2D;

/**
 * (0, closestToBallY)
 * @author drew
 */
public class DynamicPassWaitFacing extends Target implements Real{
    
    
    // seems to be important to have the same so setting it to 1
    private static final long serialVersionUID = 1;
    
  
    @Override
    public String toString() {
        return "Dynamic PassWait Facing x = 0 y = closestToBallY"; 
    }

    @Override
    public Double2D getLocation(Agent agent, Macro parent, Horde horde) {
        DarwinParser dp = ((DarwinAgent) agent).getCurrentData();
        return new Double2D(0, dp.getClosestToBallLocY());
    }
    @Override
    public Double2D getRealTargetLocation(Agent agent, Macro parent, Horde horde) {
        DarwinParser dp = ((DarwinAgent) agent).getCurrentData();
        return new Double2D(0, dp.getClosestToBallLocY());
    }
    
}
