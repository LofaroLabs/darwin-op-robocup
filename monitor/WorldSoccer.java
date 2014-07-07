
import javax.swing.*;
import javax.swing.event.*;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.lang.*;
import java.io.*;

import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class WorldSoccer implements ActionListener {




	static JFrame frame;
	JPanel totalGUI;
	public JButton Comp,Test;
	static FieldDraw currentField = null;
	static final Object lock = new Object();
	static int x_position; // in cm
	static int y_position; // in cm
	static double orientation; // in radians
	static String teamid;
	static String id;
	static volatile ArrayList<BufferedReader> inputs = new ArrayList<BufferedReader>();


	public JPanel createContentPane (){
    	//******************Main Panel ***********************

		totalGUI = new JPanel();
		totalGUI.setLayout(null);
		totalGUI.setVisible(true);


		//************** Menu ********************************




		Comp = new JButton("Competition Field");
	  	Comp.setLocation(20, 50);
	    Comp.setSize(250, 50);
	    Comp.setVisible(true);
	    Comp.addActionListener(this);
		Comp.setEnabled(true);
	    totalGUI.add(Comp);

	    Test = new JButton("Our Field");
	  	Test.setLocation(20, 150);
	    Test.setSize(250, 50);
	    Test.setVisible(true);
	   	Test.addActionListener(this);
		Test.setEnabled(true);
	    totalGUI.add(Test);


		return totalGUI;



	}


	public void actionPerformed(ActionEvent e) {
			//dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
			if(currentField != null)
				currentField.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
			if (e.getSource()==Comp){//competition
                                if (FieldDraw.th != null) {
                                    FieldDraw.running = false;
                                }
				currentField = new FieldDraw(1,x_position,y_position,orientation,teamid,id);
				currentField.setVisible(true);
			}
			if (e.getSource()==Test){//our field
                                if (FieldDraw.th != null) {
                                    FieldDraw.running = false;
                                }
				currentField = new FieldDraw(0,x_position,y_position,orientation,teamid,id);
				currentField.setVisible(true);
			}

	}

	private static void createAndShowGUI() {

        JFrame.setDefaultLookAndFeelDecorated(true);
        frame = new JFrame("~*~ World Soccer ~*~");

        //Create and set up the content pane.
		WorldSoccer demo = new WorldSoccer();
        frame.setContentPane(demo.createContentPane());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);
        frame.setVisible(true);
    }
	
	public static final int PORT = 11111;
	public static final int MAX_PACKET_LENGTH = 64 * 1024;

	public static void main (String[] args) throws Exception{
		
		try {

			//invoke gui
			SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});


		}
		/*
		catch (SocketException e){
			System.out.println("Error reading/opening socket\n"+ e);
		}*/
		catch (Exception e){
			System.out.println("Error:\n\tUsage : java WorldSoccer hostname portnumber");
		}
		
		// create a thread to accept connections to socket
		Runnable r = new Runnable() {

                    @Override
                   /* public void run() {
                        try {
							ServerSocket welcomeSocket = new ServerSocket(40002);//support more than one socket
                        while(true == true) {
                                System.out.println("waiting for connection");
                                Socket connectionSocket = welcomeSocket.accept();
                                System.out.println("connection is good");
                                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                                synchronized(lock){
									inputs.add(inFromClient);
								}
                            }
                        } catch (IOException ex) {
                                System.out.println("Socket thread for connection encountered an exception.");
                                System.err.println(ex);
                            }
                    }
                    */
                    
                    public void run() 
                    	{
 						try
 							{
 							DatagramSocket sock = new DatagramSocket(PORT);  // automatically bound to the wildcard address
							DatagramPacket pack = new DatagramPacket(new byte[MAX_PACKET_LENGTH], MAX_PACKET_LENGTH);
							while(true)
								{
								sock.receive(pack);
								Node node = Sean.getNode(new String(pack.getData()), 0);
								//System.err.println(node);
								if (currentField != null) { currentField.update(node); }
                   				}
                   			}
                   		catch (Exception e)
                   			{
                   			e.printStackTrace();
                   			}
                    	}
                    
                };
                
                Thread th = new Thread(r);
                th.start();
		
		
		boolean debug = true;
		
		/*
		while(true){
		
			synchronized(lock){
				if (debug){
					//Particle P#100,botid, x, y ,a ,w
					//Robot x , y , a , id ,teamid
					String inputR = "0,0,0,1,19";
					String input = "P#100,1,1,1,0,0.7";
					String input2 = "P#101,2,2,1,0,0.2";
					String input3 = "P#102,1.5,1.4,1,0,1";
					if(currentField!=null){
						currentField.update(inputR);
						currentField.update(input);
						currentField.update(input2);
						currentField.update(input3);
					//	System.out.println(input);
					}else{
						try{Thread.sleep(100);} catch(Exception e){}
					}
				}
				else{
				for (BufferedReader inFromClient : inputs) {
					String input =  inFromClient.readLine();
					//String input = "-123,111,0.34,1111,222";
					if(currentField!=null){
						currentField.update(input);
					//	System.out.println(input);
					}else{
						try{Thread.sleep(100);} catch(Exception e){}
					}
					//System.out.print("");//magic
				}
			}
			}
		}
		*/

	}

} 
