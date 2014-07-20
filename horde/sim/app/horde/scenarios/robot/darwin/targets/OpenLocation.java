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
 *
 * @author drew
 */
public class OpenLocation extends Target implements Real{
    
    // seems to be important to have the same so setting it to 1
    private static final long serialVersionUID = 1;
    Double2D positiveOpen = new Double2D(1.8, -1);
    Double2D negativeOpen = new Double2D(-1.8, -1);
    
    
    @Override
    public String toString() {
        return "Open Location (+/-1.8, -1)"; 
    }

    @Override
    public Double2D getLocation(Agent agent, Macro parent, Horde horde) {
        DarwinParser dp = ((DarwinAgent) agent).getCurrentData();
        if(dp.getAttackingGoalSign() > 0) {
            
            return positiveOpen;
        }
        else {
            
            return negativeOpen;
        }
    }
    @Override
    public Double2D getRealTargetLocation(Agent agent, Macro parent, Horde horde) {
        DarwinParser dp = ((DarwinAgent) agent).getCurrentData();
        if(dp.getAttackingGoalSign() > 0) {
            
            return positiveOpen;
        }
        else {
            
            return negativeOpen;
        }
    }
    
}
