package sim.app.horde.scenarios.pioneer;

import java.util.*;

public class RobotThread extends Thread
    {
    PioneerHorde horde;
    ArrayList<RobotListenerAdapter> frameListeners = new ArrayList<RobotListenerAdapter>();

    public boolean displayGraphics = false;

    public RobotThread(PioneerHorde h) {
        horde = h;
        }

    public void addListener(RobotListenerAdapter rla)
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
                displayGraphics = false; 
                for (int i = 0; i < frameListeners.size(); i++)
                    frameListeners.get(i).stop();
                break;
                }

            if (displayGraphics)
                {
                for (int i = 0; i < frameListeners.size(); i++)
                    frameListeners.get(i).updateGraphics(horde);
                }
            }
        }
    }
