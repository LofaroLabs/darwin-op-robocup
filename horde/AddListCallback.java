package sim.app.horde;
import javax.swing.*;
import java.awt.*;

/**
   ADD LIST CALLBACK
        
   <p>An interface which encapsulates all the elements necessary for an AddList to include
   a given object and manipulate it.  AddListCallbacks will be told where their object is
   located in the top list; must be able to provide a JComponent which will appear in the top list 
   via copyElement(); will be told when such an element is deleted from the top list; and must
   be able to provide a useful label.
*/

public interface AddListCallback
    {
    /** Called when the element is being directly added to the top list or is being copied from the bottom list 
    	to the top list.  You are responsible
        for providing a JComponent which will appear in the top list (often a JButton or JLabel), and keep
        track of such a JComponent.  Your JComponent can respond to mouse events if necessary.  Keep in mind
        that you might be called on to produce multiple elements.  If user is true, then copyElement()
        is being called in response to the user adding an element; otherwise it is being called to build
        the list initially. */
    public JComponent promoteElement(boolean user);
        
    /** When a mouse clicks on your JComponent, this method is first called to inform the component of
        where it is located in space. This is mostly useful for knowing where to pop up JMenus.  */
    public void setComponentLocation(Point p);
        
    /** When the user deletes the JComponent from the top list, this method is called to inform you of it. */
    public void unincludeElement(JComponent element);
        
    /** This method should return a string label which is useful for placing in the bottom list. */
    public String toString();
    }