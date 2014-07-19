package sim.app.horde.behaviors;

public abstract class Flag extends Behavior
    {
    private static final long serialVersionUID = 1;

    int flag;
    public int getFlag() { return flag; }
    public Flag() { name = "Flag"; }
    }