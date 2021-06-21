package the.bytecode.club.bytecodeviewer.gui.hexviewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

/**
 * Created by IntelliJ IDEA. User: laullon Date: 08-abr-2003 Time: 13:21:09
 */
public class JHexEditor extends JPanel implements FocusListener,
        AdjustmentListener, MouseWheelListener {
    private static final long serialVersionUID = 2289328616534802372L;
    byte[] buff;
    public int cursor;
    protected static Font font = new Font("Monospaced", Font.PLAIN, 12);
    protected int border = 2;
    public boolean DEBUG = false;
    private final JScrollBar sb;
    private int inicio = 0;
    private int lineas = 10;

    public JHexEditor(byte[] buff) {
        super();
        this.buff = buff;

        this.addMouseWheelListener(this);

        sb = new JScrollBar(JScrollBar.VERTICAL);
        sb.addAdjustmentListener(this);
        sb.setMinimum(0);
        sb.setMaximum(buff.length / getLineas());

        JPanel p1, p2, p3;
        // centro
        p1 = new JPanel(new BorderLayout(1, 1));
        p1.add(new JHexEditorHEX(this), BorderLayout.CENTER);
        p1.add(new Columnas(), BorderLayout.NORTH);

        // izq.
        p2 = new JPanel(new BorderLayout(1, 1));
        p2.add(new Filas(), BorderLayout.CENTER);
        p2.add(new Caja(), BorderLayout.NORTH);

        // der
        p3 = new JPanel(new BorderLayout(1, 1));
        p3.add(sb, BorderLayout.EAST);
        p3.add(new JHexEditorASCII(this), BorderLayout.CENTER);
        p3.add(new Caja(), BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(1, 1));
        panel.add(p1, BorderLayout.CENTER);
        panel.add(p2, BorderLayout.WEST);
        panel.add(p3, BorderLayout.EAST);

        this.setLayout(new BorderLayout(1, 1));
        this.add(panel, BorderLayout.CENTER);
    }

    @Override
    public void paint(Graphics g) {
        FontMetrics fn = getFontMetrics(font);
        Rectangle rec = this.getBounds();
        lineas = (rec.height / fn.getHeight()) - 1;
        int n = (buff.length / 16) - 1;
        if (lineas > n) {
            lineas = n;
            inicio = 0;
        }

        sb.setValues(getInicio(), +getLineas(), 0, buff.length / 16);
        sb.setValueIsAdjusting(true);
        super.paint(g);
    }

    protected int getInicio() {
        return inicio;
    }

    protected int getLineas() {
        return lineas;
    }

    protected void fondo(Graphics g, int x, int y, int s) {
        FontMetrics fn = getFontMetrics(font);
        g.fillRect(((fn.stringWidth(" ") + 1) * x) + border,
                (fn.getHeight() * y) + border, ((fn.stringWidth(" ") + 1) * s),
                fn.getHeight() + 1);
    }

    protected void cuadro(Graphics g, int x, int y, int s) {
        FontMetrics fn = getFontMetrics(font);
        g.drawRect(((fn.stringWidth(" ") + 1) * x) + border,
                (fn.getHeight() * y) + border, ((fn.stringWidth(" ") + 1) * s),
                fn.getHeight() + 1);
    }

    protected void printString(Graphics g, String s, int x, int y) {
        FontMetrics fn = getFontMetrics(font);
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
        inicio = e.getValue();
        if (inicio < 0)
            inicio = 0;
        repaint();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        inicio += (e.getUnitsToScroll());
        if ((inicio + lineas) >= buff.length / 16)
            inicio = (buff.length / 16) - lineas;
        if (inicio < 0)
            inicio = 0;
        repaint();
    }

    public void keyPressed(KeyEvent e) {
        /*
         * switch(e.getKeyCode()) { case 33: // rep if(cursor>=(16*lineas))
         * cursor-=(16*lineas); actualizaCursor(); break; case 34: // fin
         * if(cursor<(buff.length-(16*lineas))) cursor+=(16*lineas);
         * actualizaCursor(); break; case 35: // fin cursor=buff.length-1;
         * actualizaCursor(); break; case 36: // ini cursor=0;
         * actualizaCursor(); break; case 37: // <-- if(cursor!=0) cursor--;
         * actualizaCursor(); break; case 38: // <-- if(cursor>15) cursor-=16;
         * actualizaCursor(); break; case 39: // --> if(cursor!=(buff.length-1))
         * cursor++; actualizaCursor(); break; case 40: // -->
         * if(cursor<(buff.length-16)) cursor+=16; actualizaCursor(); break; }
         */
    }

    private class Columnas extends JPanel {
        private static final long serialVersionUID = -1734199617526339842L;

        public Columnas() {
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
            d.setSize(((fn.stringWidth(" ") + 1) * +((16 * 3) - 1))
                    + (border * 2) + 1, h * nl + (border * 2) + 1);
            return d;
        }

        @Override
        public void paint(Graphics g) {
            Dimension d = getMinimumSize();
            g.setColor(Color.white);
            g.fillRect(0, 0, d.width, d.height);
            g.setColor(Color.black);
            g.setFont(font);

            for (int n = 0; n < 16; n++) {
                if (n == (cursor % 16))
                    cuadro(g, n * 3, 0, 2);
                String s = "00" + Integer.toHexString(n);
                s = s.substring(s.length() - 2);
                printString(g, s, n * 3, 0);
            }
        }
    }

    private class Caja extends JPanel {
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

    private class Filas extends JPanel {
        private static final long serialVersionUID = 8797347523486018051L;

        public Filas() {
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
            int nl = getLineas();
            d.setSize((fn.stringWidth(" ") + 1) * (8) + (border * 2) + 1, h
                    * nl + (border * 2) + 1);
            return d;
        }

        @Override
        public void paint(Graphics g) {
            Dimension d = getMinimumSize();
            g.setColor(Color.white);
            g.fillRect(0, 0, d.width, d.height);
            g.setColor(Color.black);
            g.setFont(font);

            int ini = getInicio();
            int fin = ini + getLineas();
            int y = 0;
            for (int n = ini; n < fin; n++) {
                if (n == (cursor / 16))
                    cuadro(g, 0, y, 8);
                String s = "0000000000000" + Integer.toHexString(n);
                s = s.substring(s.length() - 8);
                printString(g, s, 0, y++);
            }
        }
    }
}
