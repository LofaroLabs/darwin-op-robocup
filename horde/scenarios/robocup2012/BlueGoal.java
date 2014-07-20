package sim.app.horde.scenarios.robocup2012;

import java.awt.Color;
import sim.app.horde.scenarios.robocup2012.features.*;
import sim.util.Double2D;


public class BlueGoal extends RobocupObject
    {
    private static final long serialVersionUID = 1L;

    public BlueGoal()
        {
        super(Color.blue, "Blue Goal");
        }
        
    public void update(HumanoidHorde hHorde, HumanoidData data)
        {
        double x=-1, y=-1;
        width=-1;
        height=-1; 
                        
        if (data.blueGoalLeftXMin >= 0 && data.blueGoalRightXMin >=0) // can see both posts  
            {
            x = data.blueGoalLeftXMin; y = data.blueGoalLeftYMin; 
            width = data.blueGoalRightXMax - data.blueGoalLeftXMin; 
            height = data.blueGoalRightYMax - data.blueGoalLeftYMin; 
            }
        else if (data.blueGoalRightXMin < 0) // can see only left post  
            {
            x = data.blueGoalLeftXMin; y = data.blueGoalLeftYMin; 
            width = data.blueGoalLeftXMax - data.blueGoalLeftXMin; 
            height = data.blueGoalLeftYMax - data.blueGoalLeftYMin; 
            }
        else if (data.blueGoalLeftXMin < 0) // can see only right post 
            {
            x = data.blueGoalRightXMin; y = data.blueGoalRightYMin; 
            width = data.blueGoalRightXMax - data.blueGoalRightXMin; 
            height = data.blueGoalRightYMax - data.blueGoalRightYMin; 
            }
            
                        
        if (width < 5) width = 10; 
        if (height < 5) height = 10; 
                        
        //System.out.println(x + " " + y + " : " + width + " " + height); 
        //System.out.print(data.blueGoalXDistance + " " + data.blueGoalZDistance + " : "); 
        //System.out.println(data.ballXDistance + " " + data.ballZDistance); 
        hHorde.blueField.setObjectLocation(this, new Double2D(x,y)); 
        hHorde.blueGoalLocation.setObjectLocation(this, new Double2D(-data.blueGoalXDistance, data.blueGoalZDistance));
        }
                

    }