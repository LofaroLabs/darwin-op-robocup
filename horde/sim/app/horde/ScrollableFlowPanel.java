package sim.app.horde;

import java.awt.*;
import javax.swing.*;
 
/**
   SCROLLABLE FLOW PANEL
        
   <p>FlowLayout has a bug: it doesn't respect scroll bars and just continues to
   flow left to right, never wrapping.  This panel is a version of JPanel which
   makes FlowLayout work properly.
           
   <p>From http://forums.sun.com/thread.jspa?forumID=57&threadID=701797&start=2
*/

public class ScrollableFlowPanel extends JPanel implements Scrollable {
    private static final long serialVersionUID = 1L;

    public void setBounds( int x, int y, int width, int height ) {
        super.setBounds( x, y, getParent().getWidth(), height );
        }
 
    public Dimension getPreferredSize() {
        return new Dimension( getWidth(), getPreferredHeight() );
        }
 
    public Dimension getPreferredScrollableViewportSize() {
        return super.getPreferredSize();
        }
 
    public int getScrollableUnitIncrement( Rectangle visibleRect, int orientation, int direction ) {
        int hundredth = ( orientation ==  SwingConstants.VERTICAL
            ? getParent().getHeight() : getParent().getWidth() ) / 100;
        return ( hundredth == 0 ? 1 : hundredth ); 
        }
 
    public int getScrollableBlockIncrement( Rectangle visibleRect, int orientation, int direction ) {
        return orientation == SwingConstants.VERTICAL ? getParent().getHeight() : getParent().getWidth();
        }
 
    public boolean getScrollableTracksViewportWidth() {
        return true;
        }
 
    public boolean getScrollableTracksViewportHeight() {
        return false;
        }
 
    private int getPreferredHeight() {
        doLayout();  // for some reason Java won't lay out this container, so we have to force it here, else the components all are size 0
        // see also comment in ButtonArray.setBehaviors(...) near the end
        int rv = 0;
        for ( int k = 0, count = getComponentCount(); k < count; k++ ) {
            Component comp = getComponent( k );
            Rectangle r = comp.getBounds();
            int height = r.y + r.height;
            if ( height > rv )
                rv = height;
            }
        rv += ( (FlowLayout) getLayout() ).getVgap();
        return rv;
        }
    }
