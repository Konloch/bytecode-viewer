package the.bytecode.club.bytecodeviewer.gui;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JTextField;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JTextArea;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Resources;

import javax.swing.JPanel;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

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
 * A simple console GUI.
 *
 * @author Konloch
 */

public class SystemErrConsole extends JFrame {

    JTextArea textArea = new JTextArea();
    JPanel panel = new JPanel(new BorderLayout());
    JScrollPane scrollPane = new JScrollPane();
    public JCheckBox check = new JCheckBox("Exact");
    final JTextField field = new JTextField();
    private PrintStream originalOut;

    public SystemErrConsole(String title) {
        this.setIconImages(Resources.iconList);
        setTitle(title);
        setSize(new Dimension(542, 316));

        getContentPane().add(scrollPane, BorderLayout.CENTER);

        scrollPane.setViewportView(textArea);
        textArea.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    field.requestFocus();
                }

                BytecodeViewer.checkHotKey(e);
            }

            @Override
            public void keyReleased(KeyEvent arg0) {
            }

            @Override
            public void keyTyped(KeyEvent arg0) {
            }
        });

        JButton searchNext = new JButton();
        JButton searchPrev = new JButton();
        JPanel buttonPane = new JPanel(new BorderLayout());
        buttonPane.add(searchNext, BorderLayout.WEST);
        buttonPane.add(searchPrev, BorderLayout.EAST);
        searchNext.setIcon(Resources.nextIcon);
        searchPrev.setIcon(Resources.prevIcon);
        panel.add(buttonPane, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        panel.add(check, BorderLayout.EAST);
        searchNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                search(field.getText(), true);
            }
        });

        searchPrev.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                search(field.getText(), false);
            }
        });
        field.addKeyListener(new KeyListener() {
            @Override
            public void keyReleased(KeyEvent arg0) {
                if (arg0.getKeyCode() == KeyEvent.VK_ENTER)
                    search(field.getText(), true);
            }

            @Override
            public void keyPressed(KeyEvent arg0) {
            }

            @Override
            public void keyTyped(KeyEvent arg0) {
            }
        });
        scrollPane.setColumnHeaderView(panel);
        this.setLocationRelativeTo(null);
        s = new CustomOutputStream(textArea);
        PrintStream printStream = new PrintStream(s);
        originalOut = System.err;
        System.setErr(printStream);
    }

    CustomOutputStream s;

    public void finished() {
        if (originalOut != null)
            System.setErr(originalOut);
    }

    public void pretty() {
        s.update();
        String[] test = null;
        if (textArea.getText().split("\n").length >= 2)
            test = textArea.getText().split("\n");
        else
            test = textArea.getText().split("\r");

        String replace = "";
        for (String s : test) {
            if (s.startsWith("File '")) {
                String[] split = s.split("'");
                String start = split[0] + "'" + split[1] + "', ";
                s = s.substring(start.length(), s.length());
            }
            replace += s + BytecodeViewer.nl;
        }
        setText(replace);
    }

    /**
     * This was really interesting to write.
     *
     * @author Konloch
     */
    public void search(String search, boolean next) {
        try {
            JTextArea area = textArea;
            if (search.isEmpty()) {
                highlight(area, "");
                return;
            }

            int startLine = area.getDocument().getDefaultRootElement()
                    .getElementIndex(area.getCaretPosition()) + 1;
            int currentLine = 1;
            boolean canSearch = false;
            String[] test = null;
            if (area.getText().split("\n").length >= 2)
                test = area.getText().split("\n");
            else
                test = area.getText().split("\r");
            int lastGoodLine = -1;
            int firstPos = -1;
            boolean found = false;

            if (next) {
                for (String s : test) {
                    if (!check.isSelected()) {
                        s = s.toLowerCase();
                        search = search.toLowerCase();
                    }

                    if (currentLine == startLine) {
                        canSearch = true;
                    } else if (s.contains(search)) {
                        if (canSearch) {
                            area.setCaretPosition(area.getDocument()
                                    .getDefaultRootElement()
                                    .getElement(currentLine - 1)
                                    .getStartOffset());
                            canSearch = false;
                            found = true;
                        }

                        if (firstPos == -1)
                            firstPos = currentLine;
                    }

                    currentLine++;
                }

                if (!found && firstPos != -1) {
                    area.setCaretPosition(area.getDocument()
                            .getDefaultRootElement().getElement(firstPos - 1)
                            .getStartOffset());
                }
            } else {
                canSearch = true;
                for (String s : test) {
                    if (!check.isSelected()) {
                        s = s.toLowerCase();
                        search = search.toLowerCase();
                    }

                    if (s.contains(search)) {
                        if (lastGoodLine != -1 && canSearch)
                            area.setCaretPosition(area.getDocument()
                                    .getDefaultRootElement()
                                    .getElement(lastGoodLine - 1)
                                    .getStartOffset());

                        lastGoodLine = currentLine;

                        if (currentLine >= startLine)
                            canSearch = false;
                    }
                    currentLine++;
                }

                if (lastGoodLine != -1
                        && area.getDocument().getDefaultRootElement()
                        .getElementIndex(area.getCaretPosition()) + 1 == startLine) {
                    area.setCaretPosition(area.getDocument()
                            .getDefaultRootElement()
                            .getElement(lastGoodLine - 1).getStartOffset());
                }
            }
            highlight(area, search);
        } catch (Exception e) {
            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
        }
    }

    private DefaultHighlighter.DefaultHighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(
            new Color(255, 62, 150));

    public void highlight(JTextComponent textComp, String pattern) {
        if (pattern.isEmpty()) {
            textComp.getHighlighter().removeAllHighlights();
            return;
        }

        try {
            Highlighter hilite = textComp.getHighlighter();
            hilite.removeAllHighlights();
            javax.swing.text.Document doc = textComp.getDocument();
            String text = doc.getText(0, doc.getLength());
            int pos = 0;

            if (!check.isSelected()) {
                pattern = pattern.toLowerCase();
                text = text.toLowerCase();
            }

            // Search for pattern
            while ((pos = text.indexOf(pattern, pos)) >= 0) {
                // Create highlighter using private painter and apply around
                // pattern
                hilite.addHighlight(pos, pos + pattern.length(), painter);
                pos += pattern.length();
            }
        } catch (Exception e) {
            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
        }
    }

    /**
     * Appends \r\n to the end of your string, then it puts it on the top.
     *
     * @param t the string you want to append
     */
    public void appendText(String t) {
        textArea.setText((textArea.getText().isEmpty() ? "" : textArea
                .getText() + "\r\n")
                + t);
        textArea.setCaretPosition(0);
    }

    /**
     * Sets the text
     *
     * @param t the text you want set
     */
    public void setText(String t) {
        textArea.setText(t);
        textArea.setCaretPosition(0);
    }

    class CustomOutputStream extends OutputStream {
        private StringBuilder sb = new StringBuilder();
        private JTextArea textArea;

        public CustomOutputStream(JTextArea textArea) {
            this.textArea = textArea;
        }

        public void update() {
            textArea.append(sb.toString());
        }

        @Override
        public void write(int b) throws IOException {
            sb.append(String.valueOf((char) b));
        }
    }

    private static final long serialVersionUID = -6556940545421437508L;
}
