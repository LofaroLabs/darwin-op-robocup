
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.horde.scenarios.robot.comm;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Improve upon the Communication class to not depend on byte strings and depend
 * on the newline being at the end of a read of the socket
 * @author drew
 */
public abstract class StringComm extends Communication{

    protected BufferedReader reader;
    
    public StringComm(String host, int port, Parse parser, byte readDelayMS) throws IOException {
        super(host, port, parser, readDelayMS);
        // wrap the datainput stream
         
          reader = new BufferedReader(new InputStreamReader(in));
    }

    public StringComm(int port, Parse parser, byte readDelayMS) throws IOException {
        super(port, parser, readDelayMS);
    }

    
    
    
    @Override
    protected void read() {
        
        if (reader == null && connected == false)
            return;
        // "in" is created in a seperate thread if acting as a server
        if (reader == null && connected == true)
            reader = new BufferedReader(new InputStreamReader(in));
        
        try {
            if (reader.ready()) {
                String line = reader.readLine();
                myParser.setInput(line.getBytes());
            }
        } catch (IOException ex) {
            Logger.getLogger(StringComm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
