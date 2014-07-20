package sim.app.horde.scenarios.heteroboxpushing;

import java.awt.*;
import java.awt.geom.GeneralPath;

public class RobotOutline
    {

    GeneralPath originalOutline;

    public RobotOutline() {
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

    public void draw(int x0, int y0, Graphics g)
        {
        g.setColor(Color.RED);
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(x0, y0);
        g2d.rotate(-Math.PI / 2);
        g2d.scale(0.15, 0.15);
        g2d.fill(originalOutline);
        }

    }
