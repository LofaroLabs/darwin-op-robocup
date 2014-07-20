package sim.app.horde.classifiers.knn_old;

import java.util.Arrays;

/**
 * Class for tree with Weighted Squared Euclidean distancing
 */
public class WeightedSqEuclidianDistance extends KDTree
    {
    private static final long serialVersionUID = 1L;
    private double[] weights;

    public Object clone()
        {
        WeightedSqEuclidianDistance wmd = (WeightedSqEuclidianDistance)super.clone(); 
        wmd.weights = weights; 
        return wmd; 
        }
        
    public WeightedSqEuclidianDistance(int dimensions, Integer sizeLimit) {
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
            double diff = (p1[i] - p2[i]) * weights[i];
            if (!Double.isNaN(diff))
                {
                d += diff * diff;
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
                diff = (point[i] - max[i]) * weights[i];
                }
            else if (point[i] < min[i])
                {
                diff = (point[i] - min[i]) * weights[i];
                }

            if (!Double.isNaN(diff))
                {
                d += diff * diff;
                }
            }

        return d;
        }
    }
