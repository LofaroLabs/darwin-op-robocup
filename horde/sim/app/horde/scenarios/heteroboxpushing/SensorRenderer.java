package sim.app.horde.scenarios.heteroboxpushing;

import java.awt.*;
import javax.swing.*;
import sim.app.horde.scenarios.heteroboxpushing.*;
import sim.app.horde.scenarios.heteroboxpushing.pioneer.PioneerAgent;

public class SensorRenderer extends JPanel
    {
    private static final long serialVersionUID = 1L;

    Dimension panelSize = new Dimension(400, 480);

    RobotOutline robotOutline = new RobotOutline();

    final int NUM_BEAMS = 241;

    boolean useLog = false;
    double lasers[] = new double[NUM_BEAMS];

    public final int NUM_SONARS = 16;

    double angles[] = new double[NUM_SONARS];
    int x[] = new int[NUM_SONARS];
    int y[] = new int[NUM_SONARS];
    double spreadAngle, scaleFactor;

    double sonars[] = new double[NUM_SONARS];

    public SensorRenderer(RobotThread robotThread) 
        {
        setSize(panelSize);
        setBackground(Color.WHITE);
        robotThread.addListener(sensorListener);

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
                y[i] = (int) (-180 + 180 * Math.sin(angles[i]));
            }
        spreadAngle = Math.PI / 18;
        scaleFactor = 0.1;
        }

    public Dimension getMinimumSize()
        {
        return panelSize;
        }

    public Dimension getPreferredSize()
        {
        return panelSize;
        }

    public void paint(Graphics g)
        {
        int width = getWidth();
        int height = getHeight();
        int x0 = width / 2;
        int y0 = height / 2;

        // draw the robot 
        robotOutline.draw(x0, y0, g);

        // draw sonar cones 
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
            }

        // draw the laser beams 
        for (int i = -120; i <= 120; i++)
            {
            double range = useLog ? Math.log(4000) : 4000;
            int x1 = (int) (x0 - scaleFactor * range * Math.sin(Math.toRadians(i)));
            int y1 = (int) (y0 - scaleFactor * range * Math.cos(Math.toRadians(i)));
            g.setColor(Color.BLUE);

            range = lasers[i];
                        
            if (range < 20)
                g.setColor(Color.RED);
            else
                g.setColor(Color.GREEN); // green = go = good data
                        
            range = useLog ? Math.log(range) : range;
            x1 = (int) (x0 - scaleFactor * range * Math.sin(Math.toRadians(i)));
            y1 = (int) (y0 - scaleFactor * range * Math.cos(Math.toRadians(i)));
            g.drawLine(x0, y0, x1, y1);
            }

        g.dispose();
        }

    public void update(Graphics g)
        {
        paint(g);
        }

    private RobotListener sensorListener = new RobotListener()
        {
        public void updateGraphics(BoxHitab horde)
            {
            PioneerAgent a = (PioneerAgent) horde.getTrainingAgent();

            sonars = a.sonar.getSonarValues();
            //lasers = a.laser.getLaserValues();

            repaint();
            }

        public void stop()
            {
            }
        };

    }
