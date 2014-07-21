package sim.app.horde.scenarios.heteroboxpushing.pioneer;

import sim.app.horde.*;
import sim.app.horde.scenarios.heteroboxpushing.*;
import gmu.robot.pioneer.*; 

public class PioneerAgent extends Robot
    {
    private static final long serialVersionUID = 1L;

    public final static int robotPort = 6000;
    public final static int laserPort = 7000;
    public final static int cameraPort = 8000;

    public final static int MOVE_SPEED = 7;
    public final static int TURN_SPEED = 7;

    public final static int TRAINING_TURN_SPEED = 7;
    public final static int TRAINING_MOVE_SPEED = 7;

    // if ANY object is within this distance at any angle, stop!
    public final static double SAFETY_DISTANCE = 200;

    public PioneerRobot robot = new PioneerRobot();

    public Camera camera = null;
    public Sonar sonar = null;
    public Laser laser = null; 
    Object sonarLock = new int[0];
    double sonars[] = new double[Sonar.NUM_SONARS]; 

    public PioneerAgent(Horde horde, String ipAddress) {
        super(horde, ipAddress, "Pioneer");

        System.out.println("C-TOR"); 
        System.out.println(ipAddress + " " + robotPort); 

                
        robot.setVerbose(false);
        robot.connect(ipAddress, robotPort);
        robot.enable(true);
        robot.sonar(true);

        System.out.println("ROBOT"); 
                
        sonar = new Sonar();

        System.out.println("SONAR"); 
                
        laser = new Laser(ipAddress, laserPort); 
        laser.start(); 

        System.out.println("LASER"); 
                
        camera = new Camera(ipAddress, cameraPort);
        camera.start();
                
        System.out.println("CAMERA"); 
        }

    public void stop()
        {
        System.err.println("[" + getClass().getName() + "] Disconnecting from Robot " + ipAddress);

        super.stop();

        for (int i = 0; i < 10; i++)
            if (robot.close())
                break;
        robot.disconnect();
        robot = null;

        if (laser != null)
            laser.stop(); 
                
        if (camera !=null)
            camera.stop();

        System.err.println("[" + getClass().getName() + "] Stopped robot " + ipAddress);

        }

    public void stopRobot()
        {
        robot.vel2((byte) 0, (byte) 0);
        }

    public void move(int direction)
        {
        byte speed = (byte) MOVE_SPEED;
        if (isTraining())
            speed = (byte) TRAINING_MOVE_SPEED;

        if (direction == FORWARD)
            robot.vel2(speed, speed);
        else
            robot.vel2((byte) -speed, (byte) -speed);
        }

    public void turn(int direction)
        {
        byte speed = (byte) TURN_SPEED;
        if (isTraining())
            speed = (byte) TRAINING_TURN_SPEED;

        if (direction == LEFT)
            robot.vel2((byte) -speed, speed);
        else
            robot.vel2(speed, (byte) -speed);
        }

    public void pulse(BoxHitab horde)
        {
        // get the latest sonar data and determine closest obstacle
        synchronized (sonarLock)
            {
            if (robot != null)
                sonars = robot.getSonars(sonars);
            }
        if (sonar != null) 
            sonar.update(sonars);

        super.pulse(horde);
        }

    }
