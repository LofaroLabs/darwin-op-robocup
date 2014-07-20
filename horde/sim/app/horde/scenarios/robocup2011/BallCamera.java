package sim.app.horde.scenarios.robocup2011;

import sim.app.horde.Agent;
import sim.app.horde.Horde;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.features.*;

public class BallCamera extends CategoricalFeature {
    private static final long serialVersionUID = 1L;

    public BallCamera() { super("Ball-Camera", new String[] {"Left", "Right", "None"}); } 
        
    public double getValue(Agent agent, Macro parent, Horde horde) { 
        return ((HumanoidAgent)agent).ballCamera; 
        }
    }

