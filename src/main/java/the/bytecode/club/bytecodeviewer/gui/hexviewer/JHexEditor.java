package the.bytecode.club.bytecodeviewer.gui.hexviewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;

/**
 * @author laullon
 * @since 08/04/2003
 */

public class JHexEditor extends JPanel implements FocusListener, AdjustmentListener, MouseWheelListener
{
    protected int textLength = 16;
    protected int lastWidth;
    
    byte[] buf;
    public int cursor;
    public Font font;
    protected int border = 2;
    public boolean DEBUG = false;
    private final JScrollBar sb;
    private int begin = 0;
    private int lines = 10;
    private final JHexEditorHEX hex;
    private final JHexEditorASCII ascii;
    
    public JHexEditor(byte[] buff)
    {
        super();
    
        this.buf = buff;
        this.font = new Font(Font.MONOSPACED, Font.PLAIN,  BytecodeViewer.viewer.getFontSize());
        
        checkSize();

        this.addMouseWheelListener(this);

        sb = new JScrollBar(JScrollBar.VERTICAL);
        sb.addAdjustmentListener(this);
        sb.setMinimum(0);
        sb.setMaximum(buff.length / getLines());

        JPanel p1, p2, p3;
        // HEX Editor
        hex = new JHexEditorHEX(this);
        p1 = new JPanel(new BorderLayout(1, 1));
        p1.add(hex, BorderLayout.CENTER);
        p1.add(new Column(), BorderLayout.NORTH);

        p2 = new JPanel(new BorderLayout(1, 1));
        p2.add(new Row(), BorderLayout.CENTER);
        p2.add(new Cell(), BorderLayout.NORTH);

        // ASCII Editor
        ascii = new JHexEditorASCII(this);
        p3 = new JPanel(new BorderLayout(1, 1));
        p3.add(sb, BorderLayout.EAST);
        p3.add(ascii, BorderLayout.CENTER);
        p3.add(new Cell(), BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(1, 1));
        panel.add(p1, BorderLayout.CENTER);
        panel.add(p2, BorderLayout.WEST);
        panel.add(p3, BorderLayout.EAST);

        this.setLayout(new BorderLayout(1, 1));
        this.add(panel, BorderLayout.CENTER);
        
        //attach CTRL + Mouse Wheel Zoom
        SwingUtilities.invokeLater(this::attachCtrlMouseWheelZoom);
    }

    @Override
    public void paint(Graphics g)
    {
        checkSize();
        
        FontMetrics fn = getFontMetrics(font);
        
        Rectangle rec = this.getBounds();
        lines = (rec.height / fn.getHeight()) - 1;
        int n = (buf.length / textLength) - 1;
        if (lines > n) {
            lines = n;
            begin = 0;
        }

        sb.setValues(getBegin(), +getLines(), 0, buf.length / textLength);
        sb.setValueIsAdjusting(true);
        super.paint(g);
    }
    
    public void attachCtrlMouseWheelZoom()
    {
        //get the existing scroll event
        MouseWheelListener ogListener = getMouseWheelListeners().length > 0 ?
                getMouseWheelListeners()[0] : null;
        
        //remove the existing event
        if(ogListener != null)
            removeMouseWheelListener(ogListener);
        
        //add a new event
        addMouseWheelListener(e ->
        {
            if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0)
            {
                int size = font.getSize();
                
                Font newFont;
                if (e.getWheelRotation() > 0) //Up
                    newFont = new Font(font.getName(), font.getStyle(), --size >= 2 ? --size : 2);
                else //Down
                    newFont = new Font(font.getName(), font.getStyle(), ++size);
                
                setFont(newFont);
                hex.setFont(newFont);
                ascii.setFont(newFont);
                font = newFont;
                
                e.consume();
            }
            else if(ogListener != null)
            {
                ogListener.mouseWheelMoved(e);
            }
        });
    }

    protected int getBegin() {
        return begin;
    }

    protected int getLines() {
        return lines;
    }

    protected void background(Graphics g, int x, int y, int s) {
        FontMetrics fn = getFontMetrics(font);
        g.fillRect(((fn.stringWidth(" ") + 1) * x) + border,
                (fn.getHeight() * y) + border, ((fn.stringWidth(" ") + 1) * s),
                fn.getHeight() + 1);
    }

    protected void border(Graphics g, int x, int y, int s) {
        FontMetrics fn = getFontMetrics(font);
        g.drawRect(((fn.stringWidth(" ") + 1) * x) + border,
                (fn.getHeight() * y) + border, ((fn.stringWidth(" ") + 1) * s),
                fn.getHeight() + 1);
    }

    protected void printString(Graphics graphics, String s, int x, int y) {
        Graphics2D g = (Graphics2D) graphics;
        FontMetrics fn = getFontMetrics(font);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.drawString(s, ((fn.stringWidth(" ") + 1) * x) + border,
                ((fn.getHeight() * (y + 1)) - fn.getMaxDescent()) + border);
    }

    @Override
    public void focusGained(FocusEvent e) {
        this.repaint();
    }

    @Override
    public void focusLost(FocusEvent e) {
        this.repaint();
    }

    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
        begin = e.getValue();
        if (begin < 0)
            begin = 0;
        repaint();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        begin += (e.getUnitsToScroll());
        if ((begin + lines) >= buf.length / textLength)
            begin = (buf.length / textLength) - lines;
        if (begin < 0)
            begin = 0;
        repaint();
    }

    public void keyPressed(KeyEvent e) {
         /* switch(e.getKeyCode()) { case 33: // rep if(cursor>=(16*lines))
         cursor-=(16*lines); refreshCursor(); break; case 34: // fin
         if(cursor<(buff.length-(16*lines))) cursor+=(16*lines);
         refreshCursor(); break; case 35: // fin cursor=buff.length-1;
         refreshCursor(); break; case 36: // ini cursor=0;
         refreshCursor(); break; case 37: // <-- if(cursor!=0) cursor--;
         refreshCursor(); break; case 38: // <-- if(cursor>15) cursor-=16;
         refreshCursor(); break; case 39: // --> if(cursor!=(buff.length-1))
         cursor++; refreshCursor(); break; case 40: // -->
         if(cursor<(buff.length-16)) cursor+=16; refreshCursor(); break; } */
    }

    private class Column extends JPanel {
        private static final long serialVersionUID = -1734199617526339842L;

        public Column() {
            this.setLayout(new BorderLayout(1, 1));
        }

        @Override
        public Dimension getPreferredSize() {
            return getMinimumSize();
        }

        @Override
        public Dimension getMinimumSize() {
            Dimension d = new Dimension();
            FontMetrics fn = getFontMetrics(font);
            int h = fn.getHeight();
            int nl = 1;
            d.setSize(((fn.stringWidth(" ") + 1) * ((textLength * 3) - 1))
                    + (border * 2) + 1, h * nl + (border * 2) + 1);
            return d;
        }

        @Override
        public void paint(Graphics g) {
            Dimension d = getMinimumSize();
            g.setColor(Configuration.lafTheme.isDark() ? Color.darkGray : Color.white);
            g.fillRect(0, 0, d.width, d.height);
            g.setColor(Configuration.lafTheme.isDark() ? Color.white : Color.black);
            g.setFont(font);

            for (int n = 0; n < textLength; n++) {
                if (n == (cursor % textLength))
                    border(g, n * 3, 0, 2);
                String s = "00" + Integer.toHexString(n);
                s = s.substring(s.length() - 2);
                printString(g, s, n * 3, 0);
            }
        }
    }

    private class Cell extends JPanel {
        private static final long serialVersionUID = -6124062720565016834L;

        @Override
        public Dimension getPreferredSize() {
            return getMinimumSize();
        }

        @Override
        public Dimension getMinimumSize() {
            Dimension d = new Dimension();
            FontMetrics fn = getFontMetrics(font);
            int h = fn.getHeight();
            d.setSize((fn.stringWidth(" ") + 1) + (border * 2) + 1, h
                    + (border * 2) + 1);
            return d;
        }

    }

    private class Row extends JPanel {
        private static final long serialVersionUID = 8797347523486018051L;

        public Row() {
            this.setLayout(new BorderLayout(1, 1));
        }

        @Override
        public Dimension getPreferredSize() {
            return getMinimumSize();
        }

        @Override
        public Dimension getMinimumSize() {
            Dimension d = new Dimension();
            FontMetrics fn = getFontMetrics(font);
            int h = fn.getHeight();
            int nl = getLines();
            d.setSize((fn.stringWidth(" ") + 1) * (8) + (border * 2) + 1, h
                    * nl + (border * 2) + 1);
            return d;
        }

        @Override
        public void paint(Graphics g)
        {
            Dimension d = getMinimumSize();
            g.setColor(Configuration.lafTheme.isDark() ? Color.darkGray : Color.white);
            g.fillRect(0, 0, d.width, d.height);
            g.setColor(Configuration.lafTheme.isDark() ? Color.white : Color.black);
            g.setFont(font);

            int ini = getBegin();
            int fin = ini + getLines();
            int y = 0;
            for (int n = ini; n < fin; n++)
            {
                if (n == (cursor / textLength))
                    border(g, 0, y, 8);
                String s = "0000000000000" + Integer.toHexString(n);
                s = s.substring(s.length() - 8);
                printString(g, s, 0, y++);
            }
        }
    }
    
    public void checkSize()
    {
        int width = getWidth();
        
        if(lastWidth != width)
        {
            double spacer = 1.5;
            textLength = (int) ((int) (width / 28.4)/spacer);
            lastWidth = width;
            hex.revalidate();
            ascii.revalidate();
            revalidate();
        }
    }
    
    private static final long serialVersionUID = 2289328616534802372L;
}
