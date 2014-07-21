/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.horde.scenarios.robot.flockbots.behaviors;

import sim.app.horde.scenarios.robot.behaviors.CommandMotions;

/**
 * defines flockbot motions
 * @author drew
 */
public enum Motions implements CommandMotions{
    STOP,// stop moving
    FORWARD, // move forward until stop
    LEFT, // turn left until stop
    RIGHT, // turn right until stop
    FINISHED; // stop behavior
    
    
    
    
    private Motions() {
    }
    
    
    
    /**
     * returns the byte command that corresponds to this.
     * forward, left, right require speed.
     * @param speed the speed that the  f,l,r command will be set to.
     * the argument should be positive and this method will convert to +,- 
     * depending on right/left turn.
     * @return  the byte command that will 
     */
    @Override
    public byte[] command(byte speed) {
        switch(this) {
            case STOP:
                return new byte []{6,'R', 'T', 3,'D', 'S', 'T'};
            case FORWARD:
                return new byte [] {7, 'R', 'T',4,'D', 'M', 'S', speed};
            case LEFT: // +speed
                return new byte [] {7,'R', 'T', 4,'D','R','C',speed};
            case RIGHT:// -speed
                return new byte [] {7,'R', 'T', 4,'D','R','C',(byte) (-1 * speed)};
            case FINISHED:// stop behavior
                return new byte [] {2,'B','F'};
            default:
                return null;
        }
    }
}
