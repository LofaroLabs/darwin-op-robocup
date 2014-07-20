/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.horde.scenarios.robot.darwin.features;

import sim.app.horde.Horde;
import sim.app.horde.Targetable;
import sim.app.horde.agent.Agent;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.features.CategoricalFeature;
import sim.app.horde.features.Feature;
import sim.app.horde.scenarios.robot.darwin.agent.DarwinAgent;
import sim.app.horde.scenarios.robot.darwin.agent.Real;
import sim.app.horde.scenarios.robot.darwin.comm.DarwinParser;
import sim.app.horde.scenarios.robot.darwin.targets.GoalTarget;
import sim.app.horde.targets.Me;
import sim.app.horde.targets.Target;
import sim.app.horde.targets.Wrapper;
import sim.util.Double2D;

/**
 *
 * @author drew
 */
public class DistanceToTarget  extends Feature{

    private static final long serialVersionUID = 1;

    public static String getType() { return sim.app.horde.agent.SimAgent.TYPE_SIMAGENT; }
    
    public DistanceToTarget() {
        super("DistanceToTarget");
        targets = new Target[1];
        targets[0] = new Me();  //  default
        targetNames = new String[]{ "X" };// the other guy who I want to know about
        }

    
    
    @Override
    public double getValue(Agent agent, Macro parent, Horde horde) {
        
        Target target = targets[0];
        if (target instanceof Wrapper)
                target = ((Wrapper)target).getTopParameter(parent);
        
        if (target instanceof Real) {
            // this should be for example a goal like AttackingGoalTarget
            Double2D targetLoc = ((Real)target).getRealTargetLocation(agent, parent, horde);
            Double2D myLoc = ((DarwinAgent) agent).getRealTargetLocation(agent, parent, horde);
            
            return myLoc.distance(targetLoc);
        }
        // else don't know so return 0.
        return 0.0;
        }
    }