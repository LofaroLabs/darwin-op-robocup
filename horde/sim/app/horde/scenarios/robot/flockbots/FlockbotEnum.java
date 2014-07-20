/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.horde.scenarios.robot.flockbots;

/**
 * Each flockbot has some special things so set them here
 * @author drew
 */
public enum FlockbotEnum {
    THIRTY("10.0.0.30", 2005, (byte) 20),
    THIRTYONE("10.0.0.31", 2005, (byte) 20),
    THIRTYTWO("10.0.0.32", 2005, (byte) 20),
    THIRTYTHREE("10.0.0.33", 2005, (byte) 20),
    THIRTYFOUR("10.0.0.34", 2005, (byte) 20),
    THIRTYFIVE("10.0.0.35", 2005, (byte) 20),
    THIRTYSIX("10.0.0.36", 2005, (byte) 20),
    THIRTYSEVEN("10.0.0.37", 2005, (byte) 20);
    
    

    private final byte speed;
    private final String host;
    private final int port;
    private FlockbotEnum(String ip, int port, byte speed) {
        this.speed = speed;
        this.host = ip;
        this.port = port;
    }
    public Flockbot build() {
        return new Flockbot(host, port, speed);
    }
}
