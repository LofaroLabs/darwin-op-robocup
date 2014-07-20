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
 * Defending goal starts on its right so when defending goal is positive
 * then 
 * @author drew
 */
public class DefenderReadyPosition extends Target implements Real{
    
    // seems to be important to have the same so setting it to 1
    private static final long serialVersionUID = 1;
    
    
    Double2D positiveDefenderReadyPos = new Double2D(13.0, 7.0);
    Double2D negativeDefenderReadyPos = new Double2D(-13.0, -7.0);
    
    Double2D realPositiveDefenderReadyPos = new Double2D(1.3, 0.7);
    Double2D realNegativeDefenderReadyPos = new Double2D(-1.3, -0.7);
    
    
    @Override
    public String toString() {
        return "Defender Ready Position (+/-1.3, +/-.7)"; // this is the position between the robot and the goal post
    }

    @Override
    public Double2D getLocation(Agent agent, Macro parent, Horde horde) {
        DarwinParser dp = ((DarwinAgent) agent).getCurrentData();
        if(dp.getDefendingGoalSign() > 0) {
            // defending positive so walk to posive side
            return positiveDefenderReadyPos;
        }
        else {
            // otherwise go to the negative side.
            return negativeDefenderReadyPos;
        }
    }
    @Override
    public Double2D getRealTargetLocation(Agent agent, Macro parent, Horde horde) {
        DarwinParser dp = ((DarwinAgent) agent).getCurrentData();
        if(dp.getDefendingGoalSign() > 0) {
            
            return realPositiveDefenderReadyPos;
        }
        else {
            
            return realNegativeDefenderReadyPos;
        }
    }
    
}
