package the.bytecode.club.uikit.tabpopup.closer;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTabbedPane;

import the.bytecode.club.uikit.tabpopup.ITabZeroComponentEventListener;

/**
 * Provide function of closing tabs
 * @author su
 *
 */
public class JTabbedPaneCloser {
	private JTabbedPane tabbedPane;
	private ITabZeroComponentEventListener tabZeroComponentEventListener;

	public JTabbedPaneCloser(JTabbedPane tabbedPane) {
		super();
		this.tabbedPane = tabbedPane;
	}
	
	public JTabbedPaneCloser(JTabbedPane tabbedPane, ITabZeroComponentEventListener tabZeroComponentEventListener) {
		this(tabbedPane);
		this.tabZeroComponentEventListener = tabZeroComponentEventListener;
	}
	
	public void removeComponent(Component component) {
    	this.tabbedPane.remove(component);
        tryTriggerTabZeroComponentEvent();
    }
	
	public void removeOtherComponents(Component component) {
		removeOtherComponents(component, false);
	}
	
	protected void removeOtherComponents(Component component, boolean equalStop) {
        int i = this.tabbedPane.getTabCount();
        while (i-- > 0) {
            Component c = this.tabbedPane.getComponentAt(i);
            if (c != component) {
            	this.tabbedPane.remove(i);
            } else if (equalStop) {
            	break ;
            }
        }
        
        tryTriggerTabZeroComponentEvent();
    }
	
	public void removeLeftComponents(Component component) {
		int count = this.tabbedPane.getTabCount();
		int i = 0;
		List<Component> removeTabs = new ArrayList<>();
		do {
			Component c = this.tabbedPane.getComponentAt(i);
			if (c != component) {
				removeTabs.add(c);
			} else {
				break ;
			}
		} while (i++ < count);
		
		for (Component c : removeTabs) {
			this.tabbedPane.remove(c);
		}
		
		tryTriggerTabZeroComponentEvent();
	}
	
	public void removeRightComponents(Component component) {
		removeOtherComponents(component, true);
	}
	
	public void removeAllComponents() {
        this.tabbedPane.removeAll();
        tryTriggerTabZeroComponentEvent();
    }
    
    private void tryTriggerTabZeroComponentEvent() {
    	if (this.tabbedPane.getTabCount() == 0 && tabZeroComponentEventListener != null) {
        	tabZeroComponentEventListener.onTabZeroComponent(this.tabbedPane);
        }
    }
}
