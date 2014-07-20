package sim.app.horde.scenarios.robocup2011;

import sim.app.horde.behaviors.*;

import java.net.*;
import java.io.*;
import java.util.*;

public class HumanoidAgent extends sim.app.horde.Agent
    {
    // Behaviors
    public static final byte BEHAVIOR_NOTHING = 0;
    public static final byte BEHAVIOR_SUCCEED = 1;
    public static final byte BEHAVIOR_FAIL = 2;
        
    public boolean doingEpsilonButtonActions = false;

    // server port
    public static final int PORT = 8050;
        
    // game states
    public static final short GAME_INITIAL = 0;
    public static final short GAME_READY = 1;
    public static final short GAME_SET = 2;
    public static final short GAME_PLAYING = 3;
    public static final short GAME_FINISHED = 4;
        
    // game halves
    public static final short FIRST_HALF = 0;
    public static final short SECOND_HALF = 1;
        
    // camera byte settings
    public static final byte MASTER_CAMERA_C = 0;
    public static final byte SLAVE_CAMERA_C = 1;
    public static final byte UNSET_CAMERA_C = 2;
        
    // ACTIONS
    public static final byte BASIC_BEHAVIOR = -3;
    public static final byte BASIC_BEHAVIOR_BLOCKING = -2;
    public static final byte BUTTON_ACTION_EPSILON = -1;
    public static final byte BUTTON_ACTION = 0;
        
    // to synchronize the variables below
    Object[] lock = new Object[0];
    Object[] signal = new Object[0];
        
    // public variables, always in sync with one another.  You 
    // resynchronize by calling updateData() first.
    public short ballX;
    public short ballY;
    public short ballCamera;
    public short headTilt;
    public short headPan;
    public short ballCanSee;
    public short ballDistance;
    public short ballFloorX;
    public short ballFloorZ;
    public short ballBearing;
    public short galpCanSee;
    public short galpDistance;
    public short galpBearing;
    public short garpCanSee;
    public short garpDistance;
    public short garpBearing;
    public short accelerometer;
    public short gameState;
    public short gameHalf;
    public short gameScore;
    public boolean actionDone;
        
    // private variables, updated by synchronizing on lock first.
    short _ballX;
    short _ballY;
    short _ballCamera;
    short _headTilt;
    short _headPan;
    short _ballCanSee;
    short _ballDistance;
    short _ballFloorX;
    short _ballFloorZ;
    short _ballBearing;
    short _galpCanSee;
    short _galpDistance;
    short _galpBearing;
    short _garpCanSee;
    short _garpDistance;
    short _garpBearing;
    short _accelerometer;
    short _gameState;
    short _gameHalf;
    short _gameScore;
    boolean _actionDone;
        
    // Output stream for socket
    OutputStream out;

    short readShort(InputStream stream) throws IOException          // did I get this backwards?
        {
        int a = stream.read();
        int b = stream.read();
        return (short)( (((short)b) << 8) | ((short)a));
        }

    public HumanoidAgent(HumanoidHorde horde) 
        {
        super(horde);
                
        // fire up thread on socket
        try
            {
            Socket sock = new ServerSocket(PORT).accept();
            final InputStream input = sock.getInputStream();
            out = sock.getOutputStream();
                        
            Thread thread = new Thread(new Runnable()
                {
                public void run()
                    {
                    try {
                        while(true)
                            {
                            short __ballX = readShort(input);
                            short __ballY = readShort(input);
                            short __ballCamera = readShort(input);
                            short __headTilt = readShort(input);
                            short __headPan = readShort(input);
                            short __ballCanSee = readShort(input);
                            short __ballDistance = readShort(input);
                            short __ballFloorX = readShort(input);
                            short __ballFloorZ = readShort(input);
                            short __ballBearing = readShort(input);
                            short __galpCanSee = readShort(input);
                            short __galpDistance = readShort(input);
                            short __galpBearing = readShort(input);
                            short __garpCanSee = readShort(input);
                            short __garpDistance = readShort(input);
                            short __garpBearing = readShort(input);
                            short __accelerometer = readShort(input);
                            short __gameState = readShort(input);
                            short __gameHalf = readShort(input);
                            short __gameScore = readShort(input);
                            boolean __actionDone = ((byte)input.read() != 0);
                            synchronized(lock)
                                {
                                _ballX = __ballX;
                                _ballY = __ballY;
                                _ballCamera = __ballCamera;
                                _headTilt = __headTilt;
                                _headPan = __headPan;
                                _ballCanSee = __ballCanSee;
                                _ballDistance = __ballDistance;
                                _ballFloorX = __ballFloorX;
                                _ballFloorZ = __ballFloorZ;
                                _ballBearing = __ballBearing;
                                _galpCanSee = __galpCanSee;
                                _galpDistance = __galpDistance;
                                _galpBearing = __galpBearing;
                                _garpCanSee = __garpCanSee;
                                _garpDistance = __garpDistance;
                                _garpBearing = __garpBearing;
                                _accelerometer = __accelerometer;
                                _gameState = __gameState;
                                _gameHalf = __gameHalf;
                                _gameScore = __gameScore;
                                if (_actionDone)
                                    synchronized(signal) { signal.notifyAll(); }
                                }
                            }
                        }
                    catch (IOException e) { } // die
                    }
                });
            thread.setDaemon(true);
            thread.start();
            }
        catch (Exception e)
            {
            System.err.println("Could not start up Humanoid Agent");
            e.printStackTrace();
            System.exit(1);
            }
        }
        
    public void go()
        {
        updateData();
        System.err.println("X: " + ballFloorX + " Z: " + ballFloorZ + " Bearing: " + ballBearing + " See: " + ballCanSee);
        super.go();
        }
        
    public void updateData()
        {
        synchronized(lock)
            {
            ballX = _ballX;
            ballY = _ballY;
            ballCamera = _ballCamera;
            headTilt = _headTilt;
            headPan = _headPan;
            ballCanSee = _ballCanSee;
            ballDistance = _ballDistance;
            ballFloorX = _ballFloorX;
            ballFloorZ = _ballFloorZ;
            ballBearing = _ballBearing;
            galpCanSee = _galpCanSee;
            galpDistance = _galpDistance;
            galpBearing = _galpBearing;
            garpCanSee = _garpCanSee;
            garpDistance = _garpDistance;
            garpBearing = _garpBearing;
            accelerometer = _accelerometer;
            gameState = _gameState;
            gameHalf = _gameHalf;
            gameScore = _gameScore;
            actionDone = _actionDone;
            }
        }
        
    public void pushButton(byte action)
        {
        if (doingEpsilonButtonActions)
            pushButtonEpsilon(action);
        else 
            {
            System.err.println("PUSH BUTTON: " + (char)(action));
            /*try
              {
              out.write(((byte)'0') + (byte)BUTTON_ACTION);
              out.write(action);
              out.write(13);  // or something.  A third byte
              }
              catch (IOException e)
              {
              System.err.println("Can't push button: stream gone.");
              }
            */
            }
        }

    public void pushButtonEpsilon(byte action)
        {
        System.err.println("PUSH BUTTON EPSILON: " + (char)(action));
        /*try
          {
          out.write(((byte)'0') + (byte)BUTTON_ACTION_EPSILON);
          out.write(action);
          out.write(13);  // or something.  A third byte
          }
          catch (IOException e)
          {
          System.err.println("Can't push button epsilon: stream gone.");
          }
        */
        }


    public void doBlockingBehavior(byte action)
        {
        System.err.println("BLOCKING BEHAVIOR: " + (char)(action));
        /*try
          {
          out.write(((byte)'0') + (byte)BASIC_BEHAVIOR_BLOCKING);
          out.write(action);
          out.write(13);  // or something.  A third byte
          }
          catch (IOException e)
          {
          System.err.println("Can't do blocking behavior: stream gone.");
          }
        */
        }

    public void doBehavior(byte action)
        {
        System.err.println("(NON-BLOCKING) BEHAVIOR: " + (char)(action));
        /* try
           {
           out.write(((byte)'0') + (byte)BASIC_BEHAVIOR);
           out.write(action);
           out.write(13);  // or something.  A third byte
           }
           catch (IOException e)
           {
           System.err.println("Can't do (non-blocking) behavior: stream gone.");
           }
        */
        }

    public void doMotion(byte action, int times)
        {
        System.err.println("MOTION: " + (char)(action) + " times: " + times);
        /* try
           {
           synchronized(lock)
           {
           out.write(((byte)'0') + (byte)times);
           out.write(action);
           out.write(13);  // or something.  A third byte
                        
           // block until motion is done
           try { synchronized(signal) { signal.wait(2000); } } catch (InterruptedException e) { System.err.println("THIS SHOULD NOT HAVE BEEN INTERRUPTED!\n"); }
           }
           }
           catch (IOException e)
           {
           System.err.println("Can't do motion: stream gone.");
           }
        */
        }

    // actions
    public static final byte RCB4_MOT_BOW                                                               = (byte) 'A';
    public static final byte RCB4_MOT_HOME                                                                  = (byte) 'B';
    public static final byte RCB4_MOT_WAVE                                                                  = (byte) 'C';
    public static final byte RCB4_MOT_GETUPFRONT                                                    = (byte) 'D';
    public static final byte RCB4_MOT_GETUPBACK                                                             = (byte) 'E';
    public static final byte RCB4_MOT_LEFT_KICK                                                             = (byte) 'F';
    public static final byte RCB4_MOT_RIGHT_KICK                                                    = (byte) 'G';
    public static final byte RCB4_MOT_GOAL_BLOCK                                                    = (byte) 'H';           
    public static final byte RCB4_MOT_PIVOT_OUT_LEFT                                                = (byte) 'I';           // For the goalie: outer (back) pivot
    public static final byte RCB4_MOT_PIVOT_OUT_RIGHT                                               = (byte) 'J';           // For the goalie: outer (back) pivot
    public static final byte RCB4_MOT_FORWARD_BUTTON                                                = (byte) 'K';
    public static final byte RCB4_MOT_BACKWARD_BUTTON                                               = (byte) 'L';
    public static final byte RCB4_MOT_LEFT_STEP_BUTTON                                              = (byte) 'M';
    public static final byte RCB4_MOT_RIGHT_STEP_BUTTON                                             = (byte) 'N';
    public static final byte RCB4_MOT_LEFT_TURN_BUTTON                                              = (byte) 'O';
    public static final byte RCB4_MOT_RIGHT_TURN_BUTTON                                             = (byte) 'P';
    public static final byte RCB4_MOT_PIVOT_LEFT                                                    = (byte) 'Q';           
    public static final byte RCB4_MOT_PIVOT_RIGHT                                                   = (byte) 'R';
    public static final byte RCB4_MOT_CALIBRATE_HOME                                                = (byte) 'S';           // Goes to home position and then calibrates servos.  Takes a bit of time.
        
    /// buttons
    public static final byte FORWARD_BUTTON                                                                 = (byte) 'A';
    public static final byte BACKWARD_BUTTON                                                                = (byte) 'B';
    public static final byte LEFT_STEP_BUTTON                                                               = (byte) 'C';
    public static final byte RIGHT_STEP_BUTTON                                                              = (byte) 'D';
    public static final byte LEFT_TURN_BUTTON                                                               = (byte) 'E';
    public static final byte RIGHT_TURN_BUTTON                                                              = (byte) 'F';

    }
