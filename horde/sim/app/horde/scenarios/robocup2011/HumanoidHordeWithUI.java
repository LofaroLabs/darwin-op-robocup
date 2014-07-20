package sim.app.horde.scenarios.robocup2011;

import sim.app.horde.*;
import sim.engine.*;
import sim.display.*;
import sim.portrayal.continuous.*;
import javax.swing.*;
import java.awt.*;
import sim.portrayal.simple.*;
import javax.swing.event.*;
import sim.portrayal.*;
import java.awt.event.*;
import sim.util.*;
import sim.field.continuous.*;
import java.awt.geom.*;
import sim.app.horde.behaviors.*;
import sim.app.horde.features.*;
import sim.app.horde.targets.*;
import sim.app.horde.objects.*;


public class HumanoidHordeWithUI extends GUIState implements ImplementsHordeUI
    {
    private static final long serialVersionUID = 1;

    public Display2D display;
    public JFrame displayFrame;
    public boolean mouseInWindow = false;
    
    int featureLevel = 0; 
    
    public static void main(String[] args)
        {
        new HumanoidHordeWithUI().createController();  // randomizes by currentTimeMillis
        }

    public Object getSimulationInspectedObject()
        {
        return state;
        }  // non-volatile
                
    public HumanoidHordeWithUI()
        {
        super(new HumanoidHorde(System.currentTimeMillis()));
        }

    public HumanoidHordeWithUI(SimState state)
        {
        super(state);
        }

    public static String getName()
        {
        return "The Horde";
        }

    public boolean addListBuilt = false;
        
    // called at various times to reset the behavior of the agent and reset the buttons to reflect
    // the new behaviors loaded into the agent.  Called by start() for example, and also by the "Reset" button
    public void reset()
        {
        ((Horde)state).resetBehavior(); // reset the behavior and reload it into the agent
        Macro macro = ((Horde) state).getTrainingMacro();
        buttons.setBehaviors(macro);
        }
        
    public void start()
        {
        super.start();

        setupPortrayals();
        ((Horde) state).observer = buttons;
                
        reset();
                
        if (addListBuilt) {  // level of permissible features changed 
            if (featureLevel != ((Horde)state).featureLevel) { 
                featureLevel = ((Horde)state).featureLevel; 
                addListBuilt = false; 
                }
            }
        
        // clear the addlist and rebuild it if it's not built yet.  At this point
        // the parameters were defined in the TrainableMacro
        if (!addListBuilt)
            { buildAddList(); addListBuilt = true; }
        }

    public void load(SimState state)
        {
        super.load(state);
        setupPortrayals();
        }

    public Inspector getInspector() { Inspector i = super.getInspector(); /* i.setVolatile(true); */ return i; }
    public void setupPortrayals()
        {
        final HumanoidHorde horde = (HumanoidHorde) state;

        // reschedule the displayer
        display.reset();
        display.setBackdrop(Color.white);

        // redraw the display
        display.repaint();
        }
                
    public ButtonArray buttons;
    // public JFrame buttonsFrame;
    public AddList addlist;
    public JFrame addlistframe;

    public void attachPortrayals()
        {
        }

    public void init(final Controller c)
        {
        super.init(c);
                
        // make the displayer
        display = new Display2D(800,800, this);
        display.setClipping(false);

        displayFrame = display.createFrame();
        displayFrame.setTitle("The Horde");
        c.registerFrame(displayFrame);   // register the frame so it appears in the "Display" list
        displayFrame.setVisible(true);
                
        displayFrame.remove(display);  // add it separately below

        // attach the portrayals
        attachPortrayals();

        // make the addlist
        addlist = new AddList();
        JFrame addlistframe = new JFrame();
        addlistframe.getContentPane().setLayout(new BorderLayout());
        addlistframe.getContentPane().add(addlist, BorderLayout.CENTER);
        addlistframe.setSize(new Dimension(200,400));
        addlistframe.setResizable(true);
        addlistframe.setTitle("Features");
        c.registerFrame(addlistframe);
        addlistframe.setVisible(false);
                
        // make the buttons
        buttons = new ButtonArray(this);
        //displayFrame.getContentPane().add(buttons, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
            display, buttons);
                        
        split.setActionMap(null);
                
        split.setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, null);
        split.setInputMap(JComponent.WHEN_FOCUSED, null);
        split.setInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, null);
                        
        split.setDividerLocation(5000);  // let the top guy push it
        displayFrame.getContentPane().add(split, BorderLayout.CENTER);

        /// tell the console to be slower
        ((Console) c).setPlaySleep(30);

        /// tell the console to require confirmation for stopping
        ((Console) c).setRequiresConfirmationToStop(true);
        }


    public void selectTarget(Component component, Point point, final Horde horde, Object obj)
        {
        if (!(obj instanceof Targetable)) return;
        final Targetable targetable = (Targetable)obj;
        JPopupMenu popup = new JPopupMenu("Parameter Target");
        for(int i = 0 ; i < Horde.initialParameterObjectName.length; i++)
            {
            final int _i = i;
            JMenuItem menu = new JMenuItem("<html><font color=\""+SimHorde.parameterObjectColorName[i]+"\">"+HumanoidHorde.initialParameterObjectName[i]);
            menu.addActionListener(new ActionListener()
                {
                public void actionPerformed(ActionEvent e) { ((SimHorde)horde).setParameterObject(_i, targetable); display.repaint(); }
                });
            popup.add(menu);
            }
        //Code added by Ian to add status settings to shift r-click menu
        for(int i = 0 ; i < 4; i++)
            {
            final int _i = i;
            JMenuItem menu = new JMenuItem("Set status = " + i);
            menu.addActionListener(new ActionListener()
                {
                public void actionPerformed(ActionEvent e) { targetable.setTargetStatus(null,null,_i); display.repaint();}
                });
            popup.add(menu);
            }
        //End code added by Ian
        popup.show(component, point.x, point.y);
                
                
        System.err.println("done init");
        }


    public void quit()
        {
        super.quit();

        if (displayFrame != null)
            displayFrame.dispose();
        displayFrame = null;
        display = null;
        }











    /***** Code for setting up the Behavior List ******/

    public JPopupMenu createTargetMenu(final TrainableMacro macro, final Targeting targeting)
        {
        final Target[] arguments = targeting.getTargets();                      // ground targets or wrappers from the behavior
        final String[] argumentNames = targeting.getTargetNames();
        final Target[] targets = Target.provideAllTargets(macro);                   // targets they are allowed to be (in wrapper form or ground)
        // return null if no arguments used
        boolean empty = true;
        for(int i = 0; i < arguments.length; i++) 
            if (arguments[i] != null) 
                {
                empty = false; 
                break; 
                }
        if (empty) return null;
                
        // build a menu
        JPopupMenu targetMenu = new JPopupMenu();
        for(int i = 0; i < arguments.length; i++)
            if (arguments[i] != null)
                {
                JMenu menu = new JMenu(argumentNames[i]);
                ButtonGroup group = new ButtonGroup();
                for(int j = 0; j < targets.length; j++)
                    {
                    final JRadioButtonMenuItem jmi = new JRadioButtonMenuItem("Assign to " + targets[j]);
                    group.add(jmi);
                                        
                    // we need to identify which target is currently being used.
                    if (arguments[i] instanceof Wrapper && targets[j] instanceof Parameter )
                        {
                        if (((Wrapper)(arguments[i])).isTargeting(macro, (Parameter)(targets[j])))
                            jmi.setSelected(true);
                        }
                    else    // next check if it's a base class.  Let's compare classes
                        {
                        if (arguments[i].getClass() == targets[j].getClass())  // it's the one!!!
                            jmi.setSelected(true);
                        }
                                        
                    final int _i = i;
                    final int _j = j;
                    // now add the action listener
                    jmi.addActionListener(new ActionListener()
                        {
                        public void actionPerformed(ActionEvent e)
                            {
                            synchronized(state.schedule)
                                {
                                jmi.setSelected(true);
                                System.err.println("TARGET " + targeting + " " + targeting.getTargetName(_i) +  " SET TO...");
                                if (targets[_j] instanceof Parameter)
                                    {
                                    Wrapper w = new Wrapper(targeting.getTargetName(_i), ((Parameter)targets[_j]).getIndex());
                                    System.err.println("Wrapper " + w);
                                    targeting.setTarget(_i, w);  // make a wrapper for the Parameter
                                    }
                                else
                                    {
                                    System.err.println("Ground " + targets[_j]);
                                    targeting.setTarget(_i, targets[_j]);  // it's just a ground target, don't make a wrapper for it
                                    }
                                }
                            reset();
                            }
                        });
                                        
                    menu.add(jmi);
                    }
                targetMenu.add(menu);
                }
            else System.out.println("Parameter " + i + "("+argumentNames[i]+") not used");
        return targetMenu;
        }



    public AddListCallback buildAddListElement(final Feature _feature)
        {
        return new AddListCallback()
            {
            Feature feature = null;
            Point loc = null;
                                
            public void setComponentLocation(Point loc) { this.loc = loc; }
            public String toString() { return "" + _feature.getName(); }
            public void unincludeElement(JComponent element)
                {
                Horde horde = (Horde)state;
                if (feature!=null) horde.removeCurrentFeature(feature);
                else throw new RuntimeException("Unknown feature removed.  This shouldn't happen");
                                
                reset();
                }
            public JComponent copyElement()
                {
                Horde horde = (Horde)state;
                feature = (Feature)(_feature.clone());
                final JButton b = new JButton(feature.getName());
                final JPopupMenu menu = createTargetMenu(horde.getTrainingMacro(), feature);
                b.addActionListener(new ActionListener() 
                    { public void actionPerformed(ActionEvent e) { if (menu!= null) menu.show(addlist, loc.x + b.getWidth(), loc.y); } });
                horde.addCurrentFeature(feature);
                                
                reset();
                return b;
                }
            };
        }


    public void buildAddList()
        {
        // clear the currrent features
        ((Horde)state).clearCurrentFeatures();
                        
        addlist.clear(); 
        
        // add the addable features
        final Feature[] f = Feature.provideAllFeatures(featureLevel);
        for(int i = 0; i < f.length; i++)
            addlist.addElement(buildAddListElement(f[i]));
                
        // add the default features and put them in the Horde as well
        Feature[] def = ((Horde)state).defaultFeatures();
        for(int i = 0; i < def.length; i++)
            addlist.includeElement(buildAddListElement(def[i]));
        }
    }
