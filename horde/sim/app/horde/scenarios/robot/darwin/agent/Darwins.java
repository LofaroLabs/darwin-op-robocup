/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.horde.scenarios.robot.darwin.agent;

import sim.app.horde.scenarios.robot.darwin.agent.Darwin;

/**
 *
 * @author drew
 */
public class Darwins {
     private static final long serialVersionUID = 1;

     public static Darwins FIFTY = new Darwins("10.0.0.50", 4009, (byte)1);
    public static Darwins FIFTYONE = new Darwins("10.0.0.51", 4009, (byte)1);
    public static Darwins FIFTYTWO = new Darwins("10.0.0.52", 4009, (byte)1);
    public static Darwins FIFTYTHREE = new Darwins("192.168.117.53", 4009, (byte)1);
    public static Darwins FIFTYFOUR = new Darwins("10.0.0.64", 4009, (byte)1);
    public static Darwins FIFTYFIVE = new Darwins("10.0.0.55", 4009, (byte)1);
    public static Darwins FIFTYSIX = new Darwins("10.0.0.66", 4009, (byte)1);
    public static Darwins FIFTYSEVEN = new Darwins("10.0.0.67", 4009, (byte)1);
    public static Darwins LOCALHOST = new Darwins("127.0.0.1", 4009, (byte)1);
    
    private final byte speed;
    private final String host;
    private final int port;
    private Darwins(String ip, int port, byte speed) {
        this.speed = speed;
        this.host = ip;
        this.port = port;
        }
    public Darwin build() {
        return new Darwin(host, port, speed);
        }
    
    }
