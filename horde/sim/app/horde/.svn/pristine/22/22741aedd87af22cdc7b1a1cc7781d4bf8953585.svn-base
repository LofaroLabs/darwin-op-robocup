/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.horde.scenarios.robot.darwin.behaviors;

import sim.app.horde.Horde;
import sim.app.horde.Targetable;
import sim.app.horde.agent.Agent;
import sim.app.horde.behaviors.Behavior;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.scenarios.robot.darwin.agent.DarwinAgent;
import sim.app.horde.scenarios.robot.darwin.agent.Real;
import sim.app.horde.scenarios.robot.darwin.comm.DarwinParser;
import sim.app.horde.targets.Me;
import sim.app.horde.targets.Target;
import sim.app.horde.targets.Wrapper;
import sim.util.Double2D;

/**
 *  Moves to a specified location while facing another specified location 
 * (location = (x,y) global coord)
 * @author drew
 */
public class GotoPoseWhileLookingBackwards extends Behavior {
    private static final long serialVersionUID = 1L;

    
    private boolean started = false;
    public GotoPoseWhileLookingBackwards() 
    	{
        name = "GotoPoseWhileLookingBackwards";
        }
    
    public GotoPoseWhileLookingBackwards(Target where, Target facing) 
    	{
    	this();
        }
    
    public static String getType() { return sim.app.horde.agent.SimAgent.TYPE_SIMAGENT; }
   
    
    /*
  +-------------+
  |+-----+-----+|
  ||     |     ||
  |+-----C-----+|
  ||     |     ||
  |B-----+-----+|
  A-------------+

  In Horde World, units of measure are in cm
  In Horde World, A is (0,0), B is (2.6, 2.0), and C is (30.0, 20.0)
  In Real World, A is (-30.0, -20.0), B is (-27.4, -18.0), and C is (0.0, 0.0)
*/
    @Override
    public void start(Agent agent, Macro parent, Horde horde) {
        super.start(agent, parent, horde);
        
        
        ((DarwinAgent) agent).incrementAck();
        
        DarwinParser dp = ((DarwinAgent) agent).getCurrentData();
        Double2D facing = new Double2D(dp.getClosestToBallLocX(), dp.getClosestToBallLocY());
        Double2D gotoPose = new Double2D(dp.getPenaltyBoundsX(), (facing.y < dp.getPenaltyBoundsY()) ? facing.y : dp.getPenaltyBoundsY());
        ((DarwinAgent) agent).sendMotion(Motions.gotoPoseWhileLookingBackwards(gotoPose, facing));
        started = true;
    }

    @Override
    public void go(Agent agent, Macro parent, Horde horde) {
        super.start(agent, parent, horde);
        if(started) {
            started = false;
            return;
        }
        if (!started) {
            ((DarwinAgent) agent).incrementAck();

            DarwinParser dp = ((DarwinAgent) agent).getCurrentData();
            Double2D facing = new Double2D(dp.getClosestToBallLocX(), dp.getClosestToBallLocY());
            Double2D gotoPose = new Double2D(dp.getPenaltyBoundsX(), (facing.y < dp.getPenaltyBoundsY()) ? facing.y : dp.getPenaltyBoundsY());
            ((DarwinAgent) agent).sendMotion(Motions.gotoPoseWhileLookingBackwardsUpdate(gotoPose, facing));
        }
    }
    
    
    
    
}
