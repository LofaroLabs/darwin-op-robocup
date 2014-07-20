package sim.app.horde.scenarios.robocup2012.wiimote;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;


public class WiiMote
    {
        
    Object dataLock = new Object[0]; 
    WiiMoteData data; 
    Thread wiiMoteThread; 
        
    public WiiMote(String hostname, int port) 
        {

        // fire up thread on socket
        try
            {
            final Socket s = new Socket(hostname, port);
            wiiMoteThread = new Thread(new Runnable()
                {
                public void run()
                    {
                    try
                        {
                        Scanner scanner = new Scanner(s.getInputStream());
                        while (true)
                            {
                            if (Thread.interrupted())
                                break;
                                
                            WiiMoteData wd = new WiiMoteData();
                            wd.readData(scanner);

                            synchronized (dataLock)
                                {
                                data = wd;
                                }
                            }
                        } catch (Exception e)
                        {
                        }

                    try
                        {
                        System.err.println("[" + getClass().getName() + "] Shutting down wiiMote socket");
                        s.close();
                        System.err.println("[" + getClass().getName() + "] WiiMote socket closed ");
                        } catch (Exception e)
                        {
                        }
                    }
                });
            wiiMoteThread.setDaemon(true);
            wiiMoteThread.start();

            } catch (IOException e1)
            {
            e1.printStackTrace();
            }

        }
        
    public void stop()
        {
        try
            {
            System.err.println("[" + getClass().getName() + "] Shutting down wiiMote thread ");
            wiiMoteThread.interrupt();
            wiiMoteThread.join();
            System.err.println("[" + getClass().getName() + "] WiiMote stopped ");
            } catch (Exception e)
            {
            }
        }
        
    public WiiMoteData getCurrentData() 
        {
        WiiMoteData newData; 
        synchronized(dataLock) 
            {
            newData = data; 
            }
        return newData; 
        }
        
        
        
    }
