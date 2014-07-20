package sim.app.horde.classifiers.knn_old;

/**
 * Class for tree with Manhattan distancing
 */

public class ManhattanDistance extends KDTree
    {
    private static final long serialVersionUID = 1L;

    public ManhattanDistance(int dimensions, Integer sizeLimit) {
        super(dimensions, sizeLimit);
        }

    protected double pointDist(double[] p1, double[] p2)
        {
        double d = 0;

        for (int i = 0; i < p1.length; i++)
            {
            double diff = (p1[i] - p2[i]);
            if (!Double.isNaN(diff))
                {
                d += (diff < 0) ? -diff : diff;
                }
            }

        return d;
        }

    protected double pointRegionDist(double[] point, double[] min, double[] max)
        {
        double d = 0;

        for (int i = 0; i < point.length; i++)
            {
            double diff = 0;
            if (point[i] > max[i])
                {
                diff = (point[i] - max[i]);
                }
            else if (point[i] < min[i])
                {
                diff = (min[i] - point[i]);
                }

            if (!Double.isNaN(diff))
                {
                d += diff;
                }
            }

        return d;
        }
    }