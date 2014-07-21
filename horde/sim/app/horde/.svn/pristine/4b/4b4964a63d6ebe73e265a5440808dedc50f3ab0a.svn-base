package sim.app.horde.classifiers.knn;

import java.io.IOException;
import java.io.PrintWriter;

import sim.app.horde.classifiers.Classifier;
import sim.app.horde.classifiers.Domain;
import sim.app.horde.classifiers.Example;
import ec.util.MersenneTwisterFast;

public class KNN extends Classifier
    {

    private static final long serialVersionUID = 1L;

    /*// this class can be used when K > 1
      class Dist implements Comparable<Dist>{
                
      double dist;
      int index;

      public Dist(int index,double distance){
      this.index=index;
      this.dist = distance;
      }
      @Override
      public int compareTo(Dist x) {
      if(this.dist < x.dist)
      return -1;
      else
      return 1;
      }
                
      }*/
    int k;
    Example[] myExamples;

    double[] maxs, mins;
    int[] types;
    int defaultExample, numDefaultSamples;

    // Dist [] distances;

    /*      public KNN2 (){
            numContinuous = numCategorical = 0;
            maxContDist=0;
            //this.k=3;
            //this.defaultRange = defaultRange;
            this.k=1;
            defaultRange = 1.5;
            numDefaults=0;
            }*/

    public Object clone()
        {
        KNN knn = (KNN) super.clone();
        for (int i=0; i < myExamples.length; i++) 
            knn.myExamples[i] = myExamples[i]; 
        for (int i=0; i < types.length; i++) 
            knn.types[i] = types[i]; 
        for (int i=0; i < maxs.length; i++)
            knn.maxs[i] = maxs[i]; 
        for (int i=0; i < mins.length; i++)
            knn.mins[i] = mins[i]; 
        return knn;
        }

    public int classify(Example e, MersenneTwisterFast random)
        {
        //System.out.println("KNN IS CLASSIFYING");
        double closestDefaultDist = Double.POSITIVE_INFINITY, closestNonDefaultDist = Double.POSITIVE_INFINITY;
        int closestDefaultSample = -1, closestNonDefaultSample = -1;

        Example x = createModifiedExample(types.length, e);
        updateValueBounds(x);
        double[] scaledValues = scaleExample(x);
        for (int i = 0; i < myExamples.length; i++) {
            double dist = getEuclideanDist(scaledValues, scaleExample(myExamples[i]));
            if (myExamples[i].continuation && dist < closestDefaultDist) {
                closestDefaultDist = dist;
                closestDefaultSample = i;
                }

            if (!myExamples[i].continuation && dist < closestNonDefaultDist) {
                closestNonDefaultDist = dist;
                closestNonDefaultSample = i;
                }
            //System.out.println(myExamples[i].continuation);
            }

        //System.out.println(myExamples.length);

        if (numDefaultSamples == 0) // no Default Samples
            return myExamples[closestNonDefaultSample].classification;

        else {
            double[] hyperPlaneNormalVector = pointSubtract(myExamples[closestDefaultSample].values,
                myExamples[closestNonDefaultSample].values);
            double hyperPlaneConstant = dotProduct(hyperPlaneNormalVector, myExamples[closestNonDefaultSample].values);
            double pointValue = dotProduct(hyperPlaneNormalVector, e.values);

            if (pointValue > hyperPlaneConstant)
                return myExamples[closestNonDefaultSample].classification;
            else
                return myExamples[closestDefaultSample].classification;
            }
        }

    double dotProduct(double[] a, double[] b)
        {
        double result = 0;
        for (int i = 0; i < a.length; i++)
            result += a[i] * b[i];
        return result;
        }

    double[] pointSubtract(double[] a, double[] b)
        {
        double[] result = new double[a.length];
        for (int i = 0; i < a.length; i++)
            result[i] = b[i] - a[i];
        return result;
        }

    public Example createModifiedExample(int size, Example e)
        {

        // copy values from examples[] to the new myExamples [] with the extra dimensions
        // assumes that classes of a categorical data type will be coded as integers starting at 0

        Example x = new Example(new double[size]);
        x.classification = e.classification;
        x.continuation = e.continuation;

        for (int j = 0, count = 0; j < domain.type.length; j++) {
            if (domain.type[j] != Domain.TYPE_CATEGORICAL) {
                x.values[count] = e.values[j];
                count++;
                }
            else {
                x.values[count + (int) e.values[j]] = 1;
                count += domain.values[j].length;
                }
            }
        return x;
        }

    public void updateValueBounds(Example e)
        {
        // updates maximum and minimum values of each data field to be used in scaling
        for (int j = 0; j < e.values.length; j++) {
            maxs[j] = Math.max(maxs[j], e.values[j]);
            mins[j] = Math.min(mins[j], e.values[j]);
            }
        }

    public void updateValueTypes()
        {
        for (int i = 0, count = 0; i < domain.type.length; i++) {
            if (domain.type[i] != domain.TYPE_CATEGORICAL) {
                types[count] = domain.type[i];
                count++;
                }
            else {
                int j;
                for (j = 0; j < domain.values[i].length; j++)
                    types[count + j] = domain.TYPE_CATEGORICAL;
                count += j;
                }
            }
        }

    public double[] scaleExample(Example e)
        {
        double[] maxs = this.maxs;
        double[] mins = this.mins;
        double[] output = new double[e.values.length];
        for (int i = 0; i < output.length; i++)
            output[i] = (e.values[i] - mins[i]) / (maxs[i] - mins[i]);
        return output;
        }

    public void learn(Example[] examples, MersenneTwisterFast random)
        {

        myExamples = new Example[examples.length];
        // distances = new Dist[examples.length];

        // Calculate new number of dimensions which only increases if categorical types are present
        int myExampleNumValues = domain.type.length;
        for (int i = 0; i < domain.type.length; i++)
            if (domain.type[i] == Domain.TYPE_CATEGORICAL) myExampleNumValues += domain.values[i].length - 1;

        // create and initialize new max and min arrays
        maxs = new double[myExampleNumValues];
        mins = new double[myExampleNumValues];

        for (int i = 0; i < myExampleNumValues; i++) {
            maxs[i] = Double.NEGATIVE_INFINITY;
            mins[i] = Double.POSITIVE_INFINITY;
            }

        // create the types array
        types = new int[myExampleNumValues];
        updateValueTypes();

        // start creating own examples that will be used in comparison later.
        for (int i = 0; i < examples.length; i++) {

            myExamples[i] = createModifiedExample(myExampleNumValues, examples[i]);

            if (myExamples[i].continuation) {
                defaultExample = i;
                numDefaultSamples++;
                }
            updateValueBounds(myExamples[i]);
            }

        System.out.println("number of smaples: " + myExamples.length + " " + numDefaultSamples);
        }

    private double getEuclideanDist(double[] a, double[] b)
        {
        double dist = 0;
        double minDist;
        for (int i = 0; i < a.length; i++) {
            if ((domain.type[i] == Domain.TYPE_TOROIDAL)) {
                minDist = Math.min(Math.abs(a[i] - b[i]), 1 - Math.abs(a[i] - b[i])) / 0.5;
                dist += minDist * minDist;
                }
            else
                dist += (a[i] - b[i]) * (a[i] - b[i]);
            }
        return dist;
        }

    public void write(PrintWriter writer)
        {
        // TODO Auto-generated method stub

        }

    public void writeDotFile(String file, String label) throws IOException
        {
        // TODO Auto-generated method stub

        }

    public void writeClassifier(PrintWriter writer, boolean writeDomain)
        {
        // TODO Auto-generated method stub

        }

    public String toString()
        {
        // TODO Auto-generated method stub
        return null;
        }

    }
