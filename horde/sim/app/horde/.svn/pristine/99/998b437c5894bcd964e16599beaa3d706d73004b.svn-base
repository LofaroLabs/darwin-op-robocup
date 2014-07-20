/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.horde.scenarios.robot.darwin.targets;

import sim.app.horde.Horde;
import sim.app.horde.agent.Agent;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.scenarios.robot.darwin.agent.Real;
import sim.app.horde.targets.Target;
import sim.util.Double2D;

/**
 *
 * @author drew
 */
public class PositionOne extends Target implements Real{

    private static final long serialVersionUID = 106178179844089481L;
    
    Double2D posOne = new Double2D(-10, 16.5);
    
    @Override
    public String toString() {
        return "Position One : (-1, 1.6) in meters"; 
    }

    @Override
    public Double2D getLocation(Agent agent, Macro parent, Horde horde) {
        return posOne;
    }

    @Override
    public Double2D getRealTargetLocation(Agent agent, Macro parent, Horde horde) {
        return new Double2D(-1.0, 1.6); // just a hack because a field doesn't serialize.
    }
}
