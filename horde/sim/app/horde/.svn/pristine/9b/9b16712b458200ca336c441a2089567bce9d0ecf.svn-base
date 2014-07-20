/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.horde.scenarios.robot.comm;

import java.io.IOException;

/**
 * Only Sends no receive.
 * @author drew
 */
public class DefaultSendComm extends Communication {

    public DefaultSendComm(String host, int port, Parse parser, byte readDelayMS) throws IOException {
        super(host, port, parser, readDelayMS);
    }

    
    @Override
    public void startSending() {
        
    }

    @Override
    public void terminateSending() {
        
    }

    @Override
    public void run() {
        // run should not do anything.  this is not a non-blocking send.
    }
    
    
    
}
