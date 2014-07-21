package sim.app.horde.classifiers.knn_old;

import java.util.Arrays;

/**
 * Class for tree with Weighted Manhattan distancing
 */
public class WeightedManhattanDistance extends KDTree
    {
    private static final long serialVersionUID = 1L;
    private double[] weights;

    public Object clone()
        {
        WeightedManhattanDistance wmd = (WeightedManhattanDistance)super.clone(); 
        wmd.weights = weights; 
        return wmd; 
        }
        
    public WeightedManhattanDistance(int dimensions, Integer sizeLimit) {
        super(dimensions, sizeLimit);
        this.weights = new double[dimensions];
        Arrays.fill(this.weights, 1.0);
        }

    public void setWeights(double[] weights)
        {
        this.weights = weights;
        }

    protected double getAxisWeightHint(int i)
        {
        return weights[i];
        }

    protected double pointDist(double[] p1, double[] p2)
        {
        double d = 0;

        for (int i = 0; i < p1.length; i++)
            {
            double diff = (p1[i] - p2[i]);
            if (!Double.isNaN(diff))
                {
                d += ((diff < 0) ? -diff : diff) * weights[i];
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
                d += diff * weights[i];
                }
            }

        return d;
        }
    }
