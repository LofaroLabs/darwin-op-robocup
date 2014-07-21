package sim.app.horde.scenarios.pioneer.vision;

import java.io.*; 
import java.net.*;
import java.util.*;

public class BlobDisplay {
 
    public static BlobData data = null;
    public static Object dataLock = new int[0];
        
    public static void main(String[] args) throws Exception 
        {
        int port = 8050;
        String hostname = args[0] ;
                
                
        // fire up thread on socket
        final Socket s = new Socket(hostname, port);
        Thread thread = new Thread(new Runnable()
            {
            public void run()
                {
                try
                    {
                    Scanner scanner = new Scanner(s.getInputStream());
                    PrintStream out = new PrintStream(new BufferedOutputStream(s.getOutputStream()));
                    while(true)
                        {
                        BlobData bd = new BlobData();
                        bd.readData(scanner);
                        synchronized(dataLock) {
                            data = bd; 
                            }
                        }
                    }
                catch (Exception e) 
                    {
                    System.err.println("Socket closed or error");
                    }
                                
                try {
                    s.close();
                    }
                catch (Exception e) { System.err.println("CLOSED SOCKET"); 
                    }
                }
            });
        thread.setDaemon(true);
        thread.start(); 
                
        BlobData bd; 
        while (!s.isClosed()) 
            {
            bd = getCurrentData(); 
            if (bd != null) 
                System.out.println(bd); 
                        
            }
                
        }
        
    public static BlobData getCurrentData()
        {
        synchronized(dataLock) {
            return data;
            }
        }

        
                        
    }
