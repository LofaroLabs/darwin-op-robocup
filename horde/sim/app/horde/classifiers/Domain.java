package sim.app.horde.classifiers;

import ec.util.MersenneTwisterFast;
import java.io.*;
import java.util.*;
import java.util.ArrayList;

/**
 *
 * @author vittorio
 */
public class Domain implements java.io.Serializable, Cloneable
    {
    private static final long serialVersionUID = 1;
    public static final int TYPE_CATEGORICAL = 0;
    public static final int TYPE_CONTINUOUS = 1;
    public static final int TYPE_TOROIDAL = 2;
    public int[] type;
    public String[] attributes;
    public String[][] values;
    public String[] classes;
    public String name;                 // only auxillary for testing, stores file name of domain
        
    public Object clone()
        {
        try 
            {
            Domain f = (Domain)(super.clone());
                        
            // copy types
            f.type = new int[type.length];
            for(int i = 0 ; i < f.type.length; i++)
                f.type[i] = type[i];
                                
            // copy attibutes
            f.attributes = new String[attributes.length];
            for(int i = 0 ; i < f.attributes.length; i++)
                f.attributes[i] = attributes[i];

            // copy values
            f.values = new String[values.length][];
            for(int i = 0 ; i < f.values.length; i++)
                {
                if (values[i] == null) { f.values[i] = null; continue; }  // no array for some reason
                f.values[i] = new String[values[i].length];
                for(int j = 0 ; j < f.values[i].length; j++)
                    f.values[i][j] = values[i][j];
                }

            // copy classes
            f.classes = new String[classes.length];
            for(int i = 0 ; i < f.classes.length; i++)
                f.classes[i] = classes[i];

            return f;
            }
        catch (CloneNotSupportedException e) { return null; /* never happens */ }
        }
        
    /** We had to put the random number generator SOMEWHERE */
    //private final MersenneTwisterFast randomOld = null;

    public Domain(String name, String[] a, String[][] v, String[] c, int[] type /*, MersenneTwisterFast random */)
        {
        this.name = name;
        attributes = a;
        values = v;
        classes = c;
        this.type = type;
        // this.random = random;
        }

    public static Domain loadDomain(String aFileName, MersenneTwisterFast random)
        {
        ArrayList<String> parsed_classes = new ArrayList<String>();
        ArrayList<String> parsed_attributes = new ArrayList<String>();
        ArrayList<Integer> parsed_types = new ArrayList<Integer>();
        ArrayList<ArrayList<String>> parsed_values = new ArrayList<ArrayList<String>>();

        try
            {
            File file = new File(aFileName);
            Scanner scanner = new Scanner(file);
            String line = null;

            final int DONE = 0;
            final int CLASS = 1;
            final int CATEGORICAL = 2;
            final int ATTRIBUTES = CATEGORICAL;
            final int CONTINUOUS = 3;
            final int TOROIDAL = 4;


            int state = DONE;

            for (int lineNumber = 0; scanner.hasNextLine(); lineNumber++)
                {
                line = scanner.nextLine().trim();
                Scanner lineScanner = new Scanner(line);

                // System.err.println("---> " + line);
                if (line.length() == 0 || line.startsWith("#"))
                    continue;
                else if (line.startsWith("}"))
                    {
                    if (state != DONE)
                        state = DONE;
                    else
                        throw new RuntimeException("Parse Error in line " + lineNumber + " of " + aFileName +
                            "\n No Section to Close");
                    continue;
                    }
                else if (state == DONE &&
                    line.endsWith("{"))
                    {
                    String val = lineScanner.next().trim().toUpperCase();

                    if (val.equals("CLASSES"))
                        state = CLASS;
                    else if (val.equals("CATEGORICAL"))
                        state = CATEGORICAL;
                    else if (val.equals("ATTRIBUTES"))
                        state = ATTRIBUTES;
                    else if (val.equals("CONTINUOUS"))
                        state = CONTINUOUS;
                    else if (val.equals("TOROIDAL"))
                        state = TOROIDAL;
                    else
                        throw new RuntimeException("Parse Error in line " + lineNumber + " of " + aFileName +
                            "\n Invalid Section Declaration " + val);
                    continue;
                    }
                else if (state == CLASS)
                    {
                    System.err.println("Added Class: " + line); 
                    parsed_classes.add(line); 
                    continue;
                    }
                else if (state == CATEGORICAL)  // or state == ATTRIBUTES
                    {
                    // first read category -- there must be one or we would have had a blank line
                    String val = lineScanner.next().trim();
                    System.err.print("Added Categorical Attribute " + val + " Values: ");
                    parsed_attributes.add(val);
                    parsed_types.add(TYPE_CATEGORICAL);
                    ArrayList<String> current_values = new ArrayList<String>();
                    parsed_values.add(current_values);
                    if (!lineScanner.hasNext()) // no values at all
                        throw new RuntimeException("Parse Error in line " + lineNumber + " of " + aFileName +
                            "\n Categorical Attribute has no values " + val);
                    while (lineScanner.hasNext())
                        {
                        String next = lineScanner.next().trim();
                        System.err.print("[" + next + "] ");
                        current_values.add(next);
                        }
                    System.err.println();  // close out line
                    continue;
                    }
                else if (state == CONTINUOUS)
                    {
                    // these while loops assume that each feature is a single word, 
                    // and there are multiple features per line.  However, actually, 
                    // features can have spaces in their names, and each feature 
                    // is saved per line.  
                    /* while (lineScanner.hasNext())
                       {
                       String val = lineScanner.next().trim();
                       System.err.println("Added Continuous Attribute " + val);
                       parsed_attributes.add(val);
                       parsed_types.add(TYPE_CONTINUOUS);
                       parsed_values.add(new ArrayList<String>()); 
                       }
                    */
                    System.err.println("Added Continuous Attribute " + line);
                    parsed_attributes.add(line);
                    parsed_types.add(TYPE_CONTINUOUS);
                    parsed_values.add(new ArrayList<String>()); 
                    continue;
                    }
                else if (state == TOROIDAL)
                    {
                    /* while (lineScanner.hasNext())
                       {
                       String val = lineScanner.next().trim();
                       System.err.println("Added Toroidal Attribute " + val);
                       parsed_attributes.add(val);
                       parsed_types.add(TYPE_TOROIDAL);
                       parsed_values.add(new ArrayList<String>());
                       }
                    */
                    System.err.println("Added Toroidal Attribute " + line);
                    parsed_attributes.add(line);
                    parsed_types.add(TYPE_TOROIDAL);
                    parsed_values.add(new ArrayList<String>());
                    continue;
                    }
                else
                    throw new RuntimeException("Parse Error in line " + lineNumber + " of " + aFileName +
                        "\n No idea what this is " + line);
                }
            scanner.close();
            }
        catch (FileNotFoundException e)
            {
            throw new RuntimeException(e);  // make unchecked
            }

        int[] types = new int[parsed_types.size()];
        int i = 0;
        for (Integer s : parsed_types)
            {
            types[i++] = s;
            }
        String[] attributes = new String[parsed_attributes.size()];
        i = 0;
        for (String s : parsed_attributes)
            {
            System.out.println("ADDING ATTRIBUTE="+s);
            attributes[i++] = s;
            }
        String[] classes = new String[parsed_classes.size()];
        i = 0;
        for (String s : parsed_classes)
            {
            classes[i++] = s;
            }
        String[][] values = new String[parsed_values.size()][];
        int a = 0, v = 0;
        for (ArrayList<String> attr : parsed_values)
            {
            v = 0;
            values[a] = new String[attr.size()];
            for (String s : attr)
                {               
                values[a][v] = s;                
                v++;
                }
            a++;
            }
        return new Domain(aFileName.substring(0, aFileName.length() - 3), attributes, values, classes, types /*, random */);
        }

    public String toString()
        {
        String result = "CLASSES {\n";
        for (String c : classes)
            {
            result += "\n" + c;
            }
        result += "\n } \n";
        //ArrayList<String> categorical = new ArrayList<String>();
        //ArrayList<String[]> categorical_values = new ArrayList<String[]>();
        //ArrayList<String> continuous = new ArrayList<String>();
        //ArrayList<String> toroidal = new ArrayList<String>();
        for (int t = 0; t < type.length; t++)
            {
            switch (type[t])
                {
                case TYPE_CATEGORICAL:
                    //          categorical.add(attributes[t]);
                    //          categorical_values.add(values[t]);
                    result = addCategorical(result, attributes[t], values[t]);
                    break;
                case TYPE_CONTINUOUS:
                    //continuous.add(attributes[t]);
                    result = addContinuous(result, attributes[t]);
                    break;
                case TYPE_TOROIDAL:
                    //toroidal.add(attributes[t]);
                    result = addToroidal(result, attributes[t]);
                    break;
                default:
                    throw new RuntimeException("Unknown attribute type: " + type[t]);
                }
            }
    
        return result;
        }

    private String addToroidal(String result, String toroidal)
        {
        // if (toroidal.size() != 0)
        {
        result += "TOROIDAL {\n";
        //      for (String a : toroidal)
                {
                result += toroidal + "\n";
                }
        result += "\n } \n";
        }
        return result;
        }

    String addCategorical(String in, String categorical, String[] categorical_values)
        {
        String result = in;
        //if (categorical.size() != 0)
                {
                result += "CATEGORICAL {\n";
                //      for (int i = 0; i < categorical.size(); i++)
                        {
                        //        result += categorical.get(i);
                        result+=categorical;
                        for (String s : categorical_values)
                            {
                            result += " " + s;
                            }
                        result += "\n";
                        }

                result += "\n } \n";
                }
        return result;
        }

    private String addContinuous(String result, String continuous)
        {
        //if (continuous.size() != 0)
        {
        result += "CONTINUOUS {\n";
        result+=continuous;
        //      for (String a : continuous)
        //        {
        //        result += a + "\n";
        //        }
        result += "\n } \n";
        }
        return result;
        }

    public int getClassIndex(String cla)
        {
        for (int i = 0; i < classes.length; i++)
            {
            if (cla.equals(classes[i]))
                return i;
            }
        throw new RuntimeException("Class " + cla + " does not exist!");
        }

    public int getAttributeIndex(String att)
        {
        for (int i = 0; i < attributes.length; i++)
            {
            if (att.equals(attributes[i]))
                return i;
            }
        throw new RuntimeException("Attribute " + att + " does not exist!");
        }

    int getValueIndex(int a, String val)
        {       
        for (int i = 0; i < values[a].length; i++)
            {
            if (val.equals(values[a][i]))
                return i;
            }
        // assume it's a number
        try{ 
            double d = Double.parseDouble(val);
            int i = (int) d;
            if (i == d && i >= 0 && i < values[a].length)  // it's valid
                return i;
            }
        catch (Exception e) { }
        throw new UnsupportedOperationException("Value " + val + " does not exist!");
        }
                
                
    public void write(PrintWriter writer)
        {
        writer.print("( domain ");
        writer.print("\n\t( classes ");
        for(int c = 0; c < classes.length; c++)
            writer.print(classes[c] + " " );
        writer.print(") ");
        for(int i = 0 ; i < type.length; i++)
            {
            if (type[i] == TYPE_CATEGORICAL)
                {
                writer.print("\n\t( categorical ");
                writer.print(attributes[i] + " ");
                writer.print("" + values[i].length + " ");
                for(int j = 0; j < values[i].length; j++)
                    writer.print(values[i][j] + " " );
                writer.print(") ");
                }
            else if (type[i] == TYPE_CONTINUOUS)
                {
                writer.print("\n\t( continuous ");
                writer.print(attributes[i] + " ");
                writer.print(") ");
                }
            else if (type[i] == TYPE_TOROIDAL)
                {
                writer.print("\n\t( toroidal ");
                writer.print(attributes[i] + " ");
                writer.print(") ");
                }
            }
        writer.print(")\n");
        }
                

    void readClasses(Scanner scanner)
        {
        ArrayList<String> _classes = new ArrayList<String>();
        //String paren = token(scanner, "(");
        token(scanner, "(");
        String type = token(scanner);
        if (type.equals("classes"))
            {
            // grab classes
            while(true)
                {
                String tok = token(scanner);
                if (tok.equals(")")) break;
                _classes.add(tok);
                }
            }
        else generateTokenError(scanner, "classes");

        // convert arraylists
        classes = getStrings(_classes);
        }



    void readAttributes(Scanner scanner)
        {
        ArrayList<Integer> _types = new ArrayList<Integer>();
        ArrayList<String> _attributes = new ArrayList<String>();
        ArrayList<String[]> _values = new ArrayList<String[]>();
                
        while(true)
            {
            String paren = token(scanner);
            if (paren.equals(")")) break;
            if (!paren.equals("(")) generateTokenError(scanner, ")", "(");
            String type = token(scanner);
            if (type.equals("categorical"))
                {
                _types.add(new Integer(TYPE_CATEGORICAL));
                // grab name
                _attributes.add(token(scanner));
                // grab values
                ArrayList<String> v = new ArrayList<String>();
                while(true)
                    {
                    String tok = token(scanner);
                    if (tok.equals(")")) break;
                    v.add(tok);
                    }
                _values.add(getStrings(v));
                }
            else if (type.equals("continuous"))
                {
                _types.add(new Integer(TYPE_CONTINUOUS));
                _attributes.add(token(scanner));
                token(scanner, ")");
                }
            else if (type.equals("toroidal"))
                {
                _types.add(new Integer(TYPE_TOROIDAL));
                _attributes.add(token(scanner));
                token(scanner, ")");
                }
            else generateTokenError(scanner, new String[] {"categorical", "continuous", "toroidal"});
            }
                        
        // convert arraylists
        type = getInts(_types);
        attributes = getStrings(_attributes);
        values = getStringArrays(_values);
        }
                
    public Domain(Scanner scanner)
        {
        token(scanner, "(");
        //String name = token(scanner, "domain");
        token(scanner, "domain");
        readClasses(scanner);
        readAttributes(scanner);
        // no final ")"
        }
                
    protected  static String token(Scanner scanner) { return token(scanner, null); }
    protected  static String token(Scanner scanner, String expect)
        {
        if (scanner.hasNext())
            {
            String token = scanner.next();
            if (expect == null || token.equals(expect))
                {
                return token;
                }
            else generateTokenError(scanner, expect );
            }
        else throw new RuntimeException("Out of tokens for " + scanner);
        return null;  // never happens
        }
                
    protected  static void generateTokenError(Scanner scanner, String expect) { generateTokenError(scanner, new String[] { expect }); }
    protected  static void generateTokenError(Scanner scanner, String expect1, String expect2) { generateTokenError(scanner, new String[] { expect1,  expect2 }); }
    protected  static void generateTokenError(Scanner scanner, String[] expect)
        {
        String token = scanner.match().group();  // I think this is what we want
        String s = new String("Mismatched token, got " + token + " but expected any of\n" );
        for(int i = 0; i < expect.length; i++)
            s += "\t" + expect[i] + "\n";
        s += "for scanner: " + scanner + "\njust before: ";
        try
            {
            for(int i = 0; i < 3; i++)
                s = s + scanner.nextLine();
            }
        catch (Exception e) { }
        throw new RuntimeException(s);
        }
                
    protected int[] getInts(ArrayList<Integer> a)
        {
        int[] ints = new int[a.size()];
        for(int i = 0; i < ints.length; i++)
            ints[i] = ((Integer)(a.get(i))).intValue();
        return ints;
        }
        
    protected String[] getStrings(ArrayList<String> a)
        {
        String[] strings = new String[a.size()];
        for(int i = 0; i < strings.length; i++)
            strings[i] = (String)(a.get(i));
        return strings;
        }

    protected String[][] getStringArrays(ArrayList<String[]> a)
        {
        String[][] strings = new String[a.size()][];
        for(int i = 0; i < strings.length; i++)
            strings[i] = (String[])(a.get(i));
        return strings;
        }

    }
