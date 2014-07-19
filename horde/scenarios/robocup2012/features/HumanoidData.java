package sim.app.horde.scenarios.robocup2012.features;

import java.io.IOException;
import java.util.StringTokenizer;

public class HumanoidData implements Cloneable
    {

    public int ballVisible; 
    public double ballXDistance;
    public double ballZDistance;
    public double ballBearing; 
    public int blueGoalVisible; 
    public double blueGoalXDistance;
    public double blueGoalZDistance;
    public int yellowGoalVisible; 
    public double yellowGoalXDistance;
    public double yellowGoalZDistance;
        
    public double goalBearing; 
    public int actionDone;
    public int waitForBall; 
    public int waitForGoal; 
        
    public int blueGoalLeftXMin, blueGoalLeftXMax, blueGoalLeftYMin, blueGoalLeftYMax;  
    public int blueGoalRightXMin, blueGoalRightXMax, blueGoalRightYMin, blueGoalRightYMax;  

    public int attackGoalVisible; 
        
    public HumanoidData() 
        {

        }

    public String toString()
        {
        String str = "";

        if (ballVisible == 1) 
            str += "Ball(" + ballXDistance + ", " + ballZDistance + ", " + ballBearing + ") "; 
        if (blueGoalVisible == 1)
            str += "BlueGoal(" + blueGoalXDistance + ", " + blueGoalZDistance + ") "; 
        if (yellowGoalVisible == 1)
            str += "YellowGoal("+ yellowGoalXDistance + ", " + yellowGoalZDistance + ")";

        return str;
        }

    public Object clone()
        {
        HumanoidData hd = new HumanoidData();

        hd.ballXDistance = ballXDistance;
        hd.ballZDistance = ballZDistance;
        hd.ballBearing = ballBearing; 
        hd.goalBearing = goalBearing; 
        hd.blueGoalXDistance = blueGoalXDistance;
        hd.blueGoalZDistance = blueGoalZDistance;
        hd.yellowGoalXDistance = yellowGoalXDistance;
        hd.yellowGoalZDistance = yellowGoalZDistance;

                
        hd.ballVisible = ballVisible; 
        hd.blueGoalVisible = blueGoalVisible; 
        hd.yellowGoalVisible = yellowGoalVisible; 

        hd.actionDone = actionDone;
        hd.waitForBall = waitForBall; 
        hd.waitForGoal = waitForGoal; 
                
        hd.blueGoalLeftXMin = blueGoalLeftXMin; 
        hd.blueGoalLeftXMax = blueGoalLeftXMax; 
        hd.blueGoalLeftYMin = blueGoalLeftYMin; 
        hd.blueGoalLeftYMax = blueGoalLeftYMax; 

        hd.blueGoalRightXMin = blueGoalRightXMin; 
        hd.blueGoalRightXMax = blueGoalRightXMax; 
        hd.blueGoalRightYMin = blueGoalRightYMin; 
        hd.blueGoalRightYMax = blueGoalRightYMax; 
                
        hd.attackGoalVisible = attackGoalVisible; 
                
        return hd;
        }

    public void readData(String line) throws IOException
        {
        StringTokenizer st = new StringTokenizer(line, ":");

        //System.out.println("RAW: " + line); 

        ballVisible = Integer.valueOf(st.nextToken()); 
        ballXDistance = Double.valueOf(st.nextToken());
        ballZDistance = Double.valueOf(st.nextToken());
        ballBearing = Double.valueOf(st.nextToken()); 
        blueGoalVisible = Integer.valueOf(st.nextToken()); 
        blueGoalXDistance = Double.valueOf(st.nextToken());
        blueGoalZDistance = Double.valueOf(st.nextToken());
        yellowGoalVisible = Integer.valueOf(st.nextToken()); 
        yellowGoalXDistance = Double.valueOf(st.nextToken());
        yellowGoalZDistance = Double.valueOf(st.nextToken());
                
        actionDone = Integer.valueOf(st.nextToken());
        waitForBall  = Integer.valueOf(st.nextToken());
        waitForGoal  = Integer.valueOf(st.nextToken());         
                
        blueGoalLeftXMin  = Integer.valueOf(st.nextToken());    
        blueGoalLeftXMax  = Integer.valueOf(st.nextToken());    
        blueGoalLeftYMin  = Integer.valueOf(st.nextToken());    
        blueGoalLeftYMax  = Integer.valueOf(st.nextToken());    
                
        blueGoalRightXMin  = Integer.valueOf(st.nextToken());   
        blueGoalRightXMax  = Integer.valueOf(st.nextToken());   
        blueGoalRightYMin  = Integer.valueOf(st.nextToken());   
        blueGoalRightYMax  = Integer.valueOf(st.nextToken());
                
        goalBearing = Double.valueOf(st.nextToken());
        attackGoalVisible = Integer.valueOf(st.nextToken()); 
        }
    }
