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
import sim.app.horde.scenarios.robot.darwin.agent.RealMarker;
import sim.app.horde.scenarios.robot.darwin.comm.DarwinParser;
import sim.app.horde.scenarios.robot.darwin.targets.GoalTarget;
import sim.app.horde.targets.Me;
import sim.app.horde.targets.Target;
import sim.app.horde.targets.Wrapper;
import sim.util.Double2D;

/**
 * Approaches ball and aligns toward the receiver.
 * This is for the kicker. 
 * 
 * @author drew
 */
public class ApproachTarget extends Behavior {
    private static final long serialVersionUID = 1L;

    
    public static String getType() { return sim.app.horde.agent.SimAgent.TYPE_SIMAGENT; }
    
    public ApproachTarget() {
        name = "ApproachTarget";
        targets = new Target[1];
        targets[0] = new Me();
        targetNames = new String[1];
        targetNames[0] = "Receiver";
        }
    
    
    @Override
    public void start(Agent agent, Macro parent, Horde horde) {
        super.start(agent, parent, horde);
        
        
        Target target = targets[0];
        if (target instanceof Wrapper)
                target = ((Wrapper)target).getTopParameter(parent);
        
        
        if (target instanceof Real) {
            //System.err.println("Was Goal Target to KickTo behavior");
            // The goal is always ready to be kicked to so it is always ready.
            Real gt = (Real)target;
            Double2D loc = gt.getRealTargetLocation(agent, parent, horde);
            ((DarwinAgent) agent).incrementAck();
            ((DarwinAgent) agent).sendMotion(Motions.getBodyApproachTargetMotion(loc.x, loc.y, 0.1));
            return;
        }
        
        Targetable receiver = targets[0].getTargetable(agent, parent, horde);
        if (receiver instanceof DarwinAgent) {
            DarwinParser dp = ((DarwinAgent) receiver).getCurrentData();
            ((DarwinAgent) agent).incrementAck();
            ((DarwinAgent) agent).sendMotion(Motions.getBodyApproachTargetMotion(dp.getPoseX(), dp.getPoseY(), dp.getPoseAngle()));
        } else if (receiver instanceof Real) {
            Real rm = (Real) receiver;
            Double2D loc = rm.getRealTargetLocation(agent, parent, horde);
            //double x = (loc.x - 30) * .1;
            //double y = (loc.y - 20) * .1;
            // only need x and y. Theta is ignored so set to .1 arbitralily.
            ((DarwinAgent) agent).incrementAck();
            ((DarwinAgent) agent).sendMotion(Motions.getBodyApproachTargetMotion(loc.x, loc.y, 0.1));
        }
        else {
            System.err.println("Trying to do ApproachTarget behavior and the target was not real?");
        }
        
        }
    }

