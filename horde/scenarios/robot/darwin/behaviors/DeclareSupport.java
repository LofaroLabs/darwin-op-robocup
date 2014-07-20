/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.horde.scenarios.robot.darwin.behaviors;

import sim.app.horde.Horde;
import sim.app.horde.agent.Agent;
import sim.app.horde.behaviors.Behavior;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.scenarios.robot.darwin.agent.DarwinAgent;

/**
 *
 * @author drew
 */
public class DeclareSupport extends Behavior{
    
    
    private static final long serialVersionUID = 1L;

    
    public static String getType() { return sim.app.horde.agent.SimAgent.TYPE_SIMAGENT; }
    
    public DeclareSupport() {
        name = "Declare Support";
        }
    
    @Override
    public boolean getShouldAddDefaultExample() {
        return false;
    }
    
    @Override
    public void start(Agent agent, Macro parent, Horde horde) {
        super.start(agent, parent, horde);
        ((DarwinAgent) agent).incrementAck();
        ((DarwinAgent) agent).sendMotion(Motions.getDeclare(new int[]{0,1,0}));
        }
  
}