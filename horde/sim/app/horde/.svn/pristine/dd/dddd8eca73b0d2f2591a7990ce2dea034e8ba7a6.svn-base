/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.horde.scenarios.robot.flockbots;


import sim.app.horde.Horde;
import sim.app.horde.scenarios.robot.behaviors.CommandMotions;
import sim.app.horde.scenarios.robot.flockbots.comm.FlockbotParser;
import sim.app.horde.scenarios.robot.flockbots.comm.RemoteControlComm;


/**
 *
 * @author drew
 */
public class FlockbotAgent extends sim.app.horde.agent.SimAgent {

    private static final long serialVersionUID = 1L;
	Flockbot bot;
    RemoteControlComm behCom;
    int myID = 0;
    static int curAgent = 0;
    
    static FlockbotEnum[] available = new FlockbotEnum[8];
    {
        available[0] = FlockbotEnum.THIRTYTHREE;//FlockbotEnum.THIRTYSIX;
        available[1] = FlockbotEnum.THIRTYSEVEN;
        /*
        available[0] = FlockbotEnum.THIRTY;
        available[1] = FlockbotEnum.THIRTYONE;
        available[2] = FlockbotEnum.THIRTYTWO;
        available[3] = FlockbotEnum.THIRTYTHREE;
        available[4] = FlockbotEnum.THIRTYFOUR;
        available[5] = FlockbotEnum.THIRTYFIVE;
        available[6] = FlockbotEnum.THIRTYSIX;
        available[7] = FlockbotEnum.THIRTYSEVEN;*/
    }
    
    
    private static final String[] types = {"Global", "Basic", "Flockbots"};
    public FlockbotAgent(Horde horde)
    {
        
        super(horde);
        myID = curAgent;
        curAgent++;
        this.bot = available[myID].build();
        
        // otherwise i will need a static Integer that will tell me which
        // bot I am at in building
        addTypes(types);
        
        
    }

    @Override
    public String getAgentName() {
        return super.getAgentName() + "_" + myID; //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void go() {
        super.go(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    
    /**
     * 
     * @param horde
     * @param bot create the bot by using the Flockbot enum
     */
    public FlockbotAgent(Horde horde, FlockbotEnum bot) {
        super(horde);
        this.bot = bot.build();
        
    }
    public FlockbotParser getCurrentData() {
        return (FlockbotParser) bot.getParser();
    }
    
    public void sendMotion(CommandMotions cms) {
        // check with horde?
        
        // interact with the robot to send the command
        bot.sendCommand(cms);
    }
    
    public void sendMotion(CommandMotions cms, byte customSpeed) {
        bot.sendCommand(cms, customSpeed);
    }
    
    
}
