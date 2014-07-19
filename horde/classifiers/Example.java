package sim.app.horde.classifiers;

import java.text.DecimalFormat;
import java.io.*;
import java.util.*;


/**
   An example is composed of a feature vector and corresponding classification.
   The example may include a continuation, which indicates that the classification
   should continue when the feature vector remains unchanged.
   
   Examples may be loaded from a file for a specified domain
   
   @author vittorio
*/
public class Example implements java.io.Serializable, Cloneable
    { 
    private static final long serialVersionUID = 1;
    public static final DecimalFormat format = new DecimalFormat(); 
    static { format.setMaximumFractionDigits(2);  format.setMaximumIntegerDigits(0); }

    public boolean continuation;  // maintain the current classification
    public double[] values;       // The feature values
    public int classification;    // Behavior index

    public double score=1; 

    public Object clone()
        {
        try 
            {
            Example f = (Example)(super.clone());
                        
            // copy values
            f.values = new double[values.length];
            for(int i = 0 ; i < f.values.length; i++)
                f.values[i] = values[i];

            return f;
            }
        catch (CloneNotSupportedException e) { return null; /* never happens */ }
        }

    public Example(double[] val)
        {
        values = val;
        classification = -1;
        }

    public Example(double[] val, int classification)
        {
        values = val;
        this.classification = classification;
        }

    public int hashCode()
        {
        return Arrays.hashCode(values)^classification;
        }
    
    /**
     * this is a parse friendly toString()
     * re-indented by khaled.
     */
    public String toString()
        {
        String str = (continuation ? "+ " : "- ") ;
        for(int i = 0 ; i < values.length ; i++)
            str += values[i] + " ";
        str += classification;
        return str ;
        }

    public String toString(Domain d)
        {
        String result="VALUES: {";
        for(int i=0;i<values.length;i++)
            {
            if (d.type[i] == Domain.TYPE_CATEGORICAL)
                result = result + d.attributes[i] + "=" + d.values[i][(int)(values[i])]+" ";
            else
                result = result + d.attributes[i] + "=" + format.format(values[i])+" ";
            }
        result+= "} CLASSIFICATION: "+d.classes[classification];
        return result;
        }
 
    public static Example[] loadExamples(String aFileName, Domain dom) throws FileNotFoundException
        {
        ArrayList<Example> result = new ArrayList<Example>();
        File file = new File(aFileName);
        Scanner scanner = new Scanner(file);
        String line = null;
        int count = 0;
        do
            {
            line = scanner.nextLine().trim();
            if(line.length() == 0 || line.startsWith("#")) continue;
            count++;
            Scanner lineScanner = new Scanner(line);
            lineScanner.useDelimiter("(\\s)+|((\\s)*[,](\\s)*)");
            double[] val = new double[dom.attributes.length];
            // grab the + or -
            boolean cont = false;
            String continuation = lineScanner.next().trim();
            if (continuation.equals("+")) cont = true;
            else if (continuation.equals("-")) cont = false;
            else 
                {
                System.err.println("Assuming no continuation:    " + line);
                // reset line scanner in this case
                lineScanner = new Scanner(line);
                lineScanner.useDelimiter("(\\s)+|((\\s)*[,](\\s)*)");
                }

            for (int i = 0; i < val.length; i++)
                {
                String attr = lineScanner.next().trim();
                if (dom.type[i] == Domain.TYPE_CATEGORICAL)
                    val[i] = dom.getValueIndex(i, attr);
                else // TYPE_CONTINUOUS and TYPE_TOROIDAL
                    val[i] = Double.parseDouble(attr);
                }
            
            String cls = lineScanner.nextLine().trim();
            if (cls.startsWith(",")) cls = cls.substring(1).trim();
            Example e = new Example(val, dom.getClassIndex(cls));
            e.continuation = cont;
            result.add(e);
            } 
        while (scanner.hasNextLine());
                
        Object[] vals = result.toArray();
        Example[] ret = new Example[vals.length];
        System.arraycopy(vals, 0, ret, 0, vals.length); //FIXME check!
        System.err.println("Read " + count + " examples");
        return ret;
        }
    
    
    public boolean equals(Object o)
        {
        if (!(o instanceof Example))
            return false; 
        Example e = (Example) o; 
        if (e.continuation != continuation || e.classification != classification || e.values.length != values.length)
            return false ;
        
        boolean equalValues = true; 

        for (int i=0 ; i < values.length; i++) 
            {
            if (e.values[i] != values[i])
                {
                equalValues =false; 
                break; 
                }
            }
        
        return equalValues; 
        }
    
    
    /** Returns true if all the examples have identical value arrays.  If the examples array is empty, throws an exception. */
    public static boolean allSameValues(Example[] examples)
        {
        if (examples.length == 0)
            throw new RuntimeException("Empty examples set!");
        double last_value;
        for(int val=0; val<examples[0].values.length;val++)
            {
            last_value = examples[0].values[val];
            for (int idx = 1; idx < examples.length; idx++)
                if( last_value != examples[idx].values[val])
                    return false;
            }
        return true;
        }

    /** Returns true if the examples have identical classes.  If the examples array is empty, throws an exception. */
    public static boolean allSameClassification(Example[] examples)
        {
        if (examples.length == 0)
            throw new RuntimeException("Empty examples set!");
        int classification = examples[0].classification;
        for (int idx = 1; idx < examples.length; idx++)
            {
            if (examples[idx].classification != classification)
                return false;
            }
        return true;
        }
    
    
    }

