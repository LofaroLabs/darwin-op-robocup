package sim.app.horde.scenarios.heteroboxpushing.behaviors.create;

import sim.app.horde.behaviors.*;
import sim.app.horde.*;
import sim.app.horde.scenarios.heteroboxpushing.create.CreateAgent;

public class CreateStop extends Behavior 
    {
    private static final long serialVersionUID = 1L;

    public CreateStop() {
        name = "CreateStop";
        }

    public void go(Agent agent, Macro parent, Horde horde)
        {
        super.go(agent, parent, horde);
        ((CreateAgent)agent).stopRobot(); 
        }
    }
