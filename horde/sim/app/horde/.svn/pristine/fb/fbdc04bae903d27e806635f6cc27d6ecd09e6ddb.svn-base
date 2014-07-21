package sim.app.horde.scenarios.heteroboxpushing;

import sim.app.horde.*;

public abstract class Robot extends Agent
    {
    private static final long serialVersionUID = 1L;

    public static final int FORWARD = 0;
    public static final int BACKWARD = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;

    public int port = -1; 
    public String ipAddress = "";
    String name=""; 
        
    public Robot(Horde hitab, String ipAddress, String name)
        {
        super(hitab); 
                
        this.name = name; 
        this.ipAddress = ipAddress;
        started = false;
        }
        
    public void forward()
        {
        move(FORWARD);
        }

    public void backward()
        {
        move(BACKWARD);
        }

    public void turnLeft()
        {
        turn(LEFT);
        }

    public void turnRight()
        {
        turn(RIGHT);
        }

    abstract public void stopRobot(); 
    abstract public void move(int direction); 
    abstract public void turn(int direction);
        
    public void stop()
        {
        getBehavior().stop(this, null, horde);
        stopRobot();
        }
        
    public void pulse(BoxHitab hitab) 
        {
        super.go(); 
        }
        
    public String toString()
        {
        return name + ": " +  ipAddress;
        }
    }
