package sim.app.horde.scenarios.humanoid;

import sim.app.horde.Agent;
import sim.app.horde.Horde;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.features.Feature;

public class BallXMin extends Feature {
    private static final long serialVersionUID = 1L;

    public BallXMin() { super("Ball X-min"); } 
        
    public double getValue(Agent agent, Macro parent, Horde horde) { 
        return ((HumanoidAgent)agent).ballXmin; 
        }
    }

