package the.bytecode.club.bytecodeviewer.gui.hexviewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JComponent;
import the.bytecode.club.bytecodeviewer.Configuration;

/**
 * @author laullon
 * @since 09/04/2003
 */

public class JHexEditorASCII extends JComponent implements MouseListener, KeyListener
{
    private final JHexEditor he;

    public JHexEditorASCII(JHexEditor he) {
        this.he = he;
        addMouseListener(this);
        addKeyListener(this);
        addFocusListener(he);
    }

    @Override
    public Dimension getPreferredSize() {
        debug("getPreferredSize()");
        return getMinimumSize();
    }

    @Override
    public Dimension getMinimumSize()
    {
        debug("getMinimumSize()");

        Dimension d = new Dimension();
        FontMetrics fn = getFontMetrics(he.font);
        int w = fn.stringWidth(" ");
        int h = fn.getHeight();
        int nl = he.getLineas();
        int len = he.textLength + 1;
        
        int width = (len * w) + (he.border * 2) + 5;
        
        //trim inaccuracy
        if(len > 16)
        {
            int diff = 16-len;
            width += ((len * w) / (diff) * diff);
        }
    
        //System.out.println("Values: " + w + " and " + nl + " vs " + len + " ["+width+"]");
        
        d.setSize(width, h * nl + (he.border * 2) + 1);
        
        return d;
    }

    @Override
    public void paint(Graphics g) {
        debug("paint(" + g + ")");
        debug("cursor=" + he.cursor + " buff.length=" + he.buff.length);
        Dimension d = getMinimumSize();
        g.setColor(Configuration.lafTheme.isDark() ? Color.darkGray : Color.white);
        g.fillRect(0, 0, d.width, d.height);
        g.setColor(Configuration.lafTheme.isDark() ? Color.white : Color.black);

        g.setFont(he.font);

        // datos ascii
        int ini = he.getInicio() * he.textLength;
        int fin = ini + (he.getLineas() * he.textLength);
        if (fin > he.buff.length)
            fin = he.buff.length;

        int x = 0;
        int y = 0;
        for (int n = ini; n < fin; n++) {
            if (n == he.cursor) {
                g.setColor(Color.blue);
                if (hasFocus())
                    he.fondo(g, x, y, 1);
                else
                    he.cuadro(g, x, y, 1);
                if (hasFocus())
                    g.setColor(Configuration.lafTheme.isDark() ? Color.black : Color.white);
                else
                    g.setColor(Configuration.lafTheme.isDark() ? Color.white : Color.black);
            } else {
                g.setColor(Configuration.lafTheme.isDark() ? Color.white : Color.black);
            }

            String s = String.valueOf((char) (he.buff[n] & 0xFF));//"" + new Character((char) he.buff[n]);
            if ((he.buff[n] < 20) || (he.buff[n] > 126))
                s = ".";//"" + (char) 16;
            he.printString(g, s, (x++), y);
            if (x == he.textLength) {
                x = 0;
                y++;
            }
        }

    }

    private void debug(String s) {
        if (he.DEBUG)
            System.out.println("JHexEditorASCII ==> " + s);
    }

    // calcular la posicion del raton
    public int calcularPosicionRaton(int x, int y) {
        FontMetrics fn = getFontMetrics(he.font);
        x = x / (fn.stringWidth(" ") + 1);
        y = y / fn.getHeight();
        debug("x=" + x + " ,y=" + y);
        return x + ((y + he.getInicio()) * he.textLength);
    }

    // mouselistener
    @Override
    public void mouseClicked(MouseEvent e) {
        debug("mouseClicked(" + e + ")");
        he.cursor = calcularPosicionRaton(e.getX(), e.getY());
        this.requestFocus();
        he.repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    // KeyListener
    @Override
    public void keyTyped(KeyEvent e) {
        /*
         * debug("keyTyped("+e+")");
         *
         * he.buff[he.cursor]=(byte)e.getKeyChar();
         *
         * if(he.cursor!=(he.buff.length-1)) he.cursor++; he.repaint();
         */
    }

    @Override
    public void keyPressed(KeyEvent e) {
        debug("keyPressed(" + e + ")");
        he.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        debug("keyReleased(" + e + ")");
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isFocusTraversable() {
        return true;
    }

    @Override
    public boolean isFocusable() {
        return true;
    }
    
    private static final long serialVersionUID = 5505374841731053461L;
}
