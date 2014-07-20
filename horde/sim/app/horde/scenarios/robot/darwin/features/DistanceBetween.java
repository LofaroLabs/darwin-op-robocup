/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.horde.scenarios.robot.darwin.features;

import sim.app.horde.Horde;
import sim.app.horde.agent.Agent;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.features.Feature;
import sim.app.horde.scenarios.robot.darwin.agent.DarwinAgent;
import sim.app.horde.scenarios.robot.darwin.agent.Real;
import sim.app.horde.targets.Me;
import sim.app.horde.targets.Target;
import sim.app.horde.targets.Wrapper;
import sim.util.Double2D;

/**
 * Distance between two reals
 * @author drew
 */
public class DistanceBetween extends Feature{

    private static final long serialVersionUID = 1;

    public static String getType() { return sim.app.horde.agent.SimAgent.TYPE_SIMAGENT; }
    
    public DistanceBetween() {
        super("DistanceBetween");
        targets = new Target[2];
        targets[0] = new Me();  //  default
        targets[1] = new Me();  //  default
        targetNames = new String[]{ "X", "Y" };// the other guy who I want to know about
        }

    
    
    @Override
    public double getValue(Agent agent, Macro parent, Horde horde) {
        
        Target target = targets[0];
        if (target instanceof Wrapper)
                target = ((Wrapper)target).getTopParameter(parent);
        
        Target target2 = targets[1];
        if (target2 instanceof Wrapper)
                target2 = ((Wrapper)target).getTopParameter(parent);
        
        if (target instanceof Real) {
            // this should be for example a goal like AttackingGoalTarget
            Double2D targetLoc = ((Real)target).getRealTargetLocation(agent, parent, horde);
            Double2D otherLoc = ((Real)target2).getRealTargetLocation(agent, parent, horde);
            System.err.println("Distance between goal and ball = " + otherLoc.distance(targetLoc));
            return otherLoc.distance(targetLoc);
        }
        // else don't know so return 0.
        return 0.0;
        }
    }