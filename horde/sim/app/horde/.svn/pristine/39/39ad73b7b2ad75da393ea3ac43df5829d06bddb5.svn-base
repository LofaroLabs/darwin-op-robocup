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
 * The midpoint between the ball (as seen by the bot that is closest to the ball)
 * and the goal post furthest from the ball.
 * @author drew
 */
public class MidpointLocation extends Target implements Real{

    
    private static final long serialVersionUID = 1L;
    
    
    @Override
    public String toString() {
        return "Midpoint location"; 
    }

    @Override
    public Double2D getLocation(Agent agent, Macro parent, Horde horde) {
        
        DarwinParser dp = ((DarwinAgent) agent).getCurrentData();
        return new Double2D(dp.getMidpointX() * 10, dp.getMidpointY() * 10);
    }
    @Override
    public Double2D getRealTargetLocation(Agent agent, Macro parent, Horde horde) {
        
        DarwinParser dp = ((DarwinAgent) agent).getCurrentData();
        return new Double2D(dp.getMidpointX(), dp.getMidpointY());
    }
}