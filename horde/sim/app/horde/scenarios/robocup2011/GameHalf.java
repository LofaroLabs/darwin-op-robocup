package sim.app.horde.scenarios.robocup2011;

import sim.app.horde.Agent;
import sim.app.horde.Horde;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.features.*;

public class GameHalf extends CategoricalFeature {
    private static final long serialVersionUID = 1L;

    public GameHalf() { super("Game-Half", new String[] {"First", "Second"}); } 
        
    public double getValue(Agent agent, Macro parent, Horde horde) { 
        return ((HumanoidAgent)agent).gameHalf; 
        }
    }

