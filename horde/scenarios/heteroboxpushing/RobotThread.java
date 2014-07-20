package sim.app.horde.scenarios.heteroboxpushing;

import java.util.*;


public class RobotThread extends Thread
    {
    BoxHitab hitab;
    ArrayList<RobotListener> frameListeners = new ArrayList<RobotListener>();

    public boolean displayGraphics = false;

    public RobotThread(BoxHitab h) 
        {
        hitab = h;
        }

    public void addListener(RobotListener rla)
        {
        frameListeners.add(rla);
        }

    public void run()
        {
        while (true)
            {
            // shut everything down
            if (Thread.interrupted())
                {
                System.err.println("RobotThread interuupted"); 
                                
                displayGraphics = false;
                for (int i = 0; i < frameListeners.size(); i++)
                    frameListeners.get(i).stop();
                break;
                }

            if (displayGraphics)
                {
                for (int i = 0; i < frameListeners.size(); i++)
                    frameListeners.get(i).updateGraphics(hitab);
                }
            }
        }
    }
