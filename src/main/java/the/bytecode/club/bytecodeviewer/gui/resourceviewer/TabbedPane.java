package the.bytecode.club.bytecodeviewer.gui.resourceviewer;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.gui.components.ButtonHoverAnimation;
import the.bytecode.club.bytecodeviewer.gui.components.MaxWidthJLabel;
import the.bytecode.club.bytecodeviewer.gui.components.listeners.MouseClickedListener;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ResourceViewer;
import the.bytecode.club.bytecodeviewer.gui.util.DelayTabbedPaneThread;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;

/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Kalen 'Konloch' Kinloch - http://bytecodeviewer.com  *
 *                                                                         *
 * This program is free software: you can redistribute it and/or modify    *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation, either version 3 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>. *
 ***************************************************************************/

/**
 * Component to be used as tabComponent; Contains a JLabel to show the text and a JButton to close the tab it belongs to
 *
 * @author Konloch
 * @author WaterWolf
 * @since 09/26/2011
 */
public class TabbedPane extends JPanel
{
    public final JTabbedPane tabs;
    public final JLabel label;
    private DelayTabbedPaneThread probablyABadIdea;
    private long startedDragging = 0;
    public final String tabName;
    public final String fileContainerName;
    public final ResourceViewer resource;
    private static long lastMouseClick = System.currentTimeMillis();
    public final static MouseListener buttonHoverAnimation = new ButtonHoverAnimation();
    public static final Color BLANK_COLOR = new Color(0, 0, 0, 0);
    
    public TabbedPane(int tabIndex, String tabWorkingName, String fileContainerName, String name, final JTabbedPane existingTabs, ResourceViewer resource)
    {
        // unset default FlowLayout' gaps
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));

        this.tabName = name;
        this.fileContainerName = fileContainerName;
        this.resource = resource;
    
        if (existingTabs == null)
            throw new NullPointerException("TabbedPane is null");

        this.tabs = existingTabs;
        setOpaque(false);

        // make JLabel read titles from JTabbedPane
        label = new MaxWidthJLabel(tabName, 400, 20);

        this.add(label);
        // add more space between the label and the button
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        // tab button
        JButton exitButton = new TabExitButton(this, tabIndex, tabWorkingName);
        this.add(exitButton);
        // add more space to the top of the component
        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        
        //define the right click pop-up menu
        JPopupMenu rightClickMenu = new JPopupMenu();
        JMenuItem closeAllTabs = new JMenuItem(TranslatedStrings.CLOSE_ALL_BUT_THIS + ": " + name);
        JMenuItem closeTab = new JMenuItem(TranslatedStrings.CLOSE_TAB + ": " + name);
    
        rightClickMenu.add(closeAllTabs);
        rightClickMenu.add(closeTab);
        //setComponentPopupMenu(rightClickMenu);
        
        exitButton.setComponentPopupMenu(rightClickMenu);
        exitButton.addMouseListener(new MouseClickedListener(e ->
        {
            if (e.getModifiersEx() != InputEvent.ALT_DOWN_MASK || System.currentTimeMillis() - lastMouseClick < 100)
                return;
    
            lastMouseClick = System.currentTimeMillis();
            final int i = existingTabs.indexOfTabComponent(TabbedPane.this);
            if (i != -1)
                existingTabs.remove(i);
        }));
        
        closeTab.addActionListener(e ->
        {
            TabExitButton tabExitButton = (TabExitButton) ((JPopupMenu)((JMenuItem) e.getSource()).getParent()).getInvoker();
            final int index = tabExitButton.getTabIndex();
    
            if (index != -1)
                existingTabs.remove(index);
        });
        closeAllTabs.addActionListener(e ->
        {
            TabExitButton tabExitButton = (TabExitButton) ((JPopupMenu)((JMenuItem) e.getSource()).getParent()).getInvoker();
            final int index = tabExitButton.getTabIndex();
            
            while (true)
            {
                if (existingTabs.getTabCount() <= 1)
                    return;
                
                if (index != 0)
                    existingTabs.remove(0);
                else
                    existingTabs.remove(1);
            }
        });
        
        //tab dragging
        if(BytecodeViewer.EXPERIMENTAL_TAB_CODE)
        {
            /*label.addMouseListener(new MouseListener() {
                @Override public void mouseClicked(MouseEvent e) {}
                @Override public void mouseEntered(MouseEvent arg0) {
                }
                @Override public void mouseExited(MouseEvent arg0) {
                }
                @Override public void mousePressed(MouseEvent e) {
                    onMousePressed(e);
                }
                @Override public void mouseReleased(MouseEvent e) {
                    stopDragging(e.getX(), e.getY());
                }
            });*/
            
            this.addMouseListener(new MouseListener() {
                @Override public void mouseClicked(MouseEvent e) {}
                @Override public void mouseEntered(MouseEvent arg0) {}
                @Override public void mouseExited(MouseEvent arg0) {}
                @Override public void mousePressed(MouseEvent e) {
                    onMousePressed(e);
                }
                @Override public void mouseReleased(MouseEvent e) {
                    stopDragging(e.getX(), e.getY());
                }
            });
        }
        
        //middle click close
        if(BytecodeViewer.EXPERIMENTAL_TAB_CODE)
        {
            this.addMouseListener(new MouseAdapter()
            {
                @Override
                public void mouseReleased(MouseEvent e)
                {
                    if (e.getButton() != MouseEvent.BUTTON2)
                        return;
    
                    final int i = existingTabs.indexOfTabComponent(TabbedPane.this);
                    if (i != -1)
                        existingTabs.remove(i);
                }
            });
        }
    }

    private void stopDragging(int mouseX, int mouseY)
    {
        if (System.currentTimeMillis() - startedDragging >= 210)
        {
            Rectangle bounds = new Rectangle(1, 1, mouseX, mouseY);
            System.out.println("debug-5: " + mouseX + ", " + mouseY);
            
            int totalTabs = BytecodeViewer.viewer.workPane.tabs.getTabCount();
            int index = -1;
            for (int i = 0; i < totalTabs; i++)
            {
                Component c = BytecodeViewer.viewer.workPane.tabs.getTabComponentAt(i);
                
                if (c != null && bounds.intersects(c.getBounds()))
                    index = i; //replace this tabs position
            }

            if (index == -1)
            {
                for (int i = 0; i < totalTabs; i++)
                {
                    Component c = BytecodeViewer.viewer.workPane.tabs.getTabComponentAt(i);
                    //do some check to see if it's past the X or Y
                    if (c != null) {
                        System.out.println("debug-6: " + c.getBounds());
                    }
                }
            }

            if (index != -1)
            {
                BytecodeViewer.viewer.workPane.tabs.remove(this);
                BytecodeViewer.viewer.workPane.tabs.setTabComponentAt(index, this);
            }
        }
        
        SwingUtilities.invokeLater(() ->
        {
            label.setBackground(BLANK_COLOR);
            label.updateUI();
        });
    }
    
    public void onMousePressed(MouseEvent e)
    {
        BytecodeViewer.viewer.workPane.tabs.dispatchEvent(e);
    
        if(e.getButton() == 1)
        {
            startedDragging = System.currentTimeMillis();
            //dragging = true;
            if (probablyABadIdea != null)
                probablyABadIdea.stopped = true;
            
            probablyABadIdea = new DelayTabbedPaneThread(TabbedPane.this);
            probablyABadIdea.start();
            repaint();
            System.out.println(e.getX()+", "+e.getY());
            Rectangle bounds = new Rectangle(1, 1, e.getX(), e.getY());
            for(int i = 0; i < BytecodeViewer.viewer.workPane.tabs.getTabCount(); i++)
            {
                Component c = BytecodeViewer.viewer.workPane.tabs.getTabComponentAt(i);
                if(c != null && bounds.intersects(c.getBounds()))
                    BytecodeViewer.viewer.workPane.tabs.setSelectedIndex(i);
            }
        }
        else
        {
            stopDragging(e.getX(), e.getY());
        }
    }
    
    private static final long serialVersionUID = -4774885688297538774L;
    
}
