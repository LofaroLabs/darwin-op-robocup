/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.horde.scenarios.robot.flockbots.comm;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sim.app.horde.scenarios.robot.comm.DefaultParser;
import sim.app.horde.scenarios.robot.comm.ParserCallback;

/**
 * THis parses the commands that are received by an android controller.
 * @author drew
 */
public class RCParser extends DefaultParser {
    
    
    private final Object lock = new Object[0];
    String readings = "";
    ParserCallback pcallback;
    
    public void addCallback(ParserCallback parserCallback) {
        pcallback = parserCallback;
    }
    
    @Override
    public void setInput(byte[] input) {
        super.setInput(input); 
        
        synchronized(lock) {
            System.err.println("Going to read something");
            
            try {
                readings = new String(input, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                System.err.println("Error converting bytes from socket to string" + ex);
            }
            System.out.print(readings);
        }
        // whenever I get input do something with it.
        if (pcallback != null) {
            pcallback.doSomething(this);
        }
    }
    
    public String getAction() {
        synchronized(lock) { return readings; }
    }
    
    public String toString() {
        return getAction() + "\n";
    }
}
