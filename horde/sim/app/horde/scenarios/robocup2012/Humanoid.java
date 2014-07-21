package sim.app.horde.scenarios.robocup2012;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import sim.app.horde.scenarios.robocup2012.features.HumanoidData;

public class Humanoid
    {
    Thread humanoidThread;
    OutputStream out;

    Object lock = new Object[0];
    HumanoidData tmpHumanoidData = new HumanoidData();

    byte[] WAIT_FOR_BALL = "W000".getBytes(); 
    byte[] WAIT_FOR_GOAL = "W001".getBytes();  
        
    public Humanoid(String ipAddress, int port) {
        try
            {
            final Socket robotSocket = new Socket(ipAddress, port);

            out = robotSocket.getOutputStream();

            humanoidThread = new Thread(new Runnable()
                {
                public void run()
                    {
                    try
                        {
                        Scanner scanner = new Scanner(robotSocket.getInputStream());

                        while (true)
                            {
                            if (Thread.interrupted())
                                {
                                System.err.println("INTERUPTED ");

                                break;
                                }

                            String line = scanner.nextLine();

                            HumanoidData hd = new HumanoidData();
                            hd.readData(line);

                            synchronized (lock)
                                {
                                tmpHumanoidData = hd; // (HumanoidData) hd.clone();
                                lock.notifyAll(); 
                                }
                            }
                        } catch (Exception e)
                        {
                        //System.err.println("Error reading data from robot!"); 
                        //e.printStackTrace();
                        }

                    try
                        {
                        System.err.println("[" + getClass().getName() + "] Shutting down robot socket");
                        robotSocket.close();
                        System.err.println("[" + getClass().getName() + "] Robot socket closed");
                        } catch (Exception e)
                        {
                        }
                    }
                });
            System.err.println("[" + getClass().getName() + "] Starting robot thread");
            humanoidThread.start();
            System.err.println("[" + getClass().getName() + "] Robot thread started");
            } catch (Exception e)
            {
            System.err.println("Failed to connect to robot: ");
            e.printStackTrace();
            System.exit(-1);
            }
        }
        
        
    public HumanoidData waitForData() 
        {
        synchronized(lock) 
            {
            try
                {
                lock.wait();
                } catch (InterruptedException e)
                {
                e.printStackTrace();
                } 
            return getCurrentData(); 
            }
        }

    public void stop()
        {
        try
            {
            System.err.println("[" + getClass().getName() + "] Shutting down humanoid output ");
            pushButton(0); // stop any buttons current running 
            out.close();
            System.err.println("[" + getClass().getName() + "] Output closed ");
            System.err.println("[" + getClass().getName() + "] Shutting down humanoid thread ");
            humanoidThread.interrupt();
            humanoidThread.join();
            System.err.println("[" + getClass().getName() + "] WiiMote stopped ");
            } catch (Exception e)
            {
            }

        }

    public void pushButton(int button)
        {
        try
            {
            String output = "B" + button;
            if (button < 10)
                output = "B00" + button;
            else if (button < 100) 
                output = "B0" + button ;
            byte[] b = output.getBytes();
            out.write(b, 0, b.length);
            out.flush();
            } catch (Exception e)
            {
            System.err.println("ERROR in pushButton while writting to robot");
            e.printStackTrace();
            }
        }

    public void doMotion(int motion)
        {
        try
            {
            // clear any running button motions 
            pushButton(0);

            // now send the command to play the motion 
            String output = "M0" + motion;
            if (motion < 10)
                output = "M00" + motion;
            byte[] b = output.getBytes();
            out.write(b, 0, b.length);
            out.flush();

            // add in a pause so motion completes before HiTAB continues 
            long currentTime = System.currentTimeMillis();
            while (true)
                {
                if (System.currentTimeMillis() - currentTime > 30000)
                    { // at most a 30 sec pause 
                    //System.err.println("TIMEOUT"); 
                    break;
                    }
                HumanoidData d = getCurrentData();
                if (d.actionDone == 1)
                    break;
                }
            } catch (Exception e)
            {
            System.err.println("ERROR in doMotion while writting to robot");
            e.printStackTrace();
            }
        }

    public void waitForCamera()
        {
        try
            {       
            //System.out.println("WaitForCamera"); 
            synchronized(lock)
                {
                out.write(WAIT_FOR_BALL, 0, WAIT_FOR_BALL.length); 
                out.flush(); 
                        
                HumanoidData data = waitForData();
                while(data.waitForBall != 1)
                    {
                    data = waitForData();
                    System.out.println("B: " + data.waitForBall + " " + data.ballXDistance + " " + data.ballZDistance);
                    }
                }
                        
                
                        
/*
                        
  HumanoidData data = waitForData(); 
  if (data.waitForBall != 1)
  {
  while (true)
  {
  HumanoidData d = waitForData(); 
  if (d.waitForBall == 1) {
  break;
  }
  System.out.println(d.waitForBall); 
  }
  }
*/
                        
            // add in a pause so motion completes before HiTAB continues 
            //long currentTime = System.currentTimeMillis();
            /*while (true)
              {
              if (System.currentTimeMillis() - currentTime > 10000)
              { // at most a 10 sec pause 
              //System.err.println("TIMEOUT"); 
              break;
              }
              HumanoidData d = getCurrentData();
              if (d.waitForBall == 1) {
              //System.out.println("ALL DONE"); 
              break;
              }
                                        
              }
            */ 
                        
                        
            //System.out.println("Done with WaitForCamera"); 
                        

            } catch (Exception e)
            {
            System.err.println("ERROR in waitForBall while writting to robot");
            e.printStackTrace();
            }
        }

    public void waitForGoal()
        {
        try
            {
            synchronized(lock)
                {
                out.write(WAIT_FOR_GOAL, 0, WAIT_FOR_GOAL.length); 
                out.flush(); 
                
                HumanoidData data = waitForData();
                while(data.waitForGoal != 1)
                    {
                    data = waitForData();
                    System.out.println("G: " + data.waitForGoal + " " + data.goalBearing);
                    }
                }

            } catch (Exception e)
            {
            System.err.println("ERROR in waitForGoal while writting to robot");
            e.printStackTrace();
            }
        }

    public HumanoidData getCurrentData()
        {
        HumanoidData newData;
        synchronized (lock)
            {
            newData = (HumanoidData) tmpHumanoidData.clone();
            }

        //System.out.println(newData.ballBearing); 
                
        return newData;
        }
    }
