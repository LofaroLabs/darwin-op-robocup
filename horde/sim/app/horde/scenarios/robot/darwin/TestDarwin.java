/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.horde.scenarios.robot.darwin;

import java.io.IOException;
import sim.app.horde.scenarios.robot.darwin.comm.DarwinComm;
import sim.app.horde.scenarios.robot.darwin.comm.DarwinParser;

/**
 *
 * @author drew
 */
public class TestDarwin {
    
    
    public static void main(String [] args) throws IOException {
        DarwinComm featureComm = new  DarwinComm("10.0.0.52", 40003, new DarwinParser(), (byte) 0);
        Thread th = new Thread(featureComm);
        
        //featureComm.startSending();
        th.start();
        
        
        }
    }
