package sim.app.horde.features;


/**  CATEGORICAL FEATURE

     <p>A feature whose value is an integer chosen from a possible set of integers 1...n,
     each representing a possible "category" value.  For example, while a continuous feature
     might have values from 1.0 to 100.0 representing, say, temperature, a categorical
     feature might have the values "red", "blue", and "bob", represented by the arbitrary
     integers 0, 1, and 2 respectively.  Categories are not ordered with respect to one another.
        
     <p>Categorical features have an array of category names indexed by the category number.

**/


public abstract class CategoricalFeature extends Feature
    {
    private static final long serialVersionUID = 1;
    String[] categories;
        
    public Object clone()
        {
        CategoricalFeature f = (CategoricalFeature)(super.clone());
        // copy categories, don't clone, they're immuable
        f.categories = new String[categories.length];
        for(int i = 0 ; i < f.categories.length; i++)
            f.categories[i] = categories[i];
        return f;
        }
                
    public CategoricalFeature(String name, String[] categories)
        { super(name); this.categories = categories; }

    public int getNumCategories() { return categories.length; }
    public String[] getCategoryNames() { return categories; }
    }
