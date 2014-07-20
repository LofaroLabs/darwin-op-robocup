package sim.app.horde;

import java.awt.geom.*;
import java.awt.*;
import sim.util.*;
import sim.app.horde.agent.Agent;
import sim.app.horde.objects.*;

/**
   UTILITIES
        
   A set of classes for testing collisions and distances to closest points
   on shapes.
*/

public class Utilities
    {
    private static final long serialVersionUID = 1;
        
    /** Returns true if the agent has collided with something in the environment -- 
        another agent, an obstacle, or (if uncommented) the edge of the environment. */
    public static boolean collision(Horde horde, Double2D location, Agent agent)
        {

        // check for borders
        /*
        // commented out because we'll treat the environment as border-free
        double x = location.x;
        double y = location.y;

        if (x > horde.width || x < 0 || y > horde.height || y < 0)
        return true;
        */
        
        // check for obstacles
        Bag b = ((SimHorde)horde).obstacles.getAllObjects();
        final double maxDistSq = SimHorde.MAX_OBSTACLE_DIAMETER * SimHorde.MAX_OBSTACLE_DIAMETER;
        for (int i = 0; i < b.numObjs; i++)
            {
            Body body = (Body)(b.objs[i]);
            if (body.loc.distanceSq(location) < maxDistSq)
                {
                // need to check
                if (body.collision(location))
                    return true;
                }
            }

        if (SimHorde.ALLOW_COLLISIONS_WITH_AGENTS)
            {
            // check for other agents.  Right now we're requiring a little slack (0.05, less than the 0.1 distance for various operators)
            // -- otherwise stuff like wall following has the agents wandering around one another.
            b = ((SimHorde)horde).agents.getNeighborsExactlyWithinDistance(location, 0.05);
            if (b.size() == 0)
                return false;
            if (b.size() > 1)
                return true;
            if (b.objs[0] == agent)
                return false;
            }

        return false;
        }

    /** Returns the angle half-way between a and b.  This is actually somewhat nontrivial: it's not just (a+b)/2,
        because the resulting angle has to be "near" to a and b , rather than in the opposite direction.
        If a and b point in exactly opposite directions, then their mean is in the first direction in a clockwise
        sweep from a.  That's basically arbitrary.
    */
    public static double meanAngle(double a, double b)
        {
        a = normalizeAngle(a);
        b = normalizeAngle(b);
        
        double delta = Math.abs(a - b);
        if (delta > Math.PI)  // flip b
            b = (b - 2 * Math.PI);
        return normalizeAngle((a + b) / 2.0);
        }


    /** Moves angle to within [0, 2Pi) */
    // 34 bytes, so inlinable
    public static double normalizeAngle(double angle)
        {
        final double PI2 = 2 * Math.PI;
        if (angle >= 0 && angle < PI2) return angle;  // obvious thing first
        angle = angle % PI2;  // move to between [-2 * PI and 2 * PI)
        if (angle < 0) 
            angle += PI2;       // move to between [0 and 2 * PI)
        return angle;
        }

    /** Returns the relative angle of a target with respect to an agent at a current location and facing a current orientation */
    public static double relativeAngle(Double2D currentLocation, double currentOrientation, Double2D target)
        {
        // compute relative angle
        if (target == null)
            throw new RuntimeException("No target!!");
        Utilities.count[1]++;
        double rel_angle =  Utilities.fastAtan2((double)(target.y - currentLocation.y), (double)(target.x - currentLocation.x)) 
            // Math.atan2(target.y - currentLocation.y, target.x - currentLocation.x)
            - currentOrientation;

        // [[ cheaper normalization, saves an atan2 -- Sean]]
        return normalizeAngle(rel_angle);
        }



    /**
       Returns the point on the border of an arbitrary shape which is closest to
       the given point. Shapes are drawn with respect to an origin: shapeOrigin 
       gives the global position of the Shape's origin.  Circles are handled specially;
       other shapes are handled by performing a flattening path iteration on the shape
       boundary and testing closest points on each line segment in the flattened path
       iteration.  Obviously there will be some inconsisitency for rounded shapes.
    */
    public static Double2D closestPointShapeOrigin = null;
    public static Shape closestPointShape = null;
    public static Double2D closestPointPoint = null;
    public static Double2D closestPointResult = null;
    public static final double FLATNESS = 0.1;  // maybe make it even bigger?
    public static Double2D closestPoint(Double2D shapeOrigin, Shape shape, Double2D point)
        {
        // a little memoization
        if (shapeOrigin == closestPointShapeOrigin && shape == closestPointShape && point == closestPointPoint)
            return closestPointResult;
        
        // test for circles
        if (shape instanceof Ellipse2D)
            {
            Ellipse2D ellipse = (Ellipse2D) shape;
            double w = ellipse.getWidth();
            double h = ellipse.getHeight();
            if (w == h)
                return closestPointOnCircle(shapeOrigin.x + ellipse.getCenterX(), shapeOrigin.y + ellipse.getCenterY(), w / 2.0, point.x, point.y);
            }
                        
        FlatteningPathIterator p = new FlatteningPathIterator(shape.getPathIterator(null), FLATNESS);
        double pointx = point.x;
        double pointy = point.y;

        Double2D best = null;
        double bestdistsq = Double.POSITIVE_INFINITY;
        double[] coords = new double[6];
        double lastx = Double.NaN;  // should NEVER be used like this -- will be set with the first MOVETO
        double lasty = Double.NaN;
        double firstx = Double.NaN;  // used only for SEG_CLOSE
        double firsty = Double.NaN;
        while (!p.isDone())
            {
            int i = p.currentSegment(coords);
            double x = coords[0] + shapeOrigin.x; //FIXME Vittorio
            double y = coords[1] + shapeOrigin.y;//FIXME Vittorio
            if (firstx != firstx)  // it's NaN, so it's the first time
                { firstx = x; firsty = y; }
            if (i == PathIterator.SEG_LINETO)  // of interest to us
                {
                Double2D pos = closestPointOnLine(lastx, lasty, x, y, pointx, pointy);
                double distsq = point.distanceSq(pos);
                if (distsq <= bestdistsq)
                    {
                    best = pos;
                    bestdistsq = distsq;
                    }
                }
            else if (i == PathIterator.SEG_CLOSE)  // of interest to us, maybe we're finishing up.  Note that for SEG_CLOSE, x and y are nonsense.  We use lastx and lasty
                {
                Double2D pos = closestPointOnLine(lastx, lasty, firstx, firsty, pointx, pointy);
                double distsq = point.distanceSq(pos);
                if (distsq <= bestdistsq)
                    {
                    best = pos;
                    bestdistsq = distsq;
                    }
                // reset so we can start the next region of the shape
                x = firstx;
                y = firsty;
                firstx = Double.NaN;
                firsty = Double.NaN;
                }
            lastx = x;
            lasty = y;
            p.next();
            }

        // memoization
        closestPointShapeOrigin = shapeOrigin;
        closestPointShape = shape;
        closestPointPoint = point;
        closestPointResult = best;
        
        return best;
        }


    public static int[] count = new int[3];


    /**
       Returns the point on the border of a circle (centered at <x1,y1> and with a given radius) which is closest to
       the given point <x2, y2>
    */
         
    public static Double2D closestPointOnCircle(double x1, double y1, double radius, double x2, double y2)
        {
        // Let D be the distance between <x1, y1> and <x2, y2>.  Let R be the radius of the circle.
        // Let <x3, y3> be the point on the circle.
        // Then the ratio between D and R is the same as the ratio between (x2 - x1) and (x3 - x1).  Likewise for y.
        // So: (x2 - x1) / (x3 - x1) = d / r         
        // So: x3 = r / d * (x2 -x1) + x1
        // This should work for values of <x2, y2> inside the circle too.  If D = 0, we pick any random point.
        
        if (radius == 0) // its a point
            return new Double2D(x1, y1);
        if (x1 == x2 && y1 == y2)  // He's at the center of the circle, pick an arbitrary point
            return new Double2D(x1, y1 + radius);
        else
            {
            double dx = (x2 - x1);
            double dy = (y2 - y1);
            // double d = 1.0 / Math.sqrt(dx * dx + dy * dy);  // maybe a good candidate for fastInvSqrt?
            double d = fastInvSqrt(dx * dx + dy * dy);  // it turns out you get about a 20% improvement.  Let's do it
            double x3 = radius * d * dx + x1;
            double y3 = radius * d * dy + y1;
            return new Double2D(x3, y3);
            }
        }


    /**
       Returns the closest point on a line segment <x1, y1 <-> x2, y2> with respect to a third point <x3 y3>
    */
    public static Double2D closestPointOnLine(double x1, double y1, double x2, double y2, double x3, double y3)
        {
        Double2D p0 = new Double2D(x1, y1);

        if (x1==x2 && y1 == y2)  // zero length -- they're the same point anyway, so just return that point.  We do that now to avoid dividing by zero later.
            return p0;

        Double2D p1 = new Double2D(x2, y2);
        Double2D q = new Double2D(x3, y3);

        // http://www.exaflop.org/docs/cgafaq/cga1.html#Subject%201.02:%20How%20do%20I%20find%20the%20distance%20from%20a%20point%20to%20a%20line?
        //double L = Math.sqrt((p1.x - p0.x) * (p1.x - p0.x) + (p1.y - p0.y) * (p1.y - p0.y));
        //double r = (p0.y - q.y) * (p0.y - p1.y) - (p0.x - q.x) * (p1.x - p0.x);
        //r /= L * L;

        // Fixed by Sean to avoid unnecessary square root
        double LL = (p1.x - p0.x) * (p1.x - p0.x) + (p1.y - p0.y) * (p1.y - p0.y);
        double r = (p0.y - q.y) * (p0.y - p1.y) - (p0.x - q.x) * (p1.x - p0.x);
        r /= LL;


        //
        //               (Ay-Cy)(Ay-By)-(Ax-Cx)(Bx-Ax)
        //        r = -----------------------------
        //                        L^2
        Double2D qPrime = new Double2D(p0.x + r * (p1.x - p0.x), p0.y + r * (p1.y - p0.y));

        //      Px = Ax + r(Bx-Ax)
        //        Py = Ay + r(By-Ay)

        double distQP0 = qPrime.distanceSq(p0);  // distanceSq is monotonic with distance, so this should be fine.
        double distQP1 = qPrime.distanceSq(p1);
        double distP0P1 = p0.distanceSq(p1);
        if (distQP0 <= distP0P1 && distQP1 <= distP0P1) //qPrime is in the segment
            return qPrime;
        else if (distQP0 > distP0P1)
            return p1;
        else
            return p0;
        }

/** Fast approximation of 1.0 / sqrt(x).
 * See <a href="http://www.beyond3d.com/content/articles/8/">http://www.beyond3d.com/content/articles/8/</a>
 * @param x Positive value to estimate inverse of square root of
 * @return Approximately 1.0 / sqrt(x)
 **/
    public static double fastInvSqrt(double x)
        {
        double xhalf = 0.5 * x; 
        long i = Double.doubleToRawLongBits(x);
        i = 0x5FE6EB50C7B537AAL - (i>>1); 
        x = Double.longBitsToDouble(i);
        x = x * (1.5 - xhalf*x*x); 
        return x; 
        }


    static final double M_PI_4 = Math.PI / 4;
    static final double M_PI_2 = Math.PI / 2;
    static final double C = (1 + Math.sqrt(17.0)) / 8.0;

    // less accurate, 18x the speed
    static double fastAtan0(double x)
        {
        double a = Math.abs(x);
        return M_PI_4 * x - x * (a - 1) * (0.2447 + 0.0663 * a);
        }

    // pretty accurate, 13x the speed
    static double fastAtan1(double x)
        {
        double a = Math.abs(x);
        return (x < 0 ? -1 : 1) * M_PI_2 * (C * a + a * a + a * a * a) / (1 + ( C + 1 ) * a + ( C + 1 ) * a * a + a * a * a);
        }

    // See http://en.wikipedia.org/wiki/Atan2 for conversion to atan
    public static double fastAtan2(double y, double x)
        {
        if (x > 0)
            {
            return fastAtan1(y / x);
            }
        else if (x < 0)
            {
            if (y >= 0)
                return fastAtan1(y/x) + Math.PI;
            else
                return fastAtan1(y/x) - Math.PI;
            }
        else // x == 0
            {
            if (y > 0) 
                return Math.PI / 2;
            else if (y < 0) 
                return 0 - Math.PI / 2;
            else // (y==0)
                return 0;  // technically this is undefined but Java returns 0
            }
        }


    }
