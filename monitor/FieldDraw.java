
import javax.swing.*;
import javax.swing.event.*;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.Color;
import java.awt.event.*;
import java.util.*;
import java.lang.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Center of Field is at 0,0
 Center of Virtual Field is 500,350
 */

public class FieldDraw extends JFrame implements Runnable {

    
    static Thread th;
    static volatile boolean running = false;
    private Image dbImage;
    private Graphics dbg;
    private int width; // A
    private int height; // B
    private int penalty_mark_diam;
    private int goal_width; // D
    private int penalty_area_length; // E
    private int penalty_area_width; // F
    private int penalty_mark_distance; // G
    private int center_circle_diam; // H

	public static final int NUM_ROBOTS = 4;
    //private int x_coordinate;
    //private int y_coordinate;
    //private double angle;
    //private String teamid;
    //private String id;
    //private int Robotindex;
    public ArrayList<Robot> robotArray = new ArrayList<Robot>(NUM_ROBOTS);

	
	Color trans(Color c)
		{
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), 128);
		}
		
    Color[] botColors = {trans(Color.red), trans(Color.blue), trans(Color.green), trans(Color.orange), trans(Color.cyan), trans(Color.magenta), trans(Color.yellow), trans(Color.white) };
	public Color botColor(Robot robot)
		{
		try { return botColors[Integer.parseInt(robot.id) - 1]; }		
		catch (Exception e) { return Color.black; } 
		}
		

    public FieldDraw(int l, int x, int y, double a, String tid, String i) {
        for (int q = 0; q < 8; q++) {
            robotArray.add(new Robot(q));
        }
        //x_coordinate = x;
        //y_coordinate = y;
        //angle = a;
        //teamid = tid;
        //id = i;

        if (l == 0) {
            /*
             Our Field :
             A - 548
             B - 360
             D - 144 
             E - 48 (using int)
             F - 200
             G - 165 
             H - 112
             */
            width = 548; // A
            height = 360; // B
            penalty_mark_diam = 6;  // it's actually 10 but we make it 6 to be different from the balls
            goal_width = 144; // D
            penalty_area_length = 48; // E
            penalty_area_width = 200; // F
            penalty_mark_distance = 165; // G
            center_circle_diam = 112; // H
        } else if (l == 1) {
            /*
             Competition :
             See Page 1 of http://www.informatik.uni-bremen.de/humanoid/pub/Website/Downloads/HumanoidLeagueRules2014-01-10.pdf
             A - 900
             B - 600
             D - 225
             E - 60
             F - 345
             G - 180
             H - 150
             */

            width = 900; // A
            height = 600; // B
            penalty_mark_diam = 6;  // it's actually 10 but we make it 6 to be different from the balls
            goal_width = 225; // D
            penalty_area_length = 60; // E
            penalty_area_width = 345; // F
            penalty_mark_distance = 180; // G
            center_circle_diam = 150; // H
        }
        setSize(width + TRANSFORM_X * 2, height + TRANSFORM_Y * 4);
        setLocationRelativeTo(null);
        setVisible(true);
        th = new Thread(this);
        running = true;
        th.start();

    }

    public void run() {
        Random generator = new Random();
        int id = generator.nextInt(Integer.MAX_VALUE);
        
        while (running) {
            repaint();
           try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                System.err.println("Thread sleep failed!");
            }
        }
    }

    public void paint(Graphics g) {
		//super.paintComponent(g);

		//makes graphics smooth. This is hack from how I used to do it with applets, since you just override the update method. For some reason this doesnt work for JFrames, so i implemented it like this.
        //There's probably a better way to do this
        if (g != dbg) {
            update(g);
            return;
        }
		//end hack

        //now draw everything
        g.setColor(Color.black);
        drawRobot(g);
        // add teamid and id to robot dot
        drawField(g);
        //put all those particles on the field
		
        //drawParticles(g);
    }

	public static final int TRANSFORM_X = 50;
	public static final int TRANSFORM_Y = 50;


	public static final int GOAL_DEPTH = 30;

    public void drawField(Graphics g) {
        //draw outer field lines
        g.drawRect(TRANSFORM_X, TRANSFORM_Y, width, height);
        //draw center circle
        g.drawOval( TRANSFORM_X + width / 2 - center_circle_diam / 2, TRANSFORM_Y + height / 2 - center_circle_diam / 2, center_circle_diam, center_circle_diam);
        //draw center line
        g.drawLine(TRANSFORM_X + width / 2, TRANSFORM_Y, TRANSFORM_X + width / 2, TRANSFORM_Y + height);
        //draw penalty box left
        g.drawRect(TRANSFORM_X, (TRANSFORM_Y + height / 2) - (penalty_area_width / 2), penalty_area_length, penalty_area_width);
        //draw penalty box right
        g.drawRect((width + TRANSFORM_X) - penalty_area_length, (TRANSFORM_Y + height / 2) - (penalty_area_width / 2), penalty_area_length, penalty_area_width);
        //draw center mark left
        g.drawOval(width/2 + TRANSFORM_X - penalty_mark_diam / 2, TRANSFORM_Y + height / 2  - penalty_mark_diam / 2, penalty_mark_diam, penalty_mark_diam);
        //draw penalty mark left
        g.drawOval(penalty_mark_distance + TRANSFORM_X - penalty_mark_diam / 2, TRANSFORM_Y + height / 2  - penalty_mark_diam / 2, penalty_mark_diam, penalty_mark_diam);
        //draw penalty mark right
        g.drawOval((width + TRANSFORM_X * 2) - (penalty_mark_distance + TRANSFORM_Y) - penalty_mark_diam / 2, TRANSFORM_Y + height / 2 - penalty_mark_diam / 2, penalty_mark_diam, penalty_mark_diam);
        //draw goal left
        g.drawRect(TRANSFORM_X - GOAL_DEPTH, (TRANSFORM_Y + height / 2) - (goal_width / 2), GOAL_DEPTH, goal_width);
        //draw goal right
        g.drawRect(TRANSFORM_X + width, (TRANSFORM_Y + height / 2) - (goal_width / 2), GOAL_DEPTH, goal_width);
    }

/*
    public void drawParticles(Graphics g) {
        for(int i = 0; i<robotArray.size(); i++){
            Robot currentRobot = robotArray.get(i);
			if (currentRobot.init) {
                Robotindex = Integer.parseInt(currentRobot.teamid) - 1;
				for (Particle myParticle : robotArray.get(i).particleArray) {
					//System.out.println(myParticle.x);
					Color previousc = g.getColor();
					//g.setColor(teamColors[Robotindex]);
                    //System.out.println("Index of Robot:"+Robotindex+" Color of Robot "+ teamColors[Robotindex]);
                    //draw outline of particle to match color of robot
					drawCircle(g, myParticle.x, myParticle.y, myParticle.a, 8,false);
                    //color particle to match prabability between white=0 and black=1
                    int num = 255 - (int)(myParticle.w * 100);
                    Color grayColor = new Color(num,num,num);
                    g.setColor(grayColor);
                    drawCircle(g, myParticle.x+1, myParticle.y+1, myParticle.a, 7,true);
					g.setColor(previousc);
				}
			}
		}
    }
*/

	public static final int ROBOT_WIDTH = 22;  // roughly
	public static final int BALL_WIDTH = 11;  // roughly
	public static final long INVALID = 3 * 1000;  // 3 seconds

    public void drawRobot(Graphics g) {
		for(int i = 0; i<robotArray.size(); i++){
                Robot currentRobot = robotArray.get(i);
				if (currentRobot.init) {
							long time = System.currentTimeMillis();
							// draw the robot
							g.setColor(botColor(currentRobot));
							if (time - currentRobot.timestamp > INVALID)								
								drawCircle(g, currentRobot.x, currentRobot.y, currentRobot.angle, ROBOT_WIDTH, false, true);
							else 
								drawCircle(g, currentRobot.x, currentRobot.y, currentRobot.angle, ROBOT_WIDTH, true, true);
							g.setColor(Color.black);
							
							// draw the robot label
							String s = "id " + currentRobot.id + "  role " + currentRobot.role + "  battery " + currentRobot.battery + "  ip " + currentRobot.ip; // draw robot id
							g.drawString(s, (TRANSFORM_X + width / 2) + currentRobot.x - 30, (TRANSFORM_Y + height / 2) - currentRobot.y + 30);
							s = "status " + currentRobot.status + "  declared " + currentRobot.declared1 + "  " + currentRobot.declared2 + "  " + currentRobot.declared3;
							g.drawString(s, (TRANSFORM_X + width / 2) + currentRobot.x - 30, (TRANSFORM_Y + height / 2) - currentRobot.y + 50);
							if (time - currentRobot.timestamp > INVALID)
								{
								g.setColor(Color.red);
								s = "invalid for " + (time - currentRobot.timestamp) / 1000 + " seconds";
								g.drawString(s, (TRANSFORM_X + width / 2) + currentRobot.x - 30, (TRANSFORM_Y + height / 2) - currentRobot.y + 70);
								}


							// draw the robot ball
							g.setColor(botColor(currentRobot));
							drawCircle(g, currentRobot.ballx, currentRobot.bally, 0, BALL_WIDTH, currentRobot.ballLost == 0.0, false); 
							g.setColor(Color.black);

				}
		}	
    }

    public void drawCircle(Graphics g, int x_coordinate, int y_coordinate, double angle, int size, boolean fill, boolean orient)
    	{
		int x = (TRANSFORM_X + width / 2) + x_coordinate;
		int y = (TRANSFORM_Y + height / 2) - y_coordinate;

        if (fill)
            g.fillOval(	x - (size / 2),
            			y - (size / 2), size, size);
        else 
            g.drawOval(	x - (size / 2),
            			y - (size / 2), size, size);
             
        if (orient)
        	{
        	g.setColor(Color.BLACK);
			//add orientation
			int endX = x + (int) ((size * 2) * Math.cos(angle));
			int endY = y + (int) ((size * 2) * -Math.sin(angle));
			g.drawLine(x, y, endX, endY);
			}
		}    
    
    public void update(Node node)
    	{
    	Robot robot = robotArray.get((int)(node.get("id").valueD));
     	robot.x = (int) (100 * node.get("pose").get("x").valueD);
        robot.y = (int) (100 * node.get("pose").get("y").valueD);
        robot.ballx = (int) (100 * node.get("ballGlobal").get("1").valueD);  // x = 1
        robot.bally = (int) (100 * node.get("ballGlobal").get("2").valueD);  // y = 2
        robot.ballLost = (int) (node.get("ballLost").valueD);
        robot.role = (int) (node.get("role").valueD);
        robot.status = (int) (node.get("status").valueD);
        robot.declared1 = (int) (node.get("declared").get("1").valueD);
        robot.declared2 = (int) (node.get("declared").get("2").valueD);
        robot.declared3 = (int) (node.get("declared").get("3").valueD);
        robot.battery = (node.get("battery_level").valueD);
    	robot.angle = node.get("pose").get("a").valueD;
    	robot.teamid = "" + (int)(node.get("teamNumber").valueD);
    	robot.id = "" + (int)(node.get("id").valueD);
    	robot.ip = (node.name).split("\\.")[3];
    	robot.init = true;
    	robot.timestamp = node.timestamp;
    	}

/*
    public void update(String input) {

        //System.out.println(input.substring(0, 2));
        //-18 is the offset so 0,0 is at the center of the field
        try {//if(input header suggests im a robot){
            if (!input.substring(0, 2).equals("P#")) {
                String[] inputArray = input.split(",");
                x_coordinate = (int) (100 * Double.parseDouble(inputArray[1])) - 20;
                y_coordinate = (int) (100 * Double.parseDouble(inputArray[0])) - 20;
                angle = Double.parseDouble(inputArray[2]) + 1.57;
                teamid = inputArray[3];
                id = inputArray[4];
                // MORE ROBOTS!
                Robotindex = Integer.parseInt(teamid) - 1;
				
				//System.out.println("ROBOT INDEX " + Robotindex);
                robotArray.get(Robotindex).x = x_coordinate;
                robotArray.get(Robotindex).y = y_coordinate;
                robotArray.get(Robotindex).angle = angle;
                robotArray.get(Robotindex).teamid = teamid;
                robotArray.get(Robotindex).id = id;
				robotArray.get(Robotindex).init = true;
            } else {
             //   System.out.println("updating some particle");
                input = input.substring(2, input.length());
                String[] inputArray = input.split(",");
                int index = Integer.parseInt(inputArray[0]);
                robotArray.get(Robotindex).particleArray.get(index).botid = (int) (100 * Double.parseDouble(inputArray[1])) - 2;
                robotArray.get(Robotindex).particleArray.get(index).x = (int) (100 * Double.parseDouble(inputArray[2])) - 2;
                robotArray.get(Robotindex).particleArray.get(index).y = (int) (100 * Double.parseDouble(inputArray[3])) - 2;
                robotArray.get(Robotindex).particleArray.get(index).a = (Double.parseDouble(inputArray[4])) + 1.57;
                robotArray.get(Robotindex).particleArray.get(index).w = (Double.parseDouble(inputArray[5]));
            }
	//	}else if(input header suggests im a particle){
			//
            // edit the particle array at the index specified by the input header
            // NOTE: you need to make a particle array still
            //
		//}

            //System.out.println("update was called");
        } catch (Exception e) {
            System.out.println("what kind of input do you think I take in the input method ?!(socket problem)");
            System.out.println(input);
            e.printStackTrace();
        }
    }
*/

    //make graphics all pretty and stuff
    public void update(Graphics g) {
		// initialize buffer
        //System.out.println("i am called");
        if (dbImage == null) {
            dbImage = createImage(this.getSize().width, this.getSize().height);
            dbg = dbImage.getGraphics();
        }

        // clear screen in background
        dbg.setColor(getBackground());
        dbg.fillRect(0, 0, this.getSize().width, this.getSize().height);

        // draw elements in background
        dbg.setColor(getForeground());
        paint(dbg);

        // draw image on the screen
        g.drawImage(dbImage, 0, 0, this);

    }

    public static void main(String[] args) throws Exception {
        WorldSoccer.main(new String[2]);
    }
}

class Particle {
    int botid, x, y;
    double a,w;
}

class Robot {
	long timestamp;
	String ip;
    int x, y;
    int ballx, bally;
    int ballLost;
    Double angle;
    int role;
    int status;
    int declared1;
    int declared2;
    int declared3;
    double battery;
    String teamid, id;
    ArrayList<Particle> particleArray = new ArrayList<Particle>(200);
	boolean init = false;
    public Robot(int id) {
    	this.id = "" + id;
        for (int q = 0; q < 201; q++) {
            particleArray.add(new Particle());
        }
    }

}
