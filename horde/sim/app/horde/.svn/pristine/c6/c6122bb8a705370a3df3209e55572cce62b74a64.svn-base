/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.horde.scenarios.boxpushing.behaviors.flockbot;

import sim.app.horde.agent.Agent;
import sim.app.horde.Horde;
import sim.app.horde.behaviors.Behavior;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.scenarios.robot.flockbots.FlockbotAgent;
import sim.app.horde.scenarios.robot.flockbots.behaviors.Motions;

/**
 *
 * @author drew
 */
public class ForwardHundred extends Behavior {

    private static final long serialVersionUID = 1L;

    public ForwardHundred() {
        name = "forward-100";
    }
    
    
    
    @Override
    public void start(Agent agent, Macro parent, Horde horde) {
        super.start(agent, parent, horde);
        ((FlockbotAgent) agent).sendMotion(Motions.FORWARD, (byte) 100);
    }
    
    
    
}
