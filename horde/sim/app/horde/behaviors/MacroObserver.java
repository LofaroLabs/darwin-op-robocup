package sim.app.horde.behaviors;

public interface MacroObserver
    {
    public void transitioned(Macro macro, int from, int to);
    public void trainingAgentChanged();
    public void resetting();
    }
