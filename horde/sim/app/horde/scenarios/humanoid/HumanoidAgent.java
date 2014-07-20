package sim.app.horde.scenarios.humanoid;

import sim.app.horde.behaviors.*;
import sim.app.horde.scenarios.humanoid.console.*;
import edu.gmu.robocup.*;
import edu.gmu.robocup.horde.kondo;

import java.net.*;
import java.io.*;
import java.util.*;

public class HumanoidAgent extends sim.app.horde.Agent
    {
    boolean setup = false;
    public void setupVariables()
        {
        if (!setup)
            {
            FINISHED = indexOfBehavior(sim.app.horde.scenarios.humanoid.Stop.class); 
            ALT_FORWARD = indexOfBehavior(sim.app.horde.behaviors.TrainableMacro.class);  // dunno if this works
            //FORWARD = indexOfBehavior(sim.app.horde.scenarios.humanoid.Forward.class);
            //LEFT_TURN = indexOfBehavior(sim.app.horde.scenarios.humanoid.TurnLeft.class);
            //RIGHT_TURN = indexOfBehavior(sim.app.horde.scenarios.humanoid.TurnRight.class);
            }
        setup = true;
        }
        
        
        
        
    public HordeNetworkReader srvComm;

    private static final long serialVersionUID = 1L;
    public int ballXmin, panPos, tiltPos, ballSize, servoCenter, ballYmin, camera;

    public byte[] requestCameraCommand = "Hs".getBytes();

    public WiiMoteData data = null;
    public Object dataLock = new int[0];

    public static int FINISHED = 1;
    public static int ALT_FORWARD = 0;
    public static int FORWARD = 2;
    public static int LEFT_TURN = 3;
    public static int RIGHT_TURN = 4;
    public static int BACKWARD = 5;

    public void pushButton(int button)
        {
        kondo.kondo_krc3_buttons(((HordeNetworkReader) srvComm).ki, button, (short) 0, (short) 0, (short) 0, (short) 0);
        }
        
    public void playMotion(int motion)
        {
        kondo.kondo_play_motion(((HordeNetworkReader) srvComm).ki, motion, 1000);
        }

    public HumanoidAgent(HumanoidHorde horde, Behavior behavior, HordeNetworkReader comm) {
        super(horde);
        setBehavior(behavior); 
        started = false;
        srvComm = comm;

        ballXmin = -1;
        panPos = -1;
        tiltPos = -1;
        servoCenter = -1;

        srvComm.addFrameListener(new SRVListenerAdapter()
            {
            public void srvNewMessage(final byte[] bytes)
                {
                String s = new String(bytes).trim();
                if (s.startsWith("Feature information:"))
                    {
                    String[] arr = s.split(" ");
                    try
                        {
                        ballXmin = Integer.valueOf(arr[2]);
                        panPos = Integer.valueOf(arr[3]);
                        tiltPos = Integer.valueOf(arr[4]);
                        ballSize = Integer.valueOf(arr[5]);
                        servoCenter = Integer.valueOf(arr[6]); 
                        } catch (NumberFormatException e)
                        {
                        ballXmin = servoCenter = panPos = tiltPos = -1;
                        }

                    /*String str = ""; 
                      if (ballXmin < 0) 
                      str = "NONE"; 
                      else { 
                      if (ballXmin < 210) str = "LEFT"; 
                      else str = "RIGHT";
                      }
                                        
                      System.out.println(str + "\t" + ballXmin + "\t" + ballSize);
                    */
                    }
                }
            }); 

                
        int port = 8050;
        String hostname = "localhost";

        // fire up thread on socket
        try
            {
            final Socket s = new Socket(hostname, port);
            Thread thread = new Thread(new Runnable()
                {
                public void run()
                    {
                    try
                        {
                        Scanner scanner = new Scanner(s.getInputStream());
                        while (true)
                            {
                            WiiMoteData wd = new WiiMoteData();
                            wd.readData(scanner);

                            synchronized (dataLock)
                                {
                                data = wd;
                                }
                            }
                        } catch (Exception e)
                        {
                        System.err.println("Socket closed or error");
                        e.printStackTrace();
                        }

                    try
                        {
                        s.close();
                        } catch (Exception e)
                        {
                        System.err.println("CLOSED SOCKET");
                        }
                    }
                });
            thread.setDaemon(true);
            thread.start();

            } catch (IOException e1)
            {
            e1.printStackTrace();
            }

        }

    boolean firstTime = true ;
        
    PrintWriter outFile; 
    static long startTime=0;
        
    boolean isOpen = false; 
    int cnt=1;
        
    String expName = "";
        
    public void pulse(HumanoidHorde horde)
        {
                
        setupVariables();
                
        // get updated info from the camera
        srvComm.sendCommand(requestCameraCommand);

        // get latest input from the Wiimote
        WiiMoteData wd = getCurrentData();

        // enter or leave training mode based on Wiimote button press
        if (wd.currentlyTraining && !horde.getTrainingMacro().isTraining())
            {
            horde.getTrainingMacro().userChangedTraining(horde, true);
            srvComm.consoleNotify("TRAINING".getBytes());
            } else if (!wd.currentlyTraining && horde.getTrainingMacro().isTraining())
            {
            horde.getTrainingMacro().userChangedTraining(horde, false);
            srvComm.consoleNotify("DONE TRAINING".getBytes());
            }
                
        /*      try
                {
                // run experiments
                if (HordePanel.currentlyRunning)
                {
                if (!isOpen) { 
                int idx = indexOfBehavior(sim.app.horde.behaviors.TrainableMacro.class); 
                expName = horde.getTrainingMacro().behaviors[idx].getName(); 
                System.out.println("Running experiments for " + expName); 
                System.out.println("Opened file " + expName + ".data"); 
                outFile = new PrintWriter(new File(expName + ".data")); 
                outFile.println("Running experiments for " + expName);
                isOpen = true; 
                cnt=1;
                }
                                
                                
                if (firstTime && wd.currentButtonPressed == Motions.LEFT_TURN_BUTTON)
                {
                System.out.print("STARTING " + expName + " => " + cnt + ".....");
                firstTime = false;
                startTime = System.currentTimeMillis();
                int idx = indexOfBehavior(sim.app.horde.behaviors.TrainableMacro.class); 
                horde.getTrainingMacro().userChangedBehavior(horde, idx);
                } 
                else if (!firstTime && wd.currentButtonPressed == Motions.RIGHT_TURN_BUTTON)
                {
                long diff = System.currentTimeMillis() - startTime;
                outFile.println((cnt++) + "\t" + diff);
                firstTime = true;
                int idx  = indexOfBehavior(sim.app.horde.scenarios.humanoid.Stop.class);
                horde.getTrainingMacro().userChangedBehavior(horde, idx);
                System.out.println("FINSHED");
                }
                } 
                else
                {
                if (isOpen) { 
                outFile.close();
                isOpen = false; 
                System.out.println("Closed file " + expName + ".data"); 
                }
                }
                } catch (Exception e)
                {
                e.printStackTrace(); System.exit(-1); 
                }
        */       

        // if training, update the database
        if (wd.currentlyTraining)
            {
            //if (wd.currentButtonPressed == Motions.STOP_BUTTON)
            //{
            //      pushButton(Motions.STOP_BUTTON);
            //}
            if (wd.currentButtonPressed == Motions.ONE_BUTTON) 
                {
                horde.getTrainingMacro().userChangedBehavior(horde, FINISHED);                  
                }
            else if (wd.currentButtonPressed == Motions.TWO_BUTTON) 
                { 
                horde.getTrainingMacro().userChangedBehavior(horde, ALT_FORWARD); 
                }
            /*else 
              {
              int behavior = horde.getTrainingMacro().currentBehavior;
              switch (wd.currentButtonPressed) {
              case Motions.ALT_FORWARD_BUTTON:
              if (behavior == ALT_FORWARD) pushButton(Motions.FORWARD_BUTTON);
              else horde.getTrainingMacro().userChangedBehavior(horde, ALT_FORWARD);
              break;
              case Motions.FORWARD_BUTTON:
              if (behavior == FORWARD) 
              pushButton(Motions.FORWARD_BUTTON); 
              else 
              horde.getTrainingMacro().userChangedBehavior(horde, FORWARD);
              break;
              case Motions.BACKWARD_BUTTON:
              if (behavior == BACKWARD) pushButton(Motions.BACKWARD_BUTTON); 
              else horde.getTrainingMacro().userChangedBehavior(horde, BACKWARD);
              break;
              case Motions.LEFT_TURN_BUTTON:
              if (behavior == LEFT_TURN) pushButton(Motions.LEFT_TURN_BUTTON); 
              else horde.getTrainingMacro().userChangedBehavior(horde, LEFT_TURN);
              break;
              case Motions.RIGHT_TURN_BUTTON:
              if (behavior == RIGHT_TURN) pushButton(Motions.RIGHT_TURN_BUTTON); 
              else horde.getTrainingMacro().userChangedBehavior(horde, RIGHT_TURN);
              break;
              default:
              System.err.println("No such button: " + wd.currentButtonPressed);
              }
              } */
            }
        //else 
        //      System.out.println("Current behavior: " + "\t" + horde.getTrainingMacro().currentBehavior);

        super.go(); 
        }

    public WiiMoteData getCurrentData()
        {
        synchronized (dataLock)
            {
            return data;
            }
        }
    }
