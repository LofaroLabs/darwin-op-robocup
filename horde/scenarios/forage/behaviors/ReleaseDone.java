package sim.app.horde.scenarios.forage.behaviors;

import sim.app.horde.Horde;
import sim.app.horde.agent.Agent;
import sim.app.horde.behaviors.Macro;

public class ReleaseDone extends Release
    {
    private static final long serialVersionUID = 1L;
        
    public ReleaseDone() { name = "ReleaseDone"; setKeyStroke('R'); } // good default
    
    public boolean shouldAddDefaultExample() { return false; }

    public void go(Agent agent, Macro parent, Horde horde)
        {
        super.go(agent, parent, horde); 
        parent.fireFlag(Macro.FLAG_DONE, agent, parent, horde);
        
        Macro b = (Macro)agent.getBehavior(); 
        int i = b.indexOfBehaviorNamed("Done");
        b.performTransition(i, agent, horde);
        //b.finished = true; 
        b.setFlag(Macro.FLAG_DONE, true, true); 
        }
    }
