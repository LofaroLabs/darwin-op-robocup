/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.horde.scenarios.robot.flockbots;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sim.app.horde.*;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.scenarios.robot.flockbots.comm.RCParser;
import sim.app.horde.scenarios.robot.flockbots.comm.RemoteControlComm;
import sim.engine.SimState;

public class FlockbotHordeWithUI extends SimHordeWithUI
{
    RCParser rcp;
    boolean setup;
    public FlockbotHordeWithUI()
    {
        this(new FlockbotHorde(System.currentTimeMillis()));
        
        // set up the server thread to receive behavior commands 
        rcp = new RCParser();
        try {
            RemoteControlComm rccomm = new RemoteControlComm(12345, rcp, (byte) 0);
            Thread th = new Thread(rccomm);
            th.start();
        } catch (IOException ex) {
            System.err.println("Error creating the rccomm in FLockbotHordeWithUI constructor: " + ex);
        }
        
        // set up server to serve agent data
        
        
    }

    public FlockbotHordeWithUI(SimState state)
    {
        super(state);
    }
    
    public void setup() {
        if(!setup) {
            
            
            
            
        }
    }

    @Override
    public void quit() {
        super.quit(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    

    @Override
    public void start() {
        super.start();
        
        // so going to assume that I am going to have given the remote device
        // the name of the behaviors.
        
        Horde horde = ((Horde) state);
        
        /*
        Horde horde = ((Horde) state);
        String newBeh = rcp.getAction();
        if(newBeh.equals("Forward")) {
            horde.getTrainingMacro().userChangedBehavior(horde, FORWARD);
        }else if (newBeh.equals("Left")) {
            horde.getTrainingMacro().userChangedBehavior(horde, TURN_LEFT);
        }else if (newBeh.equals("Right")) {
            horde.getTrainingMacro().userChangedBehavior(horde, TURN_RIGHT);
        }else if (newBeh.equals("Stop")) {
            horde.getTrainingMacro().userChangedBehavior(horde, STOP);
        }
        */
        
        // this is where I can get the behaviors and associate the 
        Macro macro = ((Horde) state).getTrainingMacro();
        for (int i = 0; i < macro.behaviors.length; i++) {
            System.err.println("Behavior[" + i + "] " + macro.behaviors[i]);
            
            
        }
        
        
    }
        
        
        
        

        @Override
	public void setupPortrayals()
        {
            setupAgentsAndPlacesPortrayals();
        }



	public static void main(String[] args)
        {
            new FlockbotHordeWithUI().createController();
        }
}

