package the.bytecode.club.bytecodeviewer.gui;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicButtonUI;

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
 */
public class TabbedPane extends JPanel {

    private static final long serialVersionUID = -4774885688297538774L;
    public static final Color BLANK = new Color(0,0,0,0);
    private final JTabbedPane pane;
    public final JLabel label;
    private final JButton button = new TabButton();
    private static long zero = System.currentTimeMillis();
    private long startedDragging = 0;
    private boolean dragging = false;
    private DelayTabbedPaneThread probablyABadIdea;
    private TabbedPane THIS = this;
    public String tabName;
    public String fileContainerName;

    public TabbedPane(String fileContainerName, String name, final JTabbedPane pane) {
        // unset default FlowLayout' gaps
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));

        this.tabName = name;
        this.fileContainerName = fileContainerName;

        if (pane == null)
            throw new NullPointerException("TabbedPane is null");

        this.pane = pane;
        setOpaque(false);

        // make JLabel read titles from JTabbedPane
        label = new JLabel() {
            private static final long serialVersionUID = -5511025206527893360L;

            @Override
            public String getText() {
                final int i = pane.indexOfTabComponent(TabbedPane.this);
                if (i != -1)
                    return pane.getTitleAt(i);
                return null;
            }

            @Override
            public Dimension getPreferredSize() {
                Dimension realDimension = super.getPreferredSize();
                if (realDimension.getWidth() >= 400)
                    return new Dimension(400, 20);
                else
                    return realDimension;
            }
        };

        this.add(label);
        // add more space between the label and the button
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        // tab button
        this.add(button);
        // add more space to the top of the component
        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        JPopupMenu pop_up = new JPopupMenu();
        JMenuItem closealltab = new JMenuItem("Close All But This: " + name);
        JMenuItem closetab = new JMenuItem("Close Tab: " + name);
        closetab.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = e.getActionCommand().split(": ")[1];
                final int i = pane.indexOfTab(name);
                if (i != -1)
                    pane.remove(i);
            }
        });
        closealltab.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = e.getActionCommand().split(": ")[1];
                boolean removedAll = false;
                while (!removedAll) {
                    int thisID = pane.indexOfTab(name);
                    if (pane.getTabCount() <= 1) {
                        removedAll = true;
                        return;
                    }
                    if (thisID != 0)
                        pane.remove(0);
                    else
                        pane.remove(1);
                }
            }
        });

        pop_up.add(closealltab);
        pop_up.add(closetab);
        //setComponentPopupMenu(pop_up);
        button.setComponentPopupMenu(pop_up);

        button.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getModifiers() == 8) {
                    if (System.currentTimeMillis() - zero >= 100) {
                        zero = System.currentTimeMillis();
                        final int i = pane.indexOfTabComponent(TabbedPane.this);
                        if (i != -1)
                            pane.remove(i);
                    }
                }
            }

            @Override public void mouseEntered(MouseEvent arg0) { }
            @Override public void mouseExited(MouseEvent arg0) { }
            @Override public void mousePressed(MouseEvent arg0) { }
            @Override public void mouseReleased(MouseEvent e) { }
        });
        /*this.addMouseListener(new MouseListener() {
            @Override public void mouseClicked(MouseEvent e) {}
            @Override public void mouseEntered(MouseEvent arg0) {
            }
            @Override public void mouseExited(MouseEvent arg0) {
            }
            @Override public void mousePressed(MouseEvent e) {
                if(e.getButton() == 1)
                {
                    startedDragging = System.currentTimeMillis();
                    dragging = true;
                    if (probablyABadIdea != null)
                    {
                        probablyABadIdea.stopped = true;
                    }
                    probablyABadIdea = new DelayTabbedPaneThread(THIS);
                    probablyABadIdea.start();
                    repaint();
                    System.out.println(e.getX()+", "+e.getY());
                    Rectangle bounds = new Rectangle(1, 1, e.getX(), e.getY());
                    for(int i = 0; i < BytecodeViewer.viewer.workPane.tabs.getTabCount(); i++)
                    {
                        Component c = BytecodeViewer.viewer.workPane.tabs.getTabComponentAt(i);
                        if(c != null && bounds.intersects(c.getBounds()))
                        {
                            BytecodeViewer.viewer.workPane.tabs.setSelectedIndex(i);
                        }
                    }
                }
                else
                {
                    stopDragging(e.getX(), e.getY());
                }
            }
            @Override public void mouseReleased(MouseEvent e) {
                stopDragging(e.getX(), e.getY());
            }
        });*/
    }

    private void stopDragging(int mouseX, int mouseY)
    {
        if(System.currentTimeMillis()-startedDragging >= 210)
        {
            Rectangle bounds = new Rectangle(1, 1, mouseX, mouseY);
            System.out.println("debug-5: " + mouseX+", " + mouseY);
            int totalTabs = BytecodeViewer.viewer.workPane.tabs.getTabCount();
            int index = -1;
            for(int i = 0; i < totalTabs; i++)
            {
                Component c = BytecodeViewer.viewer.workPane.tabs.getTabComponentAt(i);
                if(c != null && bounds.intersects(c.getBounds()))
                {
                    index = i; //replace this tabs position
                }
            }

            if(index == -1)
            {
                for (int i = 0; i < totalTabs; i++)
                {
                    Component c = BytecodeViewer.viewer.workPane.tabs.getTabComponentAt(i);
                    //do some check to see if it's past the X or Y
                    if(c != null)
                    {
                        System.out.println("debug-6: " + c.getBounds());
                    }
                }
            }

            if(index != -1)
            {
                BytecodeViewer.viewer.workPane.tabs.remove(this);
                BytecodeViewer.viewer.workPane.tabs.setTabComponentAt(index, this);
            }
        }
        dragging = false;
        label.setBackground(BLANK);
        if(probablyABadIdea != null)
        {
            probablyABadIdea.stopped = true;
        }
        label.updateUI();
    }

    private class TabButton extends JButton implements ActionListener {
        private static final long serialVersionUID = -4492967978286454159L;

        public TabButton() {
            final int size = 17;
            setPreferredSize(new Dimension(size, size));
            setToolTipText("Close this tab");
            // Make the button looks the same for all Laf's
            setUI(new BasicButtonUI());
            // Make it transparent
            setContentAreaFilled(false);
            // No need to be focusable
            setFocusable(false);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            // Making nice rollover effect
            // we use the same listener for all buttons
            addMouseListener(buttonMouseListener);
            setRolloverEnabled(true);
            // Close the proper tab by clicking the button
            addActionListener(this);
        }

        public void actionPerformed(final ActionEvent e) {
            final int i = pane.indexOfTabComponent(TabbedPane.this);
            if (i != -1) {
                pane.remove(i);
            }
        }

        // we don't want to update UI for this button
        @Override
        public void updateUI() {
        }

        // paint the cross
        @Override
        protected void paintComponent(final Graphics g)
        {
            super.paintComponent(g);
            final Graphics2D g2 = (Graphics2D) g.create();
            // shift the image for pressed buttons
            if (getModel().isPressed()) {
                g2.translate(1, 1);
            }
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.BLACK);
            if (getModel().isRollover()) {
                g2.setColor(Color.MAGENTA);
            }
            final int delta = 6;
            g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight()
                    - delta - 1);
            g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight()
                    - delta - 1);
            g2.dispose();
        }
    }

    private final static MouseListener buttonMouseListener = new MouseAdapter() {
        @Override
        public void mouseEntered(final MouseEvent e) {
            final Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                final AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(true);
            }
        }

        @Override
        public void mouseExited(final MouseEvent e) {
            final Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                final AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(false);
            }
        }
    };
}