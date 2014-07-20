package sim.app.horde.scenarios.robocup2012;

import java.awt.Color;
import sim.app.horde.scenarios.robocup2012.features.*;
import sim.util.Double2D;

public class YellowGoal extends RobocupObject
    {
    private static final long serialVersionUID = 1L;

    public YellowGoal()
        {
        super(Color.yellow, "Yellow Goal");
        }
        
    public void update(HumanoidHorde hHorde, HumanoidData data)
        {
        double x=-1, y=-1;
        width=-1;
        height=-1; 
        /*
          if (data.yellowGoalLeftXMin >= 0 && data.yellowGoalRightXMin >=0) // can see both posts  
          {
          x = data.yellowGoalLeftXMin; y = data.yellowGoalLeftYMin; 
          width = data.yellowGoalRightXMax - data.yellowGoalLeftXMin; 
          height = data.yellowGoalRightYMax - data.yellowGoalLeftYMin; 
          }
          else if (data.yellowGoalRightXMin < 0) // can see only left post  
          {
          x = data.yellowGoalLeftXMin; y = data.yellowGoalLeftYMin; 
          width = data.yellowGoalLeftXMax - data.yellowGoalLeftXMin; 
          height = data.yellowGoalLeftYMax - data.yellowGoalLeftYMin; 
          }
          else if (data.yellowGoalLeftXMin < 0) // can see only right post 
          {
          x = data.yellowGoalRightXMin; y = data.yellowGoalRightYMin; 
          width = data.yellowGoalRightXMax - data.yellowGoalRightXMin; 
          height = data.yellowGoalRightYMax - data.yellowGoalRightYMin; 
          }
        */
            
        if (width < 5) width = 10; 
        if (height < 5) height = 10; 
                        
        hHorde.yellowField.setObjectLocation(this, new Double2D(x,y)); 
        hHorde.yellowGoalLocation.setObjectLocation(this, new Double2D(-data.yellowGoalXDistance, data.yellowGoalZDistance));
        }
                

    }