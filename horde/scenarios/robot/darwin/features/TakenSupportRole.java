/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.horde.scenarios.robot.darwin.features;

import sim.app.horde.Horde;
import sim.app.horde.agent.Agent;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.features.CategoricalFeature;
import sim.app.horde.features.Feature;
import sim.app.horde.scenarios.robot.darwin.agent.DarwinAgent;
import sim.app.horde.scenarios.robot.darwin.comm.DarwinParser;

/**
 * Whether I have taken the role
 * @author drew
 */
public class TakenSupportRole extends CategoricalFeature{
     private static final long serialVersionUID = 1;

    public static String getType() { return sim.app.horde.agent.SimAgent.TYPE_SIMAGENT; }
    
    public TakenSupportRole() {
        super("Taken Support Role", new String[] {"NotTakenRole", "TakenRole"});
        
        }

    
    
    @Override
    public double getValue(Agent agent, Macro parent, Horde horde) {
        
        DarwinParser dp = ((DarwinAgent) agent).getCurrentData();
        return dp.getSupportDeclared() == dp.getPlayerID() ? 1 : 0;
        }
}