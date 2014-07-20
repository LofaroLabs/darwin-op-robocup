package sim.app.horde.scenarios.humanoid;

import sim.app.horde.Agent;
import sim.app.horde.Horde;
import sim.app.horde.features.Feature;

public class Camera extends Feature {
    private static final long serialVersionUID = 1L;

    public Camera() { super("Camera number"); } 
        
    public double getValue(Agent agent, Horde horde) { 
        return ((HumanoidAgent)agent).camera; 
        }
    }
