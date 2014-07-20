package sim.app.horde.scenarios.robocup2011;

import sim.app.horde.Agent;
import sim.app.horde.Horde;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.features.Feature;

public class GARPDistance extends Feature {
    private static final long serialVersionUID = 1L;

    public GARPDistance() { super("GARP-Distance"); } 
        
    public double getValue(Agent agent, Macro parent, Horde horde) { 
        return ((HumanoidAgent)agent).garpDistance; 
        }
    }

