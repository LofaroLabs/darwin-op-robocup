/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.horde.scenarios.robot.behaviors;

/**
 * Enums can implement this so that they can be easily integrated into 
 * Communication.
 * @author drew
 */
public interface CommandMotions {
    /**
     * returns the byte array that corresponds to this command to be used to send
     * to the robot
     * @param speed optional
     * @return the byte array that corresponds to the implemented command.
     */
    public byte[] command(byte speed);
}
