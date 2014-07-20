/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.horde.scenarios.robot.flockbots;

import java.io.IOException;
import sim.app.horde.scenarios.robot.comm.Communication;
import sim.app.horde.scenarios.robot.flockbots.behaviors.Motions;
import sim.app.horde.scenarios.robot.flockbots.comm.FlockbotComm;
import sim.app.horde.scenarios.robot.flockbots.comm.FlockbotParser;

/**
 *Test class...
 * @author drew
 */
public class TestFlockbot {
    
    
    public static void main(String args[]) throws IOException, InterruptedException {
        
        // test start
        FlockbotAgent agent = new FlockbotAgent(null, FlockbotEnum.THIRTYSEVEN);
        Thread.sleep(1500);
        agent.bot.sendCommand(Motions.FORWARD);
        
        //FlockbotAgent agent2 = new FlockbotAgent(null, FlockbotEnum.THIRTYSEVEN);
        //Thread.sleep(1000);
        //agent2.bot.sendCommand(Motions.FORWARD);
        
        Thread.sleep(1000);
        agent.bot.sendCommand(Motions.STOP);
        
       // agent.bot.sendCommand(Motions.STOP);
        System.err.println("HIHIH");/*
        agent.bot.sendCommand(Motions.FORWARD);
        Thread.sleep(5000);
        agent.bot.sendCommand(Motions.STOP);
        agent.bot.sendCommand(Motions.RIGHT);
        Thread.sleep(1000);
        agent.bot.sendCommand(Motions.STOP);
        agent.bot.sendCommand(Motions.LEFT);
        Thread.sleep(1000);
        agent.bot.sendCommand(Motions.STOP);
             */
        /*
        FlockbotComm comm = new FlockbotComm("10.0.0.33", 2005, new FlockbotParser(), (byte) 30);
        Thread th = new Thread(comm);
        th.start();
        comm.write(Motions.FINISHED.command((byte)30));
          */      
        
    }
}
