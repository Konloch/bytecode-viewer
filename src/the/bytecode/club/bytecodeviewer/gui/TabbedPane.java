package the.bytecode.club.bytecodeviewer.gui;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * Component to be used as tabComponent;
 * Contains a JLabel to show the text and
 * a JButton to close the tab it belongs to
 * 
 * @author Konloch
 * @author WaterWolf
 * 
 */
public class TabbedPane extends JPanel {

	private static final long serialVersionUID = -4774885688297538774L;
	private final JTabbedPane pane;
    final JButton button = new TabButton();

    public TabbedPane(final JTabbedPane pane) {
        //unset default FlowLayout' gaps
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        if (pane == null)
            throw new NullPointerException("TabbedPane is null");
        this.pane = pane;
        setOpaque(false);
        
        //make JLabel read titles from JTabbedPane
        final JLabel label = new JLabel() {
			private static final long serialVersionUID = -5511025206527893360L;

			@Override
            public String getText() {
                final int i = pane.indexOfTabComponent(TabbedPane.this);
                if (i != -1)
                    return pane.getTitleAt(i);
                return null;
            }
        };
        
        add(label);
        //add more space between the label and the button
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        //tab button
        add(button);
        //add more space to the top of the component
        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        pane.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			@Override
			public void mouseReleased(MouseEvent e) {
	           //final Component component = e.getComponent();
	           // if(component instanceof JTabbedPane) {
	            	if(e.getModifiers() == 8) {
            			for(Component c : pane.getComponents()) {
                			if(c.getMousePosition() != null && c instanceof JPanel) {
                				System.out.println("gotten here...");
                				/*BytecodeViewer.viewer.getComponent(WorkPane.class).tabs.remove(component);
                	            final int i = BytecodeViewer.viewer.getComponent(WorkPane.class).tabs.indexOfTabComponent(c);
                	            if (i != -1)
                	            	BytecodeViewer.viewer.getComponent(WorkPane.class).tabs.remove(i);
                	            BytecodeViewer.viewer.getComponent(WorkPane.class).tabs.updateUI();
                	            BytecodeViewer.viewer.getComponent(WorkPane.class).tabs.repaint();
                				*////if(c.getComponentAt((int)c.getMousePosition().getX(), (int)c.getMousePosition().getY())button.)
                					//	button.doClick();
                			}
            				
            				//System.out.println(c.getMousePosition() + ":" + e.getX());		
                			//System.out.println(c.getWidth() + ":" + e.getX());		
		            		//if( e.getX() >=  &&
		            		//	 e.getY())
		            		//	button.doClick();
            			}
	            	}
	            }
			//}
        	
        });
    }

    private class TabButton extends JButton implements ActionListener {
		private static final long serialVersionUID = -4492967978286454159L;

		public TabButton() {
            final int size = 17;
            setPreferredSize(new Dimension(size, size));
            setToolTipText("Close this tab");
            //Make the button looks the same for all Laf's
            setUI(new BasicButtonUI());
            //Make it transparent
            setContentAreaFilled(false);
            //No need to be focusable
            setFocusable(false);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            //Making nice rollover effect
            //we use the same listener for all buttons
            addMouseListener(buttonMouseListener);
            setRolloverEnabled(true);
            //Close the proper tab by clicking the button
            addActionListener(this);
        }

        public void actionPerformed(final ActionEvent e) {
            final int i = pane.indexOfTabComponent(TabbedPane.this);
            if (i != -1) {
                pane.remove(i);
            }
        }

        //we don't want to update UI for this button
        @Override
        public void updateUI() {
        }

        //paint the cross
        @Override
        protected void paintComponent(final Graphics g) {
            super.paintComponent(g);
            final Graphics2D g2 = (Graphics2D) g.create();
            //shift the image for pressed buttons
            if (getModel().isPressed()) {
                g2.translate(1, 1);
            }
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.BLACK);
            if (getModel().isRollover()) {
                g2.setColor(Color.MAGENTA);
            }
            final int delta = 6;
            g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
            g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
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