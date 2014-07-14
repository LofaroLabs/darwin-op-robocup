package sim.app.horde.scenarios.humanoid.console;

import edu.gmu.robocup.*;
import edu.gmu.robocup.horde.*;
 
public class HordeNetworkReader extends NetworkSRV1Reader {

    //private DatagramPacket accelRequest = null; 
    // comparison values for the accelerometer to determine if we fell over 
    //private static final int FRONT_FALL = 115; 
    //private static final int BACK_FALL = 395; 

    boolean checkAccel = true; 

    static { 
        System.loadLibrary("kondo_java");
        }
        
    public KondoInstance ki;
        
    public HordeNetworkReader(String host, int port, int udp_local_port, String transport) {

        super(host, port, udp_local_port, transport); 
        setClassName(HordeNetworkReader.class.getName());

        //accelRequest = new DatagramPacket("ks01".getBytes(), 4, getHost(), getPort()) ;
                 
        ki = new KondoInstance(); 
        if (kondo.kondo_init(ki) < 0) {
            System.out.println(ki.getError()); 
            }       
        }

        
        

    public void doUserFunction() throws Exception
        {
        /*int bytes=0; 
                 
          byte[] buf = new byte[MTU];
          // check the accelerometer to see if we fell over.  if necessary, stand up 
          checkAccel = true; 
          _send(accelRequest); 
          bytes = _read(buf); 
          if (bytes > 0) { // got something back, so parse it 
          String str = new String(buf).trim(); 
          if (str.startsWith("Value of AD Port")) { 
          String[] tokens = str.split(" "); 
          int accel = Integer.valueOf(tokens[5]);                                                         
          if (accel < FRONT_FALL) { 
          String s = "km0" + Motions.RCB4_MOT_GETUPFRONT; 
          _send(s.getBytes()); 

          // wait for robot to stand up 
          try { 
          Thread.sleep(6000); 
          } catch (Exception e) {}
          }
          else if (accel > BACK_FALL) { 
          String s = "km" + Motions.RCB4_MOT_FLIP_AND_GETUP; 
          _send(s.getBytes()); 
          try { 
          Thread.sleep(7000); 
          } catch(Exception e) {}
          }
          }
          }  */
        }
    }
