package the.bytecode.club.bytecodeviewer.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.objectweb.asm.tree.ClassNode;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.FileChangeNotifier;

/**
 * The pane that contains all of the classes as tabs.
 * 
 * @author Konloch
 * @author WaterWolf
 *
 */

public class WorkPane extends VisibleComponent implements ActionListener {
   
	private static final long serialVersionUID = 6542337997679487946L;

    
    FileChangeNotifier fcn;
    JTabbedPane tabs;
    
    JPanel buttonPanel;
    JButton refreshClass;
    
    HashMap<String, Integer> workingOn = new HashMap<String, Integer>();
    
    public static int SyntaxFontHeight = 12;
    
    public WorkPane(final FileChangeNotifier fcn) {
        super("WorkPanel");
        setTitle("Work Space");
        
        this.tabs = new JTabbedPane();
        this.fcn = fcn;
        
        getContentPane().setLayout(new BorderLayout());
        
        getContentPane().add(tabs, BorderLayout.CENTER);
        
        buttonPanel = new JPanel(new FlowLayout());
        
        refreshClass = new JButton("Refresh class");
        refreshClass.addActionListener(this);
        
        buttonPanel.add(refreshClass);
        
        buttonPanel.setVisible(false);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        
        tabs.addContainerListener(new ContainerListener() {

            @Override
            public void componentAdded(final ContainerEvent e) {
            }

            @Override
            public void componentRemoved(final ContainerEvent e) {
                final Component c = e.getChild();
                if (c instanceof ClassViewer) {
                    workingOn.remove(((ClassViewer)c).name);
                }
            }

        });
        tabs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent arg0) {
                buttonPanel.setVisible(tabs.getSelectedIndex() != -1);
            }
        });
        
        this.setVisible(true);
        
    }
    
    int tabCount = 0;
    
    public void addWorkingFile(final String name, final ClassNode cn) {
    	if(!BytecodeViewer.viewer.hexPane.isSelected() &&
    		!BytecodeViewer.viewer.sourcePane.isSelected() &&
    		!BytecodeViewer.viewer.bytecodePane.isSelected()) {
    		BytecodeViewer.showMessage("You currently have no viewing panes selected.");
    		return;
    	}
        if (!workingOn.containsKey(name)) {
            final Component tabComp = new ClassViewer(name, cn);
            tabs.add(tabComp);
            final int tabCount = tabs.indexOfComponent(tabComp);
            workingOn.put(name, tabCount);
            tabs.setTabComponentAt(tabCount, new TabbedPane(tabs));
            tabs.setSelectedIndex(tabCount);
        } else {
            tabs.setSelectedIndex(workingOn.get(name));
        }
    }
    
    @Override
    public void openClassFile(final String name, final ClassNode cn) {
        addWorkingFile(name, cn);
    }
    
    public ClassViewer getCurrentClass() {
        return (ClassViewer) tabs.getSelectedComponent();
    }
    
    @Override
    public void actionPerformed(final ActionEvent arg0) {
        final JButton src = (JButton) arg0.getSource();
        if (src == refreshClass) {
            final Component tabComp = tabs.getSelectedComponent();
            if (tabComp != null) {
				BytecodeViewer.viewer.setC(true);
                ((ClassViewer)tabComp).startPaneUpdater();
				BytecodeViewer.viewer.setC(false);
            }
        }
    }

	public void resetWorkspace() {
		tabs.removeAll();
		tabs.updateUI();
	}

}
