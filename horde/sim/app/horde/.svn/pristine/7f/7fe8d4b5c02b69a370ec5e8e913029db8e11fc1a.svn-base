/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.horde.scenarios.robot.flockbots.behaviors;

import sim.app.horde.agent.Agent;
import sim.app.horde.Horde;
import sim.app.horde.behaviors.Behavior;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.scenarios.robot.flockbots.FlockbotAgent;

/**
 *
 * @author drew
 */
public class TurnLeft extends Behavior{
    private static final long serialVersionUID = 1L;

    public TurnLeft() {
        name = "left";
    }

    @Override
    public void start(Agent agent, Macro parent, Horde horde) {
        //To change body of generated methods, choose Tools | Templates.
        super.start(agent, parent, horde);
        ((FlockbotAgent) agent).sendMotion(Motions.LEFT);
    }
    
    
    
}
