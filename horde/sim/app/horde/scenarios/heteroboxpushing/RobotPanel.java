package sim.app.horde.scenarios.heteroboxpushing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import sim.app.horde.*;
import sim.app.horde.behaviors.*;
import sim.app.horde.features.Feature;
import sim.app.horde.scenarios.heteroboxpushing.pioneer.PioneerAgent;
import sim.app.horde.targets.Parameter;
import sim.app.horde.targets.Target;
import sim.app.horde.targets.Wrapper;
import sim.display.Display2D;
import sim.util.gui.NumberTextField;

import java.awt.*;
import java.util.*;

public class RobotPanel extends JPanel implements MacroObserver
    {
    private static final long serialVersionUID = 1L;
        
    BoxHitab horde;

    AddList addlist;
    JFrame addlistframe;

    private java.util.Timer timer = null;

        
    SensorRenderer sensorRenderer; 
    RobotThread robotThread; 
        
        
    private int hilightedButton = -1;

    ScrollableFlowPanel subarray = new ScrollableFlowPanel();
    final JCheckBox training = new JCheckBox("Training");

    private ArrayList<JCheckBoxMenuItem> robots = new ArrayList<JCheckBoxMenuItem>();

    /*ActionListener experimentListener = new ActionListener()
      {
      public void actionPerformed(ActionEvent e)
      {
      Object src = e.getSource();

      JCheckBoxMenuItem b = (JCheckBoxMenuItem) src;

      // don't do anything if the button is being unchecked
      if (!b.isSelected())
      return;

      for (int i = 0; i < robots.size(); i++)
      {
      if (robots.get(i) == src)
      {
      String address = robots.get(i).getName();
      for (int j = 0; j < horde.agents.size(); j++)
      {
      Robot a = horde.agents.get(j);
      if (a.ipAddress == address)
      {
      horde.trainingAgent = a;
      System.out.println("Training robot " + a.ipAddress);
      break;
      }
      }
      }
      else
      b.setSelected(false);

      }
      }
      };
    */
    JButton getButton(int val)
        {
        return (JButton) (((Box) (subarray.getComponent(val))).getComponent(0));
        }

    public RobotPanel(final JFrame mainWindow) {

        // start a Horde object
        horde = new BoxHitab();
        horde.started = true;
        horde.observer = this;

        setLayout(new BorderLayout());
        Box box = new Box(BoxLayout.X_AXIS);

        // make the addlist
        addlist = new AddList();
        JFrame addlistframe = new JFrame();
        addlistframe.getContentPane().setLayout(new BorderLayout());
        addlistframe.getContentPane().add(addlist, BorderLayout.CENTER);
        addlistframe.setSize(new Dimension(200, 400));
        addlistframe.setResizable(true);
        addlistframe.setTitle("Features");
        addlistframe.setVisible(true);

        buildAddList();

        final PioneerDropdownMenu robotMenu = new PioneerDropdownMenu("Robots", null);


        TimerTask task = new TimerTask()
            {
            public void run()
                {
                SwingUtilities.invokeLater(new Runnable()
                    {
                    public void run()
                        {
                        // pulse agents 
                        for (int i = 0; i < horde.agents.size(); i++)
                            horde.agents.get(i).pulse(horde);
                        }
                    });
                }
            };

        timer = new java.util.Timer("Horde Thread");
        timer.scheduleAtFixedRate(task, 0, 100);

        // have GUI display information from agent[0]
        //robotThread = new RobotThread(horde);
                
        // add a display of the line angle from the pioneer 
        //sensorRenderer = new SensorRenderer(robotThread); 
        //box.add(sensorRenderer, BorderLayout.EAST);

        // add standard buttons

        // create various buttons along the bottom
        final JButton showTreeButton = new JButton("Show trees");
        showTreeButton.setToolTipText("Show trees");
        showTreeButton.setFocusable(false);

        showTreeButton.addActionListener(new ActionListener()
            {
            public void actionPerformed(ActionEvent e)
                {
                horde.showClassifiers();
                }

            });

        final JButton logButton = new JButton("Log");
        logButton.setToolTipText("Log exemplars");
        logButton.setFocusable(false);
        logButton.addActionListener(new ActionListener()
            {
            public void actionPerformed(ActionEvent e)
                {

                FileDialog fd = new FileDialog(mainWindow, "Log Location", FileDialog.SAVE);
                fd.setVisible(true);
                if (fd.getFile() != null)
                    horde.getTrainingMacro().logExemplars(fd.getDirectory(), fd.getFile(), horde);

                }
            });

        final JButton saveTreesButton = new JButton("Save trees");
        saveTreesButton.setToolTipText("Save Trees");
        saveTreesButton.setFocusable(false);
        saveTreesButton.addActionListener(new ActionListener()
            {
            public void actionPerformed(ActionEvent e)
                {

                String fileName = (String) JOptionPane.showInputDialog(null, "Enter a filename for the behavior");

                if (fileName != null)
                    {
                    String[] s = horde.provideAllSavedMacroNames();

                    for (int i = 0; i < s.length; i++)
                        {
                        if (s[i].equalsIgnoreCase(fileName))
                            {
                            if (JOptionPane.showConfirmDialog(null, "The name you have chosen (" + fileName
                                    + ") already exists.  Do you wish to overwrite it?", "Overwrite Existing Macro?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
                                return;
                            else
                                break; // no more comparisons
                            }
                        }

                    // save it!!!
                    horde.save(fileName, KeyStroke.getKeyStroke('s'), horde.getTrainingLevel());
                    }

                }
            });

        training.addItemListener(new ItemListener()
            {
            public void itemStateChanged(ItemEvent evt)
                {
                if (training.isSelected())
                    {
                    System.out.println("TRAINING AGENT: " + horde.getTrainingAgent());
                    horde.getTrainingMacro().userChangedTraining(horde, true);
                    }
                else
                    {
                    System.out.println("DONE TRAINING");
                    horde.getTrainingMacro().userChangedTraining(horde, false);
                    horde.distributeAndRestartBehaviors();
                    }
                }
            });

        final JToggleButton motorButton = new JToggleButton("Motors");
        motorButton.addItemListener(new ItemListener()
            {
            public void itemStateChanged(ItemEvent evt)
                {
                if (motorButton.isSelected())
                    ((PioneerAgent) horde.getTrainingAgent()).robot.enable(false);
                else
                    ((PioneerAgent) horde.getTrainingAgent()).robot.enable(true);
                }
            });

        final JToggleButton graphicsButton = new JToggleButton("Graphics");
        graphicsButton.addItemListener(new ItemListener()
            {
            public void itemStateChanged(ItemEvent evt)
                {
                if (graphicsButton.isSelected())
                    robotThread.displayGraphics = true;
                else
                    robotThread.displayGraphics = false;

                }

            });

        robotMenu.removeAll();
        robots = new ArrayList<JCheckBoxMenuItem>();

        for (int i = 0; i < horde.agents.size(); i++)
            {
            Robot a = horde.agents.get(i);
            JCheckBoxMenuItem m = new JCheckBoxMenuItem(a.ipAddress);
            m.setName(a.ipAddress);
            //m.addActionListener(experimentListener);
            m.setFocusable(false);
            m.setToolTipText("Train: " + a.ipAddress);
            robotMenu.add(m);
            robots.add(m);
            }

        Box b = new Box(BoxLayout.X_AXIS);

        b.add(robotMenu.getButton());
        b.add(training);
        b.add(showTreeButton);
        b.add(logButton);
        b.add(saveTreesButton);
        b.add(motorButton);
        b.add(graphicsButton);

        b.add(Box.createGlue());
        add(b, BorderLayout.NORTH);

        // add buttons for each Macro
        setBehaviors(horde.getTrainingMacro());

        JScrollPane pane = new JScrollPane(subarray, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, box, pane);

        splitPane.setActionMap(null);

        splitPane.setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, null);
        splitPane.setInputMap(JComponent.WHEN_FOCUSED, null);
        splitPane.setInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, null);
        add(splitPane, BorderLayout.CENTER);

        }

    /**
     * Called to change the behaviors to those in the given Macro. Updates all the buttons, adds new menus and listeners to them, and updates the
     * training button's listeners.
     */
    public void setBehaviors(final Macro m)
        {
        if (m != null)
            {
            subarray.removeAll();

            for (int x = 0; x < m.behaviors.length; x++)
                {
                final int y = x;
                JButton button = new JButton(m.behaviors[x].getButtonName());
                ActionListener listener = new ActionListener()
                    {
                    public void actionPerformed(ActionEvent e)
                        {
                        if (!training.isSelected())
                            {
                            training.setSelected(true);
                            horde.setTraining(training.isSelected());
                            }

                        horde.getTrainingMacro().userChangedBehavior(horde, y);
                        }
                    };

                if (m.behaviors[x] instanceof Start) // Start is special!
                    {
                    listener = new ActionListener()
                        {
                        public void actionPerformed(ActionEvent e)
                            {
                            if (!training.isSelected())
                                {
                                training.setSelected(true);
                                horde.setTraining(training.isSelected());
                                }
                            horde.getTrainingMacro().stop(horde.getTrainingAgent(), null, horde);
                            horde.getTrainingMacro().start(horde.getTrainingAgent(), null, horde);

                            }
                        };
                    }

                button.addActionListener(listener);
                button.setIcon(NumberTextField.I_BELLY);
                button.setSelectedIcon(NumberTextField.I_BELLY_PRESSED);

                // this is an obsolete method but I don't care -- ActionMap
                // is a pain to figure out, this is much easier
                if (m.behaviors[x].getKeyStroke() != null)
                    button.registerKeyboardAction(listener, m.behaviors[x].getKeyStroke(), JComponent.WHEN_IN_FOCUSED_WINDOW);

                Box box = new Box(BoxLayout.X_AXIS);
                box.add(button);

                if (m instanceof TrainableMacro) // it always is
                    {
                    TrainableMacro tm = (TrainableMacro) m;
                    final JPopupMenu menu = createTargetMenu(tm, tm.behaviors[x]);
                    if (menu != null) // need to add it
                        {
                        JToggleButton togglebutton = new JToggleButton(Display2D.LAYERS_ICON);
                        togglebutton.setPressedIcon(Display2D.LAYERS_ICON_P);
                        togglebutton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                        togglebutton.setBorderPainted(false);
                        togglebutton.setContentAreaFilled(false);
                        final JToggleButton b = togglebutton;

                        b.addActionListener(new ActionListener()
                            {
                            public void actionPerformed(ActionEvent e)
                                {
                                menu.show(b, b.getWidth(), 0);
                                }
                            });
                        box.add(b);
                        }
                    }
                subarray.add(box);
                }
            }

        revalidate();

        }

    /**
     * Called when the agent's macro has decided to transition from FROM to TO. We then update the current hilighted behavior button as a result. A
     * method defined by MacroObserver.
     */
    public void transitioned(Macro macro, final int from, final int to)
        {

        if (macro == horde.getTrainingMacro()) // it's the relevant one, don't
            // listen to the others
            {
            if (from != to)
                {
                SwingUtilities.invokeLater(new Runnable()
                    {
                    public void run()
                        {
                        hilightCurrentBehavior(to);
                        }
                    });
                }
            }
        }

    /**
     * Hilights the given button and de-hlights the others. Sets the current hilightedButton to this button.
     */
    public void hilightCurrentBehavior(int val)
        {
        if (hilightedButton >= 0)
            {
            JButton b = getButton(hilightedButton);
            b.setBackground(new JButton().getBackground());
            b.setSelected(false);
            }
        hilightedButton = -1;
        int i = subarray.getComponentCount();
        if (val < 0 || val >= i)
            return; // bah -- probably UNKNOWN_BEHAVIOR

        JButton b = getButton(val);
        b.setBackground(Color.YELLOW);
        b.setSelected(true);

        hilightedButton = val;
        }

    public void start()
        {
        if (robotThread != null)
            robotThread.start();
        }

    public void stop()
        {
        if (robotThread != null)
            {
            try
                {
                System.out.println("[" + this.getClass().getName() + "] recieved stop signal.");
                robotThread.interrupt();
                robotThread.join();
                System.out.println("[" + this.getClass().getName() + "] stopped.");
                } catch (InterruptedException e)
                {
                System.err.println("Failed to stop robot thread: ");
                e.printStackTrace();
                }
            }

        for (int i = 0; i < horde.agents.size(); i++)
            horde.agents.get(i).stop();

        }

    private class PioneerDropdownMenu extends JPopupMenu implements ActionListener, PopupMenuListener
        {

        private static final long serialVersionUID = -1;
        JToggleButton button;

        public PioneerDropdownMenu(String s, Icon icon) {
            super(s);
            button = new JToggleButton(s, icon);
            button.addActionListener(this);
            button.setFocusable(false);
            addPopupMenuListener(this);
            setFocusable(false);
            }

        public JToggleButton getButton()
            {
            return button;
            }

        public void actionPerformed(ActionEvent e)
            {
            JToggleButton b = (JToggleButton) e.getSource();
            if (b.isSelected())
                show(b, 0, b.getHeight());
            else
                setVisible(false);
            }

        public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
            {
            SwingUtilities.invokeLater(new Runnable()
                {
                public void run()
                    {
                    button.doClick();
                    }
                });
            }

        public void popupMenuCanceled(PopupMenuEvent e)
            {
            }

        public void popupMenuWillBecomeVisible(PopupMenuEvent e)
            {
            }
        }

    public AddListCallback buildAddListElement(final Feature _feature)
        {
        return new AddListCallback()
            {
            Feature feature = null;
            Point loc = null;

            public void setComponentLocation(Point loc)
                {
                this.loc = loc;
                }

            public String toString()
                {
                return "" + _feature.getName();
                }

            public void unincludeElement(JComponent element)
                {
                if (feature != null)
                    horde.removeCurrentFeature(feature);
                else
                    throw new RuntimeException("Unknown feature removed.  This shouldn't happen");

                reset();
                }

            public JComponent copyElement()
                {
                feature = (Feature) (_feature.clone());
                final JButton b = new JButton(feature.getName());
                final JPopupMenu menu = createTargetMenu(horde.getTrainingMacro(), feature);
                b.addActionListener(new ActionListener()
                    {
                    public void actionPerformed(ActionEvent e)
                        {
                        if (menu != null)
                            menu.show(addlist, loc.x + b.getWidth(), loc.y);
                        }
                    });
                horde.addCurrentFeature(feature);

                reset();
                return b;
                }
            };
        }

    public JPopupMenu createTargetMenu(final TrainableMacro macro, final Targeting targeting)
        {
        final Target[] arguments = targeting.getTargets();
        final String[] argumentNames = targeting.getTargetNames();
        final Target[] targets = Target.provideAllTargets(macro);

        // return null if no arguments used
        boolean empty = true;
        for (int i = 0; i < arguments.length; i++)
            {
            if (arguments[i] != null)
                {
                empty = false;
                break;
                }
            }
        if (empty)
            return null;

        // build a menu
        JPopupMenu targetMenu = new JPopupMenu();
        for (int i = 0; i < arguments.length; i++)
            if (arguments[i] != null)
                {
                JMenu menu = new JMenu(argumentNames[i]);
                ButtonGroup group = new ButtonGroup();
                for (int j = 0; j < targets.length; j++)
                    {
                    final JRadioButtonMenuItem jmi = new JRadioButtonMenuItem("Assign to " + targets[j]);
                    group.add(jmi);

                    // we need to identify which target is currently being used.
                    if (arguments[i] instanceof Wrapper && targets[j] instanceof Parameter)
                        {
                        if (((Wrapper) (arguments[i])).isTargeting(macro, (Parameter) (targets[j])))
                            jmi.setSelected(true);
                        }
                    else
                        // next check if it's a base class. Let's compare classes
                        {
                        if (arguments[i].getClass() == targets[j].getClass())
                            jmi.setSelected(true);
                        }

                    final int _i = i;
                    final int _j = j;
                    // now add the action listener
                    jmi.addActionListener(new ActionListener()
                        {
                        public void actionPerformed(ActionEvent e)
                            {
                            jmi.setSelected(true);
                            System.err.println("TARGET " + targeting + " " + targeting.getTargetName(_i) + " SET TO...");

                            if (targets[_j] instanceof Parameter)
                                {
                                Wrapper w = new Wrapper(targeting.getTargetName(_i), ((Parameter) targets[_j]).getIndex());
                                System.err.println("Wrapper " + w);
                                targeting.setTarget(_i, w);
                                }
                            else
                                {
                                System.err.println("Ground " + targets[_j]);
                                targeting.setTarget(_i, targets[_j]);
                                }
                            reset();
                            }
                        });

                    menu.add(jmi);
                    }
                targetMenu.add(menu);
                }
        //else
        //      System.out.println("Parameter " + i + "(" + argumentNames[i] + ") not used");
        return targetMenu;
        }

    public void reset()
        {
        horde.resetBehavior();
        setBehaviors(horde.getTrainingMacro());
        }

    public void buildAddList()
        {
        // clear the currrent features
        horde.clearCurrentFeatures();

        // add the addable features
        final Feature[] f = Feature.provideAllFeatures(0);
        for (int i = 0; i < f.length; i++)
            addlist.addElement(buildAddListElement(f[i]));

        // add the default features and put them in the Horde as well
        Feature[] def = horde.defaultFeatures();
        for (int i = 0; i < def.length; i++)
            addlist.includeElement(buildAddListElement(def[i]));
        }
    }
