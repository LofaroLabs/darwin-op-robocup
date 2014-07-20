package sim.app.horde.scenarios.foraging;

import sim.app.horde.*;
import sim.app.horde.behaviors.*;
import sim.app.horde.targets.*;

public class Unload extends Behavior
    {
    private static final long serialVersionUID = 1;
        
    public Unload()
        {
        targets = new Target[1];
        targets[0] = new Me();
        targetNames = new String[] { "Deposit Point" };
        name = "Unload";
        setKeyStroke('u');
        }
        
    public void go(Agent agent, Macro parent, Horde horde)
        {
        super.go(agent, parent, horde);
        Targetable t = (Targetable)(targets[0].getTargetable(agent, parent, horde));
        if (t!=null)
            ((Forager)agent).unload(t, (ForagingHorde) horde);
        else System.err.println("Tried to unload on invalid target");
        }
    }
