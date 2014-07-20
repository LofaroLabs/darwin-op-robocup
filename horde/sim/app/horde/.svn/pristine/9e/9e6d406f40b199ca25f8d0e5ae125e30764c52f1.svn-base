package sim.app.horde.scenarios.heteroboxpushing.pioneer;

import java.net.Socket;
import java.util.Scanner;

public abstract class PioneerSensor
    {
    String          ipAddress;
    int                     port;
    Scanner         scanner;
    Thread          sensorThread;
    Object          sensorLock      = new int[0];

    public PioneerSensor(String address, int p)
        {
        ipAddress = address;
        port = p;
        }

    // kicks off a thread to continually read data from a socket on ipAddress:port 
    public void start()
        {
        try
            {
            final Socket sensorSocket = new Socket(ipAddress, port);

            sensorThread = new Thread(new Runnable()
                {
                public void run()
                    {
                    try
                        {
                        scanner = new Scanner(sensorSocket.getInputStream());
                        while (true)
                            {
                            if (Thread.interrupted())
                                break;

                            readData();
                            }
                        } catch (Exception e)
                        {
                        }

                    try
                        {
                        System.err.println("[" + getClass().getName() + "] Shutting down camera socket");
                        sensorSocket.close();
                        System.err.println("[" + getClass().getName() + "] Camera socket closed");
                        } catch (Exception e)
                        {
                        }
                    }
                });
            System.err.println("[" + getClass().getName() + "] Starting camera thread");
            sensorThread.start();
            System.err.println("[" + getClass().getName() + "] Camera thread started");
            } catch (Exception e)
            {
            System.err.println("Failed to connect to camera: ");
            e.printStackTrace();
            System.exit(-1);
            }
        }

    // reads the data from the socket 
    abstract void readData() throws Exception;

    // stops the sensorThread 
    public void stop()
        {
        try
            {
            System.err.println("[" + getClass().getName() + "] Shutting down camera thread " + ipAddress);
            sensorThread.interrupt();
            sensorThread.join();
            System.err.println("[" + getClass().getName() + "] Camera stopped " + ipAddress);
            } catch (Exception e)
            {
            }
        }
    }