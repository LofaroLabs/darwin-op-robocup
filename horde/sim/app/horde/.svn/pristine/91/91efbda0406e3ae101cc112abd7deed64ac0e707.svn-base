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
public class GoalTarget extends Target implements Real{

    
    private static final long serialVersionUID = 1634623479431775008L;
    
    Double2D goalTarget = new Double2D(27.6, 0);
    //Double2D realGoalTarget = new Double2D(2.76, 0);
    @Override
    public String toString() {
        return "Goal Target: (2.76, 0) in meters"; 
    }

    @Override
    public Double2D getLocation(Agent agent, Macro parent, Horde horde) {
        return goalTarget;
    }
    @Override
    public Double2D getRealTargetLocation(Agent agent, Macro parent, Horde horde) {
        
        
        return new Double2D(2.76, 0);
    }
}
