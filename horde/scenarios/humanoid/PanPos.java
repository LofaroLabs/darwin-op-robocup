package sim.app.horde.scenarios.humanoid;

import sim.app.horde.Agent;
import sim.app.horde.Horde;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.features.Feature;

public class PanPos extends Feature {
    private static final long serialVersionUID = 1L;

    public PanPos() { super("Pan position"); } 
        
    public double getValue(Agent agent, Macro parent, Horde horde) { 
        return ((HumanoidAgent)agent).panPos; 
        }
    }
