package com.jhe.hexed;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by IntelliJ IDEA. User: laullon Date: 09-abr-2003 Time: 12:47:18
 */
public class JHexEditorASCII extends JComponent implements MouseListener,
        KeyListener {
    private static final long serialVersionUID = 5505374841731053461L;
    private JHexEditor he;

    public JHexEditorASCII(JHexEditor he) {
        this.he = he;
        addMouseListener(this);
        addKeyListener(this);
        addFocusListener(he);
    }

    public Dimension getPreferredSize() {
        debug("getPreferredSize()");
        return getMinimumSize();
    }

    public Dimension getMinimumSize() {
        debug("getMinimumSize()");

        Dimension d = new Dimension();
        FontMetrics fn = getFontMetrics(JHexEditor.font);
        int h = fn.getHeight();
        int nl = he.getLineas();
        d.setSize((fn.stringWidth(" ") + 1) * (16) + (he.border * 2) + 1, h
                * nl + (he.border * 2) + 1);
        return d;
    }

    public void paint(Graphics g) {
        debug("paint(" + g + ")");
        debug("cursor=" + he.cursor + " buff.length=" + he.buff.length);
        Dimension d = getMinimumSize();
        g.setColor(Color.white);
        g.fillRect(0, 0, d.width, d.height);
        g.setColor(Color.black);

        g.setFont(JHexEditor.font);

        // datos ascii
        int ini = he.getInicio() * 16;
        int fin = ini + (he.getLineas() * 16);
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
                    g.setColor(Color.white);
                else
                    g.setColor(Color.black);
            } else {
                g.setColor(Color.black);
            }

            String s = String.valueOf((char) (he.buff[n] & 0xFF));//"" + new Character((char) he.buff[n]);
            if ((he.buff[n] < 20) || (he.buff[n] > 126))
                s = ".";//"" + (char) 16;
            he.printString(g, s, (x++), y);
            if (x == 16) {
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
        FontMetrics fn = getFontMetrics(JHexEditor.font);
        x = x / (fn.stringWidth(" ") + 1);
        y = y / fn.getHeight();
        debug("x=" + x + " ,y=" + y);
        return x + ((y + he.getInicio()) * 16);
    }

    // mouselistener
    public void mouseClicked(MouseEvent e) {
        debug("mouseClicked(" + e + ")");
        he.cursor = calcularPosicionRaton(e.getX(), e.getY());
        this.requestFocus();
        he.repaint();
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    // KeyListener
    public void keyTyped(KeyEvent e) {
        /*
         * debug("keyTyped("+e+")");
         *
         * he.buff[he.cursor]=(byte)e.getKeyChar();
         *
         * if(he.cursor!=(he.buff.length-1)) he.cursor++; he.repaint();
         */
    }

    public void keyPressed(KeyEvent e) {
        debug("keyPressed(" + e + ")");
        he.keyPressed(e);
    }

    public void keyReleased(KeyEvent e) {
        debug("keyReleased(" + e + ")");
    }

    public boolean isFocusTraversable() {
        return true;
    }
}
