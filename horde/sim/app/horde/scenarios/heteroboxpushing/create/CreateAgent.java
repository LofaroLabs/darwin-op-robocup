package sim.app.horde.scenarios.heteroboxpushing.create;

import sim.app.horde.scenarios.heteroboxpushing.*; 

public class CreateAgent extends Robot
    {
    private static final long       serialVersionUID        = 1L;
    private Create                          robot;          
    private byte[]                          sensorData;

    public CreateAgent(BoxHitab hitab, String ipAddress, int port)
        {
        this(hitab, ipAddress, port, false); 
        }
        
    public CreateAgent(BoxHitab horde, String ipAddress, int port, boolean autoUpdate)
        {
        super(horde, ipAddress, "Create");
        started = false;
        this.ipAddress = ipAddress;
        this.port = port; 
                
        String connectStr = ipAddress + ":" + port; 
                
        robot = new Create(); 
        //setVerbose(false);
        if (!robot.connect(connectStr))
            System.err.println("FAIL");

        robot.startup();
        robot.control();
        robot.pause(30);
        robot.sensors();
                
        //if (autoUpdate)
        robot.startAutoUpdate(100);
        }

    public void pulse(BoxHitab horde)
        {
        sensorData = robot.sensors();
                
        System.out.println(robot.bumpLeft(sensorData) + " " + robot.bumpRight(sensorData)); 
                
        super.pulse(horde);
        }

    public void stop()
        {
        System.err.println("[" + getClass().getName() + "] Stopping " + ipAddress);

        super.stop(); 
        robot.disconnect(); 
                
        System.err.println("[" + getClass().getName() + "] Robot is stopped " + ipAddress);

        }

    public void stopRobot()
        {
        robot.stop(); 
        }

    public void move(int direction)
        {
        if (direction == FORWARD) 
            robot.goForward(); 
        else 
            robot.goBackward(); 
        }

    public void turn(int direction)
        {
        if (direction == LEFT)
            robot.spinLeft(); 
        else 
            robot.spinRight(); 
        }

    public double angleInRadians()
        {
        return robot.angleInRadians(sensorData);
        }

    public boolean bumpLeft()
        {
        return robot.bumpLeft(sensorData);
        }

    public boolean bumpRight()
        {
        return robot.bumpRight(sensorData);
        }

    public boolean wall()
        {
        return robot.wall(sensorData);
        }
    }
