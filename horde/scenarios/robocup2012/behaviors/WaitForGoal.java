package sim.app.horde.scenarios.robocup2012.behaviors;

import sim.app.horde.*;
import sim.app.horde.behaviors.*;
import sim.app.horde.scenarios.robocup2012.HumanoidAgent;

public class WaitForGoal extends Behavior
    {
    private static final long serialVersionUID = 1L;

    public boolean getShouldAddDefaultExample() { return false; } 

    public WaitForGoal() 
        {
        name = "WaitForGoal";
        }

    public void go(Agent agent, Macro parent, Horde horde)
        {
        super.start(agent, parent, horde);
                
        System.out.println("Wait For Goal");
                
        ((HumanoidAgent)agent).humanoid.waitForGoal(); 
        }
        
        
    }
