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
import sim.app.horde.scenarios.robot.darwin.agent.DarwinAgent;
import sim.app.horde.scenarios.robot.darwin.comm.DarwinParser;
import sim.app.horde.scenarios.robot.darwin.targets.GoalTarget;
import sim.app.horde.targets.Me;
import sim.app.horde.targets.Target;
import sim.app.horde.targets.Wrapper;

/**
 *
 * @author drew
 */
public class YelledReady extends CategoricalFeature{

    private static final long serialVersionUID = 1;

    public static String getType() { return sim.app.horde.agent.SimAgent.TYPE_SIMAGENT; }
    
    public YelledReady() {
        super("YelledReady", new String[] {"NotReady", "Ready"});
        targets = new Target[1];
        targets[0] = new Me();  //  default
        targetNames = new String[]{ "X" };// the other guy who I want to know about
        }

    
    
    @Override
    public double getValue(Agent agent, Macro parent, Horde horde) {
        
        Target target = targets[0];
        if (target instanceof Wrapper)
                target = ((Wrapper)target).getTopParameter(parent);
        
        if (target instanceof GoalTarget) {
            //System.err.println("Was Goal Target");
            return 1.0;// The goal is always ready to be kicked to so it is always ready.
        }
        
        Targetable targetable = target.getTargetable(agent, parent, horde);
        if (targetable instanceof DarwinAgent) 
        {
            //System.err.println("Was DarwinAgent" );
            DarwinParser dp = ((DarwinAgent) targetable).getCurrentData();
           // System.err.println("In YelledReady and the agent i am targeting is: " + dp.getPlayerID() + " his ready state is " + dp.getReady());
            
            return dp.getReady();
        }
        //System.err.println("target " + target + " targetable " + targetable);
        return 0.0;
        }
    }