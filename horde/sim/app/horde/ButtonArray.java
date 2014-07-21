package sim.app.horde;

import sim.app.horde.behaviors.*;
import sim.app.horde.classifiers.Domain;
import sim.app.horde.classifiers.Example;
import sim.app.horde.features.Feature;
import sim.app.horde.transitions.LearnedTransition;
import sim.util.gui.*;
import sim.display.*;
import sim.engine.Stoppable;
import sim.portrayal.*;

import javax.swing.table.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
   BUTTONARRAY
   
   <p>Maintains the array of buttons at the bottom of Horde, including the scroll view with the various
   behavior buttons installed.
   
   <p>BUTTONARRAY is a MACROOBSERVER so it knows when to change the hilight of various behavior
   buttons as the Macro behavior transitions from button to button.
*/

public class ButtonArray extends JPanel
    {
    public JButton makeLittleButton(String text)
        {
        JButton button = new JButton(text);
        button.putClientProperty( "JComponent.sizeVariant", "small" );  // or "mini" ?
        button.putClientProperty( "JButton.buttonType", "bevel" );  // or "mini" ?
        return button;
        }
    
    private static final long serialVersionUID = 1;
        
    // the currently highlighted button (duh)
    int hilightedButton = -1;
        
    // various widgets
    JScrollPane scrollpane;
    public ScrollableFlowPanel subarray = new ScrollableFlowPanel();
    JCheckBox training = new JCheckBox("Training", true);
    JButton pause = makeLittleButton("Pause / Unpause");
    JButton save = makeLittleButton("Save");
    JButton showModel = makeLittleButton("Show Model");
    JButton reset = makeLittleButton("Reset");
    JButton undo = makeLittleButton("Undo"); 
    JButton editExamples = makeLittleButton("Edit Examples"); 
    JButton log = makeLittleButton("Log");
    
    
    GUIState state;

    public ButtonArray(final GUIState state)
        {
        this.state = state;

        setLayout(new BorderLayout());

        Box box = new Box(BoxLayout.X_AXIS);
        box.add(training);
        ActionListener pauseListener = new ActionListener() { public void actionPerformed(ActionEvent e) { ((Console)(state.controller)).pressPause(); } };
        pause.addActionListener(pauseListener);
        pause.registerKeyboardAction(pauseListener, KeyStroke.getKeyStroke('\n'), JComponent.WHEN_IN_FOCUSED_WINDOW);
        box.add(pause);
        //box.add(save);
        //box.add(showModel);
        //box.add(training);
        //box.add(log);
        ActionListener logListener = new ActionListener() { public void actionPerformed(ActionEvent e) { synchronized(state.state.schedule) { doLog((Horde)(state.state)); }} };
        //log.addActionListener(logListener);
        ActionListener showModelListener = new ActionListener() { public void actionPerformed(ActionEvent e) { synchronized(state.state.schedule) { ((Horde)(state.state)).showClassifiers(); } } };
        //showModel.addActionListener(showModelListener);
        ActionListener saveListener = new ActionListener() { public void actionPerformed(ActionEvent e) { synchronized(state.state.schedule) { doSave(((Horde)(state.state))); }} };
        //save.addActionListener(saveListener);
        //box.add(reset);
        ActionListener resetListener = new ActionListener() { public void actionPerformed(ActionEvent e) { synchronized(state.state.schedule) { ((Horde)state.state).reset(); }}};
        //reset.addActionListener(resetListener);
        ActionListener undoListener = new ActionListener() { public void actionPerformed(ActionEvent e) { synchronized(state.state.schedule) { doUndo((Horde)state.state) ; }}};
        //undo.addActionListener(undoListener);
        //box.add(undo);
        ActionListener editExamplesListener = new ActionListener() { public void actionPerformed(ActionEvent e) { synchronized(state.state.schedule) { doEditExamples((Horde)state.state) ; }}};
        //editExamples.addActionListener(editExamplesListener);
        //box.add(editExamples);
        box.add(Box.createGlue());
        add(box, BorderLayout.NORTH);
        
        
    	JMenu menu = new JMenu("HiTAB");
    	JMenuItem m = null;
    	m = new JMenuItem("Pause");
    	m.addActionListener(pauseListener);
    	menu.add(m);
    	 m = new JMenuItem("Save");
    	m.addActionListener(saveListener);
    	menu.add(m);
    	 m = new JMenuItem("Show Model");
    	m.addActionListener(showModelListener);
    	menu.add(m);
    	 m = new JMenuItem("Reset");
    	m.addActionListener(resetListener);
    	menu.add(m);
    	 m = new JMenuItem("Undo");
    	m.addActionListener(undoListener);
    	menu.add(m);
    	 m = new JMenuItem("Edit Examples");
    	m.addActionListener(editExamplesListener);
    	menu.add(m);
    	 m = new JMenuItem("Log");
    	m.addActionListener(logListener);
    	menu.add(m);
        ((Console)(state.controller)).getJMenuBar().add(menu);

        scrollpane = new JScrollPane(subarray, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollpane, BorderLayout.CENTER);

        // add action listeners for the training button
        ActionListener trainingListener = new ActionListener()
            {
            public void actionPerformed(ActionEvent e)
                {
                synchronized (state.state.schedule)
                    {
                    ((Horde)(state.state)).setTraining(training.isSelected());
                    }
                }
            };
        training.addActionListener( trainingListener );

        ActionListener trainingListener2 = new ActionListener()  // this is the opposite because when the keystroke is pressed the button state hasn't changed
            {
            public void actionPerformed(ActionEvent e)
                {
                synchronized(state.state.schedule) { training.setSelected(!training.isSelected()); ((Horde)(state.state)).setTraining(training.isSelected()); }
                }
            };
        training.registerKeyboardAction(trainingListener2, KeyStroke.getKeyStroke('z'), JComponent.WHEN_IN_FOCUSED_WINDOW);


	KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() 
		{
		  public boolean dispatchKeyEvent(KeyEvent e) 
			{
			//System.out.println("Got key event " + e);
			return (processArrowKeyEvent(e));
			}
		});




        }

    /** called when user pressed "Edit Examples". Provides a JFrame allowing direct editing of
     *  ALL the examples.
     */

    class ExampleTableModel extends AbstractTableModel
        {
        private static final long serialVersionUID = 1L;

        ArrayList<String> columnNames = new ArrayList<String>();
        ArrayList<ArrayList<Object>> data = new ArrayList<ArrayList<Object>>();

        public int getColumnCount()
            {
            return columnNames.size();
            }

        public int getRowCount()
            {
            return data.size();
            }

        public String getColumnName(int idx)
            {
            return columnNames.get(idx);
            }

        public Class<?> getColumnClass(int c)
            {
            return getValueAt(0, c).getClass();
            }

        public boolean isCellEditable(int row, int col)
            {
            if (row > 0) 
                return true;
            return false;
            }

        public Object getValueAt(int row, int col)
            {
            if (col > columnNames.size()) 
                return new String("Col is too big!");

            if (row > data.size()) 
                return new String("Row is too big!");

            return data.get(row).get(col);
            }
            
        public void addColumnName(String str)
            {
            columnNames.add(str);
            }

        public void setValueAt(Object value, int row, int col)
            {
            data.get(row).add(col, value);
            }

        public void addExample(int index, Example e, Domain d, String previousClassification)
            {
            ArrayList<Object> row = new ArrayList<Object>();
            
            row.add(index);
            row.add(previousClassification);
            for (int i = 0; i < e.values.length; i++)
                row.add(new Double(e.values[i]));
            row.add("" + d.classes[e.classification]);
            row.add(e.continuation ? "true" : "false");

            data.add(row);
            }

        };

    public void doEditExamples(Horde horde)
        {
        ExampleTableModel etm = new ExampleTableModel();

        TrainableMacro tm = horde.getTrainingMacro();

        etm.addColumnName("#");
        etm.addColumnName("Original Behavior");
        Feature[] features = tm.features;
        for (int k = 0; k < features.length; k++)
            etm.addColumnName(features[k].toString());
        etm.addColumnName("New Behavior");
        etm.addColumnName("Continuation");

        int count = 0;
        for (int i = 0; i < (tm.isSingleState() ? 1 : tm.behaviors.length); i++)
            {
            LearnedTransition lt = ((LearnedTransition) tm.getTransition(i));
            if (lt == null) continue;
            
            ArrayList<Example> examples = lt.getExamples();
            for (int j = 0; j < examples.size(); j++)
                {
                etm.addExample(++count, examples.get(j), lt.getDomain(), tm.behaviors[i].getName());
                }
            }

        JTable table = new JTable(etm);

        // table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);
        JScrollPane scroller = new JScrollPane(table);

        // JScrollPane scroller = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        // scroller.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));

        JFrame frame = new JFrame();

        frame.setTitle("Example Database");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(scroller, BorderLayout.CENTER);
        frame.setResizable(true);
        frame.pack();

        frame.setVisible(true);
        }

    /** Called when the user pressed "Undo". We delete the last example from the example database.  */
    public void doUndo(Horde horde)
        {
        TrainableMacro tm = horde.getTrainingMacro();
        tm.removeLastExample();
        }

    /** Called when the user pressed "Log". We save out a log file of the examples and domain. */
    public void doLog(Horde horde)
        {
        FileDialog fd = new FileDialog((Console) (state.controller), "Save Log...", FileDialog.SAVE);
        fd.setFile("Put the log name here not the filename");
        fd.setVisible(true);
        if (fd.getFile() != null)
            horde.getTrainingMacro().logExamples(fd.getDirectory(), fd.getFile(), horde);
        }

    /** Called when the user pressed "Save". We save out the current behavior and its classifiers. */
    public void doSave(Horde horde)
        {
        // init the dialog panel
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());

        final JTextField saveField = new JTextField(20);
        //JLabel agentName = new JLabel("Agent: " + horde.getTrainingAgent().getName());
        JTextField keyField = new JTextField(2);
        Box b = new Box(BoxLayout.X_AXIS);

        JPanel panel = new JPanel();
//        panel.setLayout(new BorderLayout());
        panel.setBorder(new javax.swing.border.TitledBorder("Name"));
        //panel.add(agentName, BorderLayout.NORTH);
        panel.add(saveField);
        b.add(panel);

        JPanel panel2 = new JPanel();
        panel2.setBorder(new javax.swing.border.TitledBorder("Key"));
        panel2.add(keyField);
        b.add(panel2);

        b.add(Box.createGlue());
        p.add(b, BorderLayout.NORTH);

        String[] newNames = (String[])(Horde.initialParameterObjectNames.clone());
        LabelledList list = new LabelledList("Parameter Names");
        final JTextField[] fields = new JTextField[newNames.length];
        
        boolean[] targetsUsed = horde.getTrainingAgent().getBehavior().getTargetsUsed();

       	for(int i = 0; i < newNames.length; i++)
        	{
        	if (targetsUsed[i])
        		list.addLabelled("<html><b><font color=\""+SimHorde.parameterObjectColorName[i]+"\">" + newNames[i] + "</font></b>: ",  fields[i] = new JTextField(newNames[i]));
        	else
        		list.add(null, 
        						new JLabel("<html><b><font color=\""+SimHorde.parameterObjectColorName[i]+"\">" + newNames[i] + "</font></b>: "),
        						null, 
        						fields[i] = new JTextField(newNames[i]),
        						new JLabel(" [Unused]"));
        	}
        	
        
        p.add(list, BorderLayout.CENTER);

        if (JOptionPane.showConfirmDialog(null, p, "Save Behavior for Agent \"" + horde.getTrainingAgent().getName() + "\"", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;

        String name = saveField.getText().trim();
        if (name.equals(""))
            {
            JOptionPane.showMessageDialog(null, "The name you provided is empty.", "Cannot Save", JOptionPane.ERROR_MESSAGE);
            return;
            }
            
        for(int i = 0; i < newNames.length; i++)
        	newNames[i] = fields[i].getText();
        
        String[] s = horde.getTrainingAgent().getBehavior().getBehaviorNames();

        for (int i = 0; i < s.length; i++)
            {
            if (s[i].equalsIgnoreCase(name))
                {
                if (JOptionPane.showConfirmDialog(null, "The name you have chosen (" + name + ") already exists.  Do you wish to overwrite it?",
                        "Overwrite Existing Macro?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
                    return;
                else break;  // no more comparisons
                }
            }

        String keystring = "" + keyField.getText();
        char ch = 0;
        if (keystring.length() > 0) ch = keystring.charAt(0);

        // save it!!!
        try { horde.save(name, KeyStroke.getKeyStroke(ch), newNames); }
        catch (Exception e)
        	{
        	e.printStackTrace();
        	JOptionPane.showMessageDialog(null, e.getMessage(), "Cannot Save", JOptionPane.ERROR_MESSAGE);
        	}
        }


    /* Returns the given button in the ButtonArray. */
    JButton getButton(int val) { return (JButton) (((Box)(subarray.getComponent(val))).getComponent(0)); }

    /** Hilights the given button and de-hlights the others. Sets the current hilightedButton to this button. */
    public void hilightCurrentBehavior(int val)
        {
        try
            {
            if (hilightedButton >= 0) getButton(hilightedButton).setSelected(false);
            hilightedButton = -1;
            int i = subarray.getComponentCount();
            if (val < 0 || val >= i) return; // bah -- probably UNKNOWN_BEHAVIOR

            getButton(val).setSelected(true);
            } 
        catch (Exception e) { }
        hilightedButton = val;
        }

    /** Called when the window is closed. Presently does nothing. */
    public void quit()
        {
        }
        
        
    ArrayList buttons = new ArrayList();
    
    /** Called to change the behaviors to those in the given Macro. Updates all the buttons, 
        adds new menus and listeners to them, and updates the training button's listeners. */
    public void setBehaviors(final Macro m)
        {
        synchronized (state.state.schedule)
            {
            subarray.removeAll();
            buttons.clear();
            if (m != null)
                {
                for (int x = 0; x < m.behaviors.length; x++)
                    {
                    Box box = new Box(BoxLayout.X_AXIS);
                    final int y = x;
                    JButton button = new JButton((getKeyStrokeChar(m.behaviors[x].getKeyStroke()) == 0 ? "" : 
                            "" + getKeyStrokeChar(m.behaviors[x].getKeyStroke()) + " ") + m.behaviors[x].getButtonName());
                    buttons.add(button);
                    button.setFocusTraversalKeysEnabled(false);
                    ActionListener listener = new ActionListener()
                        {
                        public void actionPerformed(ActionEvent e)
                            {
                            synchronized(state.state.schedule)
                                {
                                Horde horde = (Horde) (state.state);
                                                                        
                                // If we're paused and the control button is pressed and this behavior is
                                // a trainable macro, show the model of that macro
                                if (((Console)(state.controller)).getPlayState() == Console.PS_PAUSED && 
                                    (e.getModifiers() & ActionEvent.CTRL_MASK) != 0 && 
                                    m.behaviors[y] instanceof TrainableMacro)
                                    {
                                    ((TrainableMacro)m.behaviors[y]).showClassifiers(horde);
                                    }
                                                                        
                                if (!training.isSelected())
                                    {
                                    training.setSelected(true);
                                    horde.setTraining(training.isSelected());
                                    }
                                horde.getTrainingMacro().userChangedBehavior((Horde)(state.state), y);
                                }
                            }
                        };

                    if (m.behaviors[x] instanceof Start) // Start is special!
                        {
                        listener = new ActionListener()
                            {
                            public void actionPerformed(ActionEvent e)
                                {
                                synchronized (state.state.schedule)
                                    {
                                    Horde horde = (Horde)(state.state);
                                    if (!training.isSelected())
                                        {
                                        training.setSelected(true);
                                        horde.setTraining(training.isSelected());
                                        }
                                    horde.getTrainingMacro().stop(horde.getTrainingAgent(), null, horde);
                                    horde.getTrainingMacro().start(horde.getTrainingAgent(), null, horde);
                                    }
                                }
                            };
                        }

                    // if we have a Macro, respond to a right mouse click by showing the Macro's Inspector
                    if (m.behaviors[x] instanceof Macro)
                        {
                        final int index = x;
                        MouseListener ml = new MouseListener()
                            {
                            public void mouseClicked(MouseEvent arg0)
                                {
                                if ((arg0.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK)
                                    {
                                    Inspector simpleInspector = new SimpleInspector(m.behaviors[index], state, null, state.getMaximumPropertiesForInspector());
                                    final Stoppable stopper = simpleInspector.reviseStopper(
                                        state.scheduleRepeatingImmediatelyAfter(simpleInspector.getUpdateSteppable()));
                                    state.controller.registerInspector(simpleInspector, stopper);
                                    JFrame frame = simpleInspector.createFrame(stopper);
                                    frame.setVisible(true);
                                    }
                                }

                            public void mouseEntered(MouseEvent arg0)
                                {
                                }

                            public void mouseExited(MouseEvent arg0)
                                {
                                }

                            public void mousePressed(MouseEvent arg0)
                                {
                                }

                            public void mouseReleased(MouseEvent arg0)
                                {
                                }
                            };

                        button.addMouseListener(ml);

                        }

                    button.addActionListener(listener);
                    button.setIcon(NumberTextField.I_BELLY);
                    button.setSelectedIcon(NumberTextField.I_BELLY_PRESSED);

                    // this is an obsolete method but I don't care -- ActionMap is a pain to figure out, this is much easier
                    if (m.behaviors[x].getKeyStroke() != null) 
                    	button.registerKeyboardAction(listener, m.behaviors[x].getKeyStroke(), JComponent.WHEN_IN_FOCUSED_WINDOW);
                    box.add(button);

                    if (m instanceof TrainableMacro) // it always is
                        {
                        TrainableMacro tm = (TrainableMacro) m;
                        // Horde horde = (Horde)(state.state);
                        final JPopupMenu menu = ((HordeWithUI) state).createTargetMenu(tm, tm.behaviors[x]);
                        if (menu != null) // need to add it
                            {
                            JToggleButton togglebutton = new JToggleButton(Display2D.LAYERS_ICON);
                            togglebutton.setPressedIcon(Display2D.LAYERS_ICON_P);
                            togglebutton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                            togglebutton.setBorderPainted(false);
                            togglebutton.setContentAreaFilled(false);
                            final JToggleButton b = togglebutton;

                            b.addActionListener(new ActionListener()
                                { public void actionPerformed(ActionEvent e) { menu.show(b, b.getWidth(), 0); } });
                            box.add(b);
                            }
                        }

                    subarray.add(box);
                    box.repaint();
                    }
                }
            // this seems to be necessary ON TOP of forcing a doLayout in ScrollableFlowPanel.getPreferredHeight()
            // (see comment there), else the button array won't show up until the user resizes the window.  :-(
            scrollpane.validate();
            }
        }

    /** Returns the key stroke character if there is one, else returns 0x0. 
        This is used to pretty-print up/down/left/right arrows on the buttons, that's all. */
    public static char getKeyStrokeChar(KeyStroke stroke)
        {
        if (stroke == null) return (char) 0;
        else if (stroke == Behavior.KS_UP) return '\u2191';
        else if (stroke == Behavior.KS_DOWN) return '\u2193';
        else if (stroke == Behavior.KS_LEFT) return '\u2190';
        else if (stroke == Behavior.KS_RIGHT) return '\u2192';
        else return stroke.getKeyChar();
        }
    
    public boolean processArrowKeyEvent(KeyEvent event)
    	{
    	char c = getKeyStrokeChar(KeyStroke.getKeyStrokeForEvent(event));
    	if (event.getID() == KeyEvent.KEY_PRESSED && 
    		(c == '\u2191' || c == '\u2193' || c == '\u2190' || c == '\u2192'))  // it's an arrow key, handle it specially
    		{
    		// any buttons use this character?
    		for(int i = 0; i < buttons.size(); i++)
    			{
    			JButton button = (JButton)(buttons.get(i));
    			if (button.getText().charAt(0) == c)  // got it
    				{ /*System.err.println("Clicking: " + button);*/ button.doClick(); return true; }
    			}
    		}
    	return false;
    	}
    }
