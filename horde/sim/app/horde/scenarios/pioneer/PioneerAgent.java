package sim.app.horde.scenarios.pioneer;

import java.net.Socket;
import java.util.Scanner;

import sim.app.horde.*;
import sim.app.horde.agent.Agent;
import sim.app.horde.scenarios.pioneer.features.BlobData;

public class PioneerAgent extends Agent
    {
    private static final long serialVersionUID = 1L;

    public final static int robotPort = 6000;

    public final static int MOVE_SPEED = 7;
    public final static int TURN_SPEED = 7;
        
    public final static int TRAINING_TURN_SPEED = 7;
    public final static int TRAINING_MOVE_SPEED = 7; 

    public final static int FORWARD = 0;
    public final static int BACKWARD = 1;
    public final static int LEFT = 2;
    public final static int RIGHT = 3;

    public final static int NUM_SONARS = 16;

    // if ANY object is within this distance at any angle, stop!
    public final static double SAFETY_DISTANCE = 200;

    public double closestObstacleDistance, closestObstacleAngle;
    public double closestFrontDistance, closestFrontAngle; 
    public double closestRearDistance; 

    public int blobSize;

    // angles of sonars on pioneer in degrees
    double sonarAngles[] = new double[NUM_SONARS]; // = { -90, -50, -30, -10,
    // 10, 30, 50, 90 };

    String ipAddress = "";

    public gmu.robot.pioneer.PioneerRobot robot = new gmu.robot.pioneer.PioneerRobot();

    double sonars[] = new double[NUM_SONARS];

    int cameraPort = 8050;
    private Scanner scanner;
    private Thread cameraThread;

    public BlobData blobData = new BlobData();
    BlobData localBlobData = new BlobData();
    Object cameraLock = new int[0];
    Object sonarLock = new int[0];

        
    public PioneerAgent(Horde horde, String ipAddress) {
        super(horde);

        //this.behavior = null;
        this.ipAddress = ipAddress;
        started = false;

        robot.setVerbose(false);
        robot.connect(ipAddress, robotPort);
        robot.enable(true);
        robot.sonar(true);

        for (int i = 0; i < NUM_SONARS; i++)
            sonars[i] = 0;

        sonarAngles[0] = Math.PI;
        sonarAngles[7] = 0;
        sonarAngles[8] = 0;
        sonarAngles[15] = Math.PI;
        for (int i = 1; i <= 6; i++)
            sonarAngles[i] = Math.PI - Math.PI / 9 * (i + 1);

        for (int i = 9; i <= 14; i++)
            sonarAngles[i] = 2 * Math.PI - (Math.PI / 9 * (i - 8));

        try
            {
            final Socket cameraSocket = new Socket(ipAddress, cameraPort);

            cameraThread = new Thread(new Runnable()
                {
                public void run()
                    {
                    try
                        {
                        scanner = new Scanner(cameraSocket.getInputStream());
                        while (true)
                            {
                            if (Thread.interrupted())
                                break;
                            BlobData bd = new BlobData();
                            bd.readData(scanner);
                            synchronized (cameraLock)
                                {
                                blobData = bd;
                                }
                            }
                        } catch (Exception e)
                        {
                        }

                    try
                        {
                        System.err.println("[" + getClass().getName() + "] Shutting down camera socket");
                        cameraSocket.close();
                        System.err.println("[" + getClass().getName() + "] Camera socket closed");
                        } catch (Exception e)
                        {
                        }
                    }
                });
            System.err.println("[" + getClass().getName() + "] Starting camera thread");
            cameraThread.start();
            System.err.println("[" + getClass().getName() + "] Camera thread started");
            } catch (Exception e)
            {
            System.err.println("Failed to connect to camera: ");
            e.printStackTrace();
            System.exit(-1);
            }

        }

    public void stop()
        {
        System.err.println("[" + getClass().getName() + "] Disconnecting from Robot " + ipAddress);
        behavior.stop(this, null, horde);
        stopRobot();
        for (int i = 0; i < 10; i++)
            if (robot.close())
                break;
        robot.disconnect();
        robot = null;
        System.err.println("[" + getClass().getName() + "] Stopped robot " + ipAddress);

        try
            {
            System.err.println("[" + getClass().getName() + "] Shutting down camera thread " + ipAddress);
            cameraThread.interrupt();
            cameraThread.join();
            System.err.println("[" + getClass().getName() + "] Camera stopped " + ipAddress);
            } catch (Exception e)
            {
            }
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
            robot.vel2((byte)-speed, speed);
        else
            robot.vel2(speed, (byte) -speed);
        }

    public void pulse(PioneerHorde horde)
        {
        // get the latest sonar data and determine closest obstacle
        synchronized (sonarLock) { 
            sonars = robot.getSonars(sonars);
            }

        closestObstacleDistance = 100000;
        closestObstacleAngle = 0;
                
        closestFrontDistance = 100000; 
        closestFrontAngle = 0; 
                
        closestRearDistance = 100000; 

        for (int i = 0; i < NUM_SONARS; i++)
            {
            if (sonars[i] < closestObstacleDistance)
                {
                closestObstacleDistance = sonars[i];
                closestObstacleAngle = sonarAngles[i];
                }
                        
            if (i < 8 && sonars[i] < closestFrontDistance) { 
                closestFrontDistance = sonars[i]; 
                closestFrontAngle = sonarAngles[i];
                }
                        
            if (i >= 8 && sonars[i] < closestRearDistance) 
                closestRearDistance = sonars[i]; 
                        
            }

        //if (closestObstacleDistance < SAFETY_DISTANCE)
        //{
        //      stopRobot();
        //      return;
        //}

        // get latest camera frame
        synchronized (cameraLock)
            {
            localBlobData = blobData;
            }

        /*      if (behavior != null && behavior instanceof Macro) { 
                Macro m = (Macro)behavior; 
                String behaviorName = "UNKNOWN"; 
                if (m.currentBehavior >= 0) { 
                Behavior b = m.behaviors[m.currentBehavior];
                if (b instanceof TrainableMacro) { 
                TrainableMacro tm = (TrainableMacro)b ; 
                if (tm.currentBehavior >= 0) { 
                if (tm.behaviors[tm.currentBehavior] instanceof TrainableMacro) {
                TrainableMacro t2 = (TrainableMacro)tm.behaviors[tm.currentBehavior]; 
                if (t2.currentBehavior >= 0) 
                behaviorName = t2.behaviors[t2.currentBehavior].getName(); 
                }
                else 
                behaviorName = tm.behaviors[tm.currentBehavior].getName(); 
                }
                } 
                else 
                behaviorName = b.getName();
                }
                System.out.println(ipAddress + " => " + behaviorName); 
                } */
                
        super.go();
        }
        
    public String toString() 
        {
        return "PioneerAgent: " + ipAddress; 
        }
    }
