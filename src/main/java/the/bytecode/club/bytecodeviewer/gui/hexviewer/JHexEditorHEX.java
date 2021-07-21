package the.bytecode.club.bytecodeviewer.gui.hexviewer;

import the.bytecode.club.bytecodeviewer.Configuration;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JComponent;

/**
 * @author laullon
 * @since 09/04/2003
 */

public class JHexEditorHEX extends JComponent implements MouseListener, KeyListener
{
    private final JHexEditor he;

    public JHexEditorHEX(JHexEditor he)
    {
        this.he = he;
        addMouseListener(this);
        addKeyListener(this);
        addFocusListener(he);
    }

    @Override
    public Dimension getMaximumSize()
    {
        debug("getMaximumSize()");
        return getMinimumSize();
    }

    @Override
    public void paint(Graphics g)
    {
        debug("paint(" + g + ")");
        debug("cursor=" + he.cursor + " buff.length=" + he.buff.length);
        
        if(!Configuration.lafTheme.isDark())
        {
            //TODO if you want a background for the hex-text uncomment this
            //g.setColor(Color.white);
            //g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.black);
        }
        else
        {
            g.setColor(Color.white);
        }

        g.setFont(JHexEditor.font);

        int ini = he.getInicio() * he.textLength;
        int fin = ini + (he.getLineas() * he.textLength);
        if (fin > he.buff.length)
            fin = he.buff.length;

        // datos hex
        int x = 0;
        int y = 0;
        for (int n = ini; n < fin; n++) {
            if (n == he.cursor) {
                if (hasFocus()) {
                    g.setColor(Color.black);
                    he.fondo(g, (x * 3), y, 2);
                    g.setColor(Color.blue);
                    int cursor = 0;
                    he.fondo(g, (x * 3) + cursor, y, 1);
                } else {
                    g.setColor(Color.blue);
                    he.cuadro(g, (x * 3), y, 2);
                }

                if (hasFocus())
                    g.setColor(Color.white);
                else
                    g.setColor(Configuration.lafTheme.isDark() ? Color.white : Color.black);
            } else {
                g.setColor(Configuration.lafTheme.isDark() ? Color.white : Color.black);
            }

            String s = ("0" + Integer.toHexString(he.buff[n]));
            s = s.substring(s.length() - 2);
            he.printString(g, s, ((x++) * 3), y);
            if (x == he.textLength) {
                x = 0;
                y++;
            }
        }
    }

    private void debug(String s) {
        if (he.DEBUG)
            System.out.println("JHexEditorHEX ==> " + s);
    }

    // calcular la posicion del raton
    public int calcularPosicionRaton(int x, int y) {
        FontMetrics fn = getFontMetrics(JHexEditor.font);
        x = x / ((fn.stringWidth(" ") + 1) * 3);
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
        debug("keyTyped(" + e + ")");

        /*
         * char c=e.getKeyChar();
         * if(((c>='0')&&(c<='9'))||((c>='A')&&(c<='F'))||((c>='a')&&(c<='f')))
         * { char[] str=new char[2]; String
         * n="00"+Integer.toHexString((int)he.buff[he.cursor]); if(n.length()>2)
         * n=n.substring(n.length()-2); str[1-cursor]=n.charAt(1-cursor);
         * str[cursor]=e.getKeyChar();
         * he.buff[he.cursor]=(byte)Integer.parseInt(new String(str),16);
         *
         * if(cursor!=1) cursor=1; else if(he.cursor!=(he.buff.length-1)){
         * he.cursor++; cursor=0;} he.actualizaCursor(); }
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
    
    private static final long serialVersionUID = 1481995655372014571L;
}
