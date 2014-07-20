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
import sim.app.horde.scenarios.robot.darwin.comm.DarwinParser;
import sim.util.Double2D;

/**
 *
 * @author drew
 */
public class Defer extends Behavior{
    
    
    private static final long serialVersionUID = 1L;

    
    public static String getType() { return sim.app.horde.agent.SimAgent.TYPE_SIMAGENT; }
    
    public Defer() {
        name = "Defer";
        }
    
    
    @Override
    public void start(Agent agent, Macro parent, Horde horde) {
        super.start(agent, parent, horde);
        ((DarwinAgent) agent).incrementAck();
        
        
        DarwinParser dp = ((DarwinAgent) agent).getCurrentData();
        int signX = (int) (dp.getClosestToBallLocX() / Math.abs(dp.getClosestToBallLocX()));
        int signY = (int) (dp.getClosestToBallLocY() / Math.abs(dp.getClosestToBallLocY()));
        Double2D facing = new Double2D(dp.getClosestToBallLocX(), dp.getClosestToBallLocY());
        Double2D gotoPose = new Double2D(.75 * signX, .75 * signY);
        
        ((DarwinAgent) agent).sendMotion(Motions.getDefer(gotoPose, facing));
        
        }
  
}
