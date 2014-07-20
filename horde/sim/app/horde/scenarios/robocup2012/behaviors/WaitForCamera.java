package sim.app.horde.scenarios.robocup2012.behaviors;

import sim.app.horde.*;
import sim.app.horde.behaviors.*;
import sim.app.horde.scenarios.robocup2012.HumanoidAgent;

public class WaitForCamera extends Behavior
    {
    private static final long serialVersionUID = 1L;

    public boolean getShouldAddDefaultExample() { return false; } 

        
    public WaitForCamera() 
        {
        name = "WaitForCamera";
        }

    public void start(Agent agent, Macro parent, Horde horde)
        {
        super.start(agent, parent, horde);
                
        System.out.println("Wait For Camera");
                
        ((HumanoidAgent)agent).humanoid.waitForCamera(); 
        }
        
        
    }