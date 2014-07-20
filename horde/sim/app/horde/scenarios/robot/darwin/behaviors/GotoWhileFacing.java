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
import sim.app.horde.targets.Me;
import sim.app.horde.targets.Target;
import sim.app.horde.targets.Wrapper;
import sim.util.Double2D;

/**
 *  Moves to a specified location while facing another specified location 
 * (location = (x,y) global coord)
 * @author drew
 */
public class GotoWhileFacing extends Behavior {
    private static final long serialVersionUID = 1L;

    public GotoWhileFacing() 
    	{
    	targets = new Target[2];
    	targets[0] = new Me();
    	targets[1] = new Me();
    	targetNames = new String[] {"Where", "Facing"};
        name = "GotoWhileFacing";
        }
    
    public GotoWhileFacing(Target where, Target facing) 
    	{
    	this();
    	targets[0] = where;
    	targets[1] = facing;
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
        
        
        
        Targetable targetable = targets[0].getTargetable(agent, parent, horde); // midpoint
        Targetable targetable2 = targets[1].getTargetable(agent, parent, horde);// good it is a attackerTwoAgent
        /*
        System.err.println("Targetable 2: " + targetable2.getClass() + " Target: " + targets[1]);
        
        System.err.println("Is attacker2 a Real? " + (targetable2 instanceof Real));
        System.err.println("Is targets[0] a real " + (targets[0] instanceof Real) + " also targets[0] = " + targets[0]);
        */
        Target target = targets[0];
        Target target2 = targets[1];
        if (target instanceof Wrapper) {
                target = ((Wrapper)target).getTopParameter(parent);
                //System.err.println("First target is a wrapper and is a real? = " + (target instanceof Real));
        }
        if (target2 instanceof Wrapper) {
            target2 =  ((Wrapper)target2).getTopParameter(parent);
            //System.err.println("First target is a wrapper and is a real? = " + (target2 instanceof Real));
        }
        
        //System.err.println("Is targets[0] a real " + (target instanceof Real) + " also targets[0] = " + target.getClass());
        Real rm = null, rm2 = null;

        
        if (target instanceof Real && targetable2 instanceof Real) {
            //System.err.println("First was a real Target and the second was a targetable real");
            rm = (Real)target;
            rm2 = (Real)targetable2;
        }
        
        if (target2 instanceof Real && targetable instanceof Real) {
            //System.err.println("Second was a real Target and the first was a targetable real");
            rm = (Real)targetable;
            rm2 = (Real)target2;
        }
        
        if (targetable instanceof Real &&
        	targetable2 instanceof Real)
        {
            rm = (Real)targetable;
            rm2 = (Real)targetable2;

            
        }
        if (target instanceof Real && target2 instanceof Real) {
            //System.err.println("Both targets not real markers");
            rm = (Real)target;
            rm2 = (Real)target2;
        }
        if (rm != null && rm2 != null) {
            Double2D loc = rm.getRealTargetLocation(agent, parent, horde);
            Double2D loc2 = rm2.getRealTargetLocation(agent, parent, horde);
           // System.err.println("Going to send GotoPose!");

            // .1 meters = 1 decimeter
            // convert to darwin's inertial frame.
            //double x = (loc.x - 30) * 0.1;
            //double y = (loc.y - 20) * 0.1;
            // orientation: Math.atan2(loc2.y - loc.y, loc2.x - loc.x);
            //System.err.println("The Horde coords " + loc.toCoordinates() + "  converted: " + x + " " + y + " orientation: " + orientation);
 
        ((DarwinAgent) agent).incrementAck();
            ((DarwinAgent) agent).sendMotion(Motions.getGotoPoseWhileFacing(loc, loc2));
        }
        
    }

    @Override
    public void go(Agent agent, Macro parent, Horde horde) {
        super.go(agent, parent, horde); //To change body of generated methods, choose Tools | Templates.
        
       
        
        Targetable targetable = targets[0].getTargetable(agent, parent, horde); // midpoint
        Targetable targetable2 = targets[1].getTargetable(agent, parent, horde);// good it is a attackerTwoAgent
        /*
        System.err.println("Targetable 2: " + targetable2.getClass() + " Target: " + targets[1]);
        
        System.err.println("Is attacker2 a Real? " + (targetable2 instanceof Real));
        System.err.println("Is targets[0] a real " + (targets[0] instanceof Real) + " also targets[0] = " + targets[0]);
        */
        Target target = targets[0];
        Target target2 = targets[1];
        if (target instanceof Wrapper) {
                target = ((Wrapper)target).getTopParameter(parent);
            //    System.err.println("First target is a wrapper and is a real? = " + (target instanceof Real));
        }
        if (target2 instanceof Wrapper) {
            target2 =  ((Wrapper)target2).getTopParameter(parent);
      //      System.err.println("First target is a wrapper and is a real? = " + (target2 instanceof Real));
        }
        
        //System.err.println("Is targets[0] a real " + (target instanceof Real) + " also targets[0] = " + target.getClass());
        Real rm = null, rm2 = null;

        
        if (target instanceof Real && targetable2 instanceof Real) {
            //System.err.println("First was a real Target and the second was a targetable real");
            rm = (Real)target;
            rm2 = (Real)targetable2;
        }
        
        if (target2 instanceof Real && targetable instanceof Real) {
            //System.err.println("Second was a real Target and the first was a targetable real");
            rm = (Real)targetable;
            rm2 = (Real)target2;
        }
        
        if (targetable instanceof Real &&
        	targetable2 instanceof Real)
        {
            rm = (Real)targetable;
            rm2 = (Real)targetable2;

            
        }
        if (target instanceof Real && target2 instanceof Real) {
        //    System.err.println("Both targets not real markers");
            rm = (Real)target;
            rm2 = (Real)target2;
        }
        if (rm != null && rm2 != null) {
            Double2D loc = rm.getRealTargetLocation(agent, parent, horde);
            Double2D loc2 = rm2.getRealTargetLocation(agent, parent, horde);
        //    System.err.println("Going to send GotoPose!");

            // .1 meters = 1 decimeter
            // convert to darwin's inertial frame.
            //double x = (loc.x - 30) * 0.1;
            //double y = (loc.y - 20) * 0.1;
            // orientation: Math.atan2(loc2.y - loc.y, loc2.x - loc.x);
            //System.err.println("The Horde coords " + loc.toCoordinates() + "  converted: " + x + " " + y + " orientation: " + orientation);
             
            ((DarwinAgent) agent).incrementAck();
            ((DarwinAgent) agent).sendMotion(Motions.getGotoPoseWhileFacingUpdate(loc, loc2));
        }
        
    }
    
    
    
}
