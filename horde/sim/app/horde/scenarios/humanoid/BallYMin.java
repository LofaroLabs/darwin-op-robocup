package sim.app.horde.scenarios.humanoid;

import sim.app.horde.Agent;
import sim.app.horde.Horde;
import sim.app.horde.features.Feature;

public class BallYMin extends Feature {
    private static final long serialVersionUID = 1L;

    public BallYMin() { super("Ball y-min"); } 
        
    public double getValue(Agent agent, Horde horde) { 
        return ((HumanoidAgent)agent).ballYmin; 
        }
    }
