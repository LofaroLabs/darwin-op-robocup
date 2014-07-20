package sim.app.horde.scenarios.heteroboxpushing.behaviors.pioneer;

import sim.app.horde.behaviors.*;
import sim.app.horde.*;
import sim.app.horde.scenarios.heteroboxpushing.pioneer.PioneerAgent;

public class PioneerStop extends Behavior 
    {
    private static final long serialVersionUID = 1L;

    public PioneerStop() {
        name = "PioneerStop";
        }

    public void go(Agent agent, Macro parent, Horde horde)
        {
        super.go(agent, parent, horde);
        PioneerAgent pioneer = (PioneerAgent)agent; 
        pioneer.stopRobot();
        }
    }
