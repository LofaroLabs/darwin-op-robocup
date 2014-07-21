package sim.app.horde.scenarios.robocup2011;

import sim.app.horde.Agent;
import sim.app.horde.Horde;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.features.*;

public class GameState extends CategoricalFeature {
    private static final long serialVersionUID = 1L;

    public GameState() { super("Game-State", new String[] {"Initial", "Ready", "Set", "Playing", "Finished"}); } 
        
    public double getValue(Agent agent, Macro parent, Horde horde) { 
        return ((HumanoidAgent)agent).gameState; 
        }
    }

