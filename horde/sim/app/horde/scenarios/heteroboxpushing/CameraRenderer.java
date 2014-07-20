package sim.app.horde.scenarios.heteroboxpushing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.*;

import sim.app.horde.scenarios.heteroboxpushing.features.pioneer.BlobData;
import sim.app.horde.scenarios.heteroboxpushing.pioneer.PioneerAgent;

public class CameraRenderer extends JPanel
    {
    private static final long serialVersionUID = 1L;

    Dimension panelSize = new Dimension(640, 480);
    BlobData blobData;

    public CameraRenderer(RobotThread robotThread) {
        setSize(panelSize);
        setBackground(Color.BLACK);
        robotThread.addListener(cameraListener);
        }

    public Dimension getMinimumSize()
        {
        return panelSize;
        }

    public Dimension getPreferredSize()
        {
        return panelSize;
        }

    private Color[] blobColors = { Color.PINK, Color.GREEN, Color.ORANGE, Color.CYAN };

    public void paintComponent(Graphics g)
        {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);

        if (blobData != null)
            {

            for (int i = 0; i < BlobData.NUM_COLORS; i++)
                {
                if (blobData.xMax[i] > 0)
                    {
                    g.setColor(blobColors[i]);
                    g.fillRect(blobData.xMin[i], blobData.yMin[i], blobData.xMax[i] - blobData.xMin[i],
                        blobData.yMax[i] - blobData.yMin[i]);
                    }
                }
            }

        g.dispose();
        }

    public void update(Graphics g)
        {
        paint(g);
        }

    private RobotListener cameraListener = new RobotListener()
        {
        public void updateGraphics(BoxHitab horde)
            {
            try
                {
                PioneerAgent a = (PioneerAgent) horde.getTrainingAgent();
                blobData = a.camera.getBlobData(); 
   
                repaint();
                } catch (Exception e)
                {
                System.err.println("Failed to read camera data from training robot!!!");
                e.printStackTrace();
                }
            }

        public void stop()
            {
            }
        };

    }
