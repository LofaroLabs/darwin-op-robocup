package sim.app.horde.scenarios.humanoid.console;

import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import java.util.*;

import edu.gmu.robocup.*;
import sim.app.horde.scenarios.humanoid.*; 
import sim.app.horde.behaviors.Behavior;
import sim.app.horde.behaviors.TrainableMacro;
import sim.app.horde.features.*;

public class HordePanel extends SRVPanel {
  
    private static final long serialVersionUID = 1L;
    private HumanoidHorde horde = null; 
    private java.util.Timer timer = null; 

    private boolean doHorde; 
        
    public static boolean currentlyRunning = false; 
    public static int experimentIdx; 
        
    private ArrayList<HordeMenuItem> experiments = new ArrayList<HordeMenuItem>(); 

    public HordePanel(String hostName, int remotePort, int localPort,
        String protocol, String archiveDir, String archivePrefix) {     

        super(hostName, remotePort, localPort, protocol, archiveDir, archivePrefix); 
        doHorde = false; 
        horde = null; 

        // comm and archiver
        srvComm = new HordeNetworkReader(hostName, remotePort, localPort, protocol);

        srvComm.addFrameListener(new SRVListenerAdapter() {
            public void srvNewMessage(final byte[] bytes) {
                String s = new String(bytes).trim(); 
                if ( doHorde) { 
                    if (s.startsWith("Starting Horde") || s.startsWith("Stopping Horde") || s.contains("TRAINING")) 
                        logWriteLine(s); 
                    }
                else 
                    logWriteLine(s); 
                }
            }); 
                
        iconDirectory = HordePanel.class.getResource("").getPath() + "/../icons"; 
        init(archiveDir, archivePrefix);

        final HordeDropdownMenu experimentMenu = new HordeDropdownMenu("Experiments", new ImageIcon(
                iconDirectory + "/auto.png"));          
                
                
        final JCheckBox experimentBox = new JCheckBox("Experiments"); 
        experimentBox.addItemListener(new ItemListener()
            {
            public void itemStateChanged (ItemEvent evt) { 
                if (experimentBox.isSelected()) 
                    currentlyRunning = true; 
                else 
                    currentlyRunning = false; 
                }
            }); 
                
        final JCheckBox hordeBox = new JCheckBox("Horde"); 
        hordeBox.addItemListener(new ItemListener()
            {
            TimerTask task = null; 
            public void itemStateChanged(ItemEvent evt)
                {                               
                if (hordeBox.isSelected() && task == null) { 

                    doHorde = true; 

                    horde = new HumanoidHorde((HordeNetworkReader) srvComm);
                    horde.started = true; 

                    Feature[] f = Feature.provideAllFeatures(0);

                    for (int i=0; i < f.length; i++ )
                        horde.addCurrentFeature(f[i]);
                    horde.resetBehavior();                                  
                        
                    experimentMenu.removeAll(); 
                    experiments = new ArrayList<HordeMenuItem>(); 
                    System.out.println("Loaded behaviors"); 
                    Behavior[] b = TrainableMacro.provideAllTrainableMacros();
                    for (int i=0; i < b.length; i++) { 
                        HordeMenuItem m = new HordeMenuItem(b[i]);
                        m.addActionListener(experimentListener);
                        experimentMenu.add(m); 
                        experiments.add(m); 
                        }
                                        

                    task = new TimerTask() {
                        public void run() {
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    ((HumanoidAgent)horde.getTrainingAgent()).pulse(horde);
                                    }
                                });
                            }
                        };

                    timer = new java.util.Timer("Horde Thread");
                    timer.scheduleAtFixedRate(task, 0, 100);
                    }
                else if (task!=null)  { 
                    task.cancel(); 
                    timer.cancel();
                    timer = null; 
                    task = null;
                    horde.started = false; 
                    horde = null; 
                    doHorde = false;        
                    srvComm.clearCommandQueue(); 
                    } 
                }
            });
                
        final JButton showTreeButton = new JButton("Show trees");
        showTreeButton.setToolTipText("Show trees");
        showTreeButton.setFocusable(false);
                
        showTreeButton.addActionListener(new ActionListener()
            { 
            public void actionPerformed(ActionEvent e)
                {
                horde.showClassifiers(); 
                horde.getTrainingMacro().logExemplars("/home/ksulliv/workspace/MASON/bin/sim/app/horde/scenarios/humanoid/",
                    "examples.txt", horde);
                }
                
            }); 
                
        final JTextField fileNameTreeField = new JTextField(20);
        fileNameTreeField.setText("someBehavior");
            
        final JButton saveTreeButton = new JButton("Save tree");
        saveTreeButton.setToolTipText("Save tree");
        saveTreeButton.setFocusable(false);
                
        saveTreeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = fileNameTreeField.getText().trim();
                System.out.println("Filename: " + name);
                if (name.equals("")) {
                    JOptionPane.showMessageDialog(null,
                        "The name you provided is empty.", "Cannot Save",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                    }

                String[] s = horde.provideAllSavedMacroNames();

                for (int i = 0; i < s.length; i++) {
                    if (s[i].equalsIgnoreCase(name)) {
                        if (JOptionPane
                            .showConfirmDialog(
                                null,
                                "The name you have chosen ("
                                + name
                                + ") already exists.  Do you wish to overwrite it?",
                                "Overwrite Existing Macro?",
                                JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
                            return;
                        else
                            break; // no more comparisons
                        }
                    }

                // save it!!!
                horde.save(name, KeyStroke.getKeyStroke('s'),0 );
                String s1 = "Saved " + name; 
                srvComm.consoleNotify(s1.getBytes()); 
                }

            });
            
                
        box.add(hordeBox); 
        //box.add(experimentBox); 
        box.add(experimentMenu.getButton()); 
        box.add(showTreeButton); 
        box.add(fileNameTreeField);
        box.add(saveTreeButton);
        }
                
    ActionListener experimentListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();
                                                
            for (int i=0; i < experiments.size(); i++) { 
                if (experiments.get(i) == src) { 
                    experimentIdx  = ((HumanoidAgent)horde.getTrainingAgent()).indexOfBehavior(experiments.get(i).behaviorClass);
                    //System.out.println(experiments.get(i).behaviorClass);
                    if (currentlyRunning) {  // stop a running experiment 
                        experimentIdx = ((HumanoidAgent)horde.getTrainingAgent()).indexOfBehavior(sim.app.horde.scenarios.humanoid.Stop.class); 
                        }
                                         
                    //horde.getTrainingMacro().userChangedBehavior(horde, idx );
                    currentlyRunning = !currentlyRunning; 
                    break; 
                    }
                }
            //System.out.println(experimentIdx); 
            }
        };
        
        
    private class HordeDropdownMenu extends JPopupMenu implements
                                                       ActionListener, PopupMenuListener {

        private static final long serialVersionUID = -1;
        JToggleButton button;

        public HordeDropdownMenu(String s, Icon icon) {
            super(s);
            button = new JToggleButton(s, icon);
            button.addActionListener(this);
            button.setFocusable(false);
            addPopupMenuListener(this);
            setFocusable(false);
            }
        public JToggleButton getButton() {
            return button;
            }
        @Override public void actionPerformed(ActionEvent e) {
            JToggleButton b = (JToggleButton) e.getSource();
            if (b.isSelected()) show(b, 0, b.getHeight());
            else setVisible(false);
            }
        @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    button.doClick();
                    }
                });
            }
        @Override public void popupMenuCanceled(PopupMenuEvent e) {}
        @Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
        }
        
        
    private class HordeMenuItem extends JCheckBoxMenuItem { 
        private static final long serialVersionUID = 1L;
        public Class<?> behaviorClass; 
                
        public HordeMenuItem (Behavior b) 
            {
            super(b.getName()); 
            behaviorClass = b.getClass(); 
            setToolTipText("Run " + b.getName());
            setFocusable(false);
            }
                
        };
    }
