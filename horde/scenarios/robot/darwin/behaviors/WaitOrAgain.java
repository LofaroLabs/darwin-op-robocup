/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.horde.scenarios.robot.darwin.behaviors;

import sim.app.horde.behaviors.Behavior;

/**
 *
 * @author drew
 */
public class WaitOrAgain  extends Behavior {
    private static final long serialVersionUID = 1L;

    public WaitOrAgain() {
        name = "WaitOrAgain";
        }
    
    public static String getType() { return sim.app.horde.agent.SimAgent.TYPE_SIMAGENT; }

    @Override
    public boolean getShouldAddDefaultExample() {
        return false;
    }
    
    
}
