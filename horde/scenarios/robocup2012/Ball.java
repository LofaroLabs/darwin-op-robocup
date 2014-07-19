package sim.app.horde.scenarios.robocup2012;

import java.awt.Color;
import sim.app.horde.scenarios.robocup2012.features.*;
import sim.util.Double2D;

public class Ball extends RobocupObject
    {
    private static final long serialVersionUID = 1L;

    public Ball()
        {
        super(Color.orange, "Ball");
        }
        
    public void update(HumanoidHorde hHorde, HumanoidData data)
        {
        width = 10; //Math.max(5, xMax - xMin); 
        height =  10; //Math.max(5, yMax -  yMin); 
        //hHorde.ballField.setObjectLocation(this, new Double2D(data.ballXMin, data.ballYMin));
        hHorde.ballLocation.setObjectLocation(this, new Double2D(-data.ballXDistance, data.ballZDistance));
        }
                

    }