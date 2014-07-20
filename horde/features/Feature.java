package sim.app.horde.features;

import sim.app.horde.*;
import sim.app.horde.classifiers.Domain;
import sim.app.horde.agent.*;
import sim.app.horde.behaviors.*;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.*;

/**
 * FEATURE
 * 
 * <p>
 * A feature returns a value describing a feature of the environment. Many
 * features are with respect to TARGETS. For example, a feature might be
 * "color of(target)", enabling parameterization.
 * 
 * <p>
 * The current value of a feature is returned by getValue(...). Features also
 * have names which are returned by getName().
 * 
 * <p>
 * There are three kinds of features: CONTINUOUS, CATEGORICAL, and TOROIDAL
 * features. The abstract FEATURE class is CONTINUOUS. Other features are
 * handled by CATEGORICALFEATURE and TOROIDAL FEATURE.
 * 
 * <p>
 * One feature is special: Done. This feature indicates that the FSA has
 * signaled its "done" flag to state that it believes it has completed its task.
 */

public abstract class Feature extends Targeting
    {
    private static final long serialVersionUID = 1;
    public static Class<?>[] basicFeatureClasses;
    public static String[] basicFeatureParams;

    String name;

    public Feature(String name)
        {
        this.name = name;
        }

    public void start(Agent agent, Macro parent, Horde horde)
        {
        super.startTargets(agent, parent, horde);
        }

    public void stop(Agent agent, Macro parent, Horde horde)
        {
        super.stopTargets(agent, parent, horde);
        }

    /**
     * The current value of the Feature. The default implementation always
     * returns 0. Override this.
     */
    public double getValue(Agent agent, Macro parent, Horde horde)
        {
        return 0;
        }

    /** The name of the Feature. */
    public String getName()
        {
        return name;
        }

    /** Useful for throwing errors in functions which otherwise must be inlined. */
    protected String throwError(double value)
        {
        throw new IndexOutOfBoundsException("Illegal Value for Feature " + toString() + " ("
            + value + ")");
        }

    public String toString()
        {
        String s = name;
        if (targetNames != null && targetNames.length > 0)
            {
            s += "[" + targetNames[0] + "->" + targets[0];
            for (int i = 1; i < targetNames.length; i++)
                s += "," + targetNames[i] + "->" + targets[i];
            s += "]";
            }
        return s;
        }

    /**
     * Constructs a Domain object from the given features and behaviors suitable
     * to hand to the classifier construction code. In short: the features are
     * the attributes of the domain are the various features, the
     * classes/categories of the domain are the various behaviors, the types of
     * the attributes are based on the types of the various features, and the
     * attribute values for categorical features are those features' category
     * names.
     */
    public static Domain buildDomain(String name, Feature[] features, Behavior[] behaviors)
        {
        String[] attributes = new String[features.length];
        int[] types = new int[features.length];
        String[][] values = new String[features.length][];
        String[] classes = new String[behaviors.length];

        for (int i = 0; i < features.length; i++)
            {
            attributes[i] = features[i].toString();

            // the ordering here matters
            types[i] = (features[i] instanceof CategoricalFeature ? Domain.TYPE_CATEGORICAL
                : (features[i] instanceof ToroidalFeature ? Domain.TYPE_TOROIDAL
                    : Domain.TYPE_CONTINUOUS));

            if (types[i] == Domain.TYPE_CATEGORICAL)
                values[i] = ((CategoricalFeature) (features[i])).getCategoryNames();
            }

        for (int i = 0; i < behaviors.length; i++)
            classes[i] = behaviors[i].toString();

        return new Domain(name, attributes, values, classes, types); // new
        // MersenneTwisterFast());
        }

    /** Returns an array of brand-new instances of all known Feature classes. */
    public static Feature[] provideAllFeatures(Agent agent)
        {
        ArrayList<Feature> list = new ArrayList<Feature>();
        for (int i = 0; i < basicFeatureClasses.length; i++)
            {
            try
                {
                Class featureClass = basicFeatureClasses[i];

                Class cArg = String.class; 
                                
                if (agent.hasType(Targeting.getType(featureClass)))
                    {
                    if (basicFeatureParams[i] == null || basicFeatureParams[i].length() == 0)
                        list.add((Feature) featureClass.newInstance());
                    else
                        {
                        String [] params = basicFeatureParams[i].split("\\s+");
                                                
                        Constructor constructor = featureClass.getConstructor(new Class[] { String[].class });
                        list.add((Feature) constructor.newInstance(new Object[] {params}));
                        }
                    }
                } catch (Exception e)
                {
                throw new RuntimeException("Can't Provide Feature: " + basicFeatureClasses[i], e);
                }
            }

        Feature[] f = new Feature[list.size()];
        for (int i = 0; i < f.length; i++)
            f[i] = (Feature) (list.get(i));
        return f;
        }

    /** Loads all known Feature classes from the "basic.features" file. */
    static
        {
        ArrayList<Class<?>> list = new ArrayList<Class<?>>();
        List<String> plist = new ArrayList<String>();
        Scanner scanner = new Scanner(Horde.getStreamRelativeToClass(Horde.locationRelativeClass, Horde.BASIC_FEATURES_LOCATION));
        while (scanner.hasNextLine())
            {
            String s = scanner.nextLine().trim();
            if (s.startsWith("#") || s.length() == 0) continue;
            try
                {
                Scanner vscan = new Scanner(s);
                String cn = vscan.next();
                list.add(Class.forName(cn, true, Thread.currentThread().getContextClassLoader()));
                if (vscan.hasNext())
                    plist.add(vscan.nextLine().trim());
                else
                    plist.add(null);
                } catch (ClassNotFoundException e)
                {
                System.err.println("Couldn't find Feature: " + s);
                }
            }
        scanner.close();
        basicFeatureClasses = new Class[list.size()];
        basicFeatureParams = new String[plist.size()];
        System.arraycopy(list.toArray(), 0, basicFeatureClasses, 0, list.size());
        System.arraycopy(plist.toArray(), 0, basicFeatureParams, 0, plist.size());
        }

    public String writeToString()
        {
        String s = " ( feature " + name + " " + getType(getClass()) + " " + targets.length + " ";
        for (int i = 0; i < targets.length; i++)
            s += (targetNames[i] + " ");
        s += ") ";
        return s;
        }

    public void write(PrintWriter writer)
        {
        writer.print(writeToString());
        }
    }
