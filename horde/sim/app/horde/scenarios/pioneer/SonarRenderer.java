package sim.app.horde.scenarios.pioneer;

import java.awt.*;
import java.awt.geom.GeneralPath;

import javax.swing.*;

public class SonarRenderer extends JPanel
    {
    private static final long serialVersionUID = 1L;

    public final int NUM_SONARS = 16;

    double minDist = -1; 
    double minAngle = -1; 
        
    double angles[] = new double[NUM_SONARS];
    int x[] = new int[NUM_SONARS];
    int y[] = new int[NUM_SONARS];
    double spreadAngle, scaleFactor;

    double sonars[] = new double[NUM_SONARS];
    Dimension panelSize = new Dimension(400, 480);

    GeneralPath originalOutline; 
                
    public SonarRenderer(RobotThread robotThread) {
        setSize(panelSize);
        setBackground(Color.WHITE);
        robotThread.addListener(sonarListener);
                
        angles[0] = Math.PI;
        angles[7] = 0;
        angles[8] = 0;
        angles[15] = Math.PI;
        for (int i = 1; i <= 6; i++)
            angles[i] = Math.PI - Math.PI / 9 * (i + 1);
                
        for (int i = 9; i <= 14; i++)
            angles[i] = 2 * Math.PI - (Math.PI / 9 * (i - 8));
                
        for (int i = 0; i < 16; i++)
            {
            sonars[i] = 0; 
            x[i] = (int) (200 * Math.cos(angles[i]));
            if (i < 8)
                y[i] = (int) (180 + 180 * Math.sin(angles[i]));
            else
                y[i] = (int) (-180+ 180 * Math.sin(angles[i]));
            }
        spreadAngle = Math.PI / 18;
        scaleFactor = 0.1;

                
        // setup the outline of the pioneer 
        originalOutline = new GeneralPath();

                
        originalOutline.moveTo(-223, -15);
        originalOutline.lineTo(-217, -56);
        originalOutline.lineTo(-204, -93);
        originalOutline.lineTo(-183, -129);
        originalOutline.lineTo(-159, -158);
        originalOutline.lineTo(-151, -167);
        originalOutline.lineTo(-139, -171);
        originalOutline.lineTo(-53, -171);
        originalOutline.lineTo(-46, -173);
        originalOutline.lineTo(-31, -188);
        originalOutline.lineTo(-22, -194);
        originalOutline.lineTo(104, -194);
        originalOutline.lineTo(117, -189);
        originalOutline.lineTo(131, -181);
        originalOutline.lineTo(154, -161);
        originalOutline.lineTo(172, -141);
        originalOutline.lineTo(186, -123);
        originalOutline.lineTo(200, -97);
        originalOutline.lineTo(211, -72);
        originalOutline.lineTo(218, -43);
        originalOutline.lineTo(222, -14);
        originalOutline.lineTo(222, 13);
        originalOutline.lineTo(218, 42);
        originalOutline.lineTo(211, 71);
        originalOutline.lineTo(200, 96);
        originalOutline.lineTo(186, 122);
        originalOutline.lineTo(172, 140);
        originalOutline.lineTo(154, 160);
        originalOutline.lineTo(131, 180);
        originalOutline.lineTo(117, 188);
        originalOutline.lineTo(104, 193);
        originalOutline.lineTo(-22, 193);
        originalOutline.lineTo(-31, 187);
        originalOutline.lineTo(-46, 172);
        originalOutline.lineTo(-53, 170);
        originalOutline.lineTo(-139, 170);
        originalOutline.lineTo(-151, 166);
        originalOutline.lineTo(-159, 157);
        originalOutline.lineTo(-183, 128);
        originalOutline.lineTo(-204, 92);
        originalOutline.lineTo(-217, 55);
        originalOutline.lineTo(-223, 14);               
        }

    public Dimension getMinimumSize()
        {
        return panelSize;
        }

    public Dimension getPreferredSize()
        {
        return panelSize;
        }

    public void paintComponent(Graphics g)
        {
        int width = getWidth();
        int height = getHeight();
                
        // clear old values 
        g.setColor(Color.WHITE); 
        g.fillRect(0,0, width-1, height-1); 
                
        g.setColor(Color.BLUE);

        int minX1=0, minY1=0, minX2=0, minY2=0, minY3=0, minX3=0; 
        double minDist = 100000; 
                
        for (int i = 0; i < NUM_SONARS; i++)
            {
            double scaledSonar = sonars[i] * scaleFactor;
            double scaledX = x[i] * scaleFactor;
            double scaledY = y[i] * scaleFactor;
            int x1 = (int) (width / 2 + scaledX);
            int y1 = (int) (height / 2 - scaledY);
            int x2 = (int) (x1 + scaledSonar * Math.cos(angles[i] + spreadAngle));
            int y2 = (int) (y1 - scaledSonar * Math.sin(angles[i] + spreadAngle));
            int x3 = (int) (x1 + scaledSonar * Math.cos(angles[i] - spreadAngle));
            int y3 = (int) (y1 - scaledSonar * Math.sin(angles[i] - spreadAngle));

            g.drawLine(x1, y1, x2, y2);
            g.drawLine(x2, y2, x3, y3);
            g.drawLine(x3, y3, x1, y1);
            if (sonars[i] < minDist) { 
                minDist = sonars[i]; 
                minX1 = x1; minX2 = x2; minX3 = x3; 
                minY1 = y1; minY2 = y2; minY3 = y3; 
                }
            }
                
        g.fillPolygon(new int[]{minX1, minX2, minX3}, new int[]{minY1, minY2, minY3}, 3); 
                
        // draw some text 
        String str = String.format("Min dist: %.2f", minDist); 
        g.setColor(Color.BLACK); 
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20)); 
        g.drawString(str, 0, height-1);
                
        // draw the pioneer 
        g.setColor(Color.RED);
        Graphics2D g2d = (Graphics2D)g; 
        g2d.translate(width/2, height/2);
        g2d.rotate(-Math.PI/2); 
        g2d.scale(0.15, 0.15); 
        g2d.fill(originalOutline);
                
                
        g.dispose(); 
        }

    public void update(Graphics g)
        {
        paint(g);
        }

    private RobotListenerAdapter sonarListener = new RobotListenerAdapter()
        {
        public void updateGraphics(PioneerHorde horde)
            {
            PioneerAgent a = (PioneerAgent) horde.getTrainingAgent();
            synchronized (a.sonarLock) { 
                sonars  = a.robot.getSonars(sonars); 
                }
            minDist = a.closestObstacleDistance; 
            minAngle = a.closestObstacleAngle; 
            repaint();
            }
                
        public void stop() {} 
        };

    }
