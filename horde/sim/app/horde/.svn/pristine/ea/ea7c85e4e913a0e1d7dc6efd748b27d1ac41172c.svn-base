/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.horde.scenarios.robot.darwin.comm;

import com.google.gson.Gson;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;
import sim.app.horde.scenarios.robot.comm.Communication;
import sim.app.horde.scenarios.robot.comm.Parse;
import sim.app.horde.scenarios.robot.comm.StringComm;

/**
 * 
 * @author drew
 */
public class DarwinComm extends Communication {

     BufferedReader br;
    Gson json = new Gson();
    
    public DarwinComm(String host, int port, Parse parser, byte readDelayMS) throws IOException {
        super(host, port, parser, readDelayMS);
        }

    @Override
    protected void read() {
        try {
            
            String line = br.readLine();
            
            //System.err.println("DarwinComm " + System.identityHashCode(this) + " updated with " + socket + " calling " + myParser);
            
            if (line != null) {
                myParser.setInput(line.getBytes());
                
                }
            } catch (IOException ex) {
            Logger.getLogger(DarwinComm.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        
        }

    
    
    
    @Override
    protected void makeClientConnection() throws IOException {
        super.makeClientConnection(); 
        br = new BufferedReader(new InputStreamReader(in));
 
        }

    @Override
    public void run() {
        super.run(); //To change body of generated methods, choose Tools | Templates.
        }
    
    
    class StartSending {
        String action = "StartSending";
        int args[] = {1};
        int ackNumber = 0;
        }
    class StopSending {
        String action = "StopSending";
        String args = "";
        }

    @Override
    public void startSending() {
        System.err.println("Tell darwin to start sending me the feature info!!!");
        try {
            write((json.toJson(new StartSending()) + "\n").getBytes());
            } catch (IOException ex) {
            Logger.getLogger(DarwinComm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    @Override
    public void terminateSending() {
        System.err.println("Tell darwin to stop the thread that has been sending me data.");
        try {
            write((json.toJson(new StopSending()) + "\n").getBytes());
            } catch (IOException ex) {
            Logger.getLogger(DarwinComm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    
    }
