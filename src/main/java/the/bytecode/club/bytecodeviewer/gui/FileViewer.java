package the.bytecode.club.bytecodeviewer.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.imgscalr.Scalr;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.Resources;
import the.bytecode.club.bytecodeviewer.gui.hexviewer.JHexEditor;
import the.bytecode.club.bytecodeviewer.util.FileContainer;
import the.bytecode.club.bytecodeviewer.util.Language;

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
 * Represents any open non-class file.
 *
 * @author Konloch
 */

public class FileViewer extends Viewer {

    private static final long serialVersionUID = 6103372882168257164L;

    String name;
    private final byte[] contents;
    RSyntaxTextArea panelArea = Configuration.rstaTheme.apply(new RSyntaxTextArea());
    JPanel panel = new JPanel(new BorderLayout());
    JPanel panel2 = new JPanel(new BorderLayout());
    public JCheckBox check = new JCheckBox("Exact");
    final JTextField field = new JTextField();
    public BufferedImage image;
    boolean canRefresh = false;
    public TabbedPane tabbedPane;

    public void setContents() {
        String name = this.name.toLowerCase();
        panelArea.setCodeFoldingEnabled(true);
        panelArea.setAntiAliasingEnabled(true);
        RTextScrollPane scrollPane = new RTextScrollPane(panelArea);
        panelArea.addKeyListener(new KeyListener() {
            @Override
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

        String contentsS = new String(contents);

        if (!isPureAscii(contentsS)) {
            if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") ||
                    name.endsWith(".gif") || name.endsWith(".tif") || name.endsWith(".bmp")) {
                canRefresh = true;
                try {
                    image = ImageIO.read(new ByteArrayInputStream(contents)); //gifs fail cause of this
                    JLabel label = new JLabel("", new ImageIcon(image), JLabel.CENTER);
                    panel2.add(label, BorderLayout.CENTER);
                    panel2.addMouseWheelListener(e -> {
                        int notches = e.getWheelRotation();
                        if (notches < 0) {
                            image = Scalr.resize(image, Scalr.Method.SPEED, image.getWidth() + 10,
                                    image.getHeight() + 10);
                        } else {
                            image = Scalr.resize(image, Scalr.Method.SPEED, image.getWidth() - 10,
                                    image.getHeight() - 10);
                        }
                        panel2.removeAll();
                        JLabel label1 = new JLabel("", new ImageIcon(image), JLabel.CENTER);
                        panel2.add(label1, BorderLayout.CENTER);
                        panel2.updateUI();
                    });
                    return;
                } catch (Exception e) {
                    new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
                }
            } else if (BytecodeViewer.viewer.forcePureAsciiAsText.isSelected()) {
                JHexEditor hex = new JHexEditor(contents);
                panel2.add(hex);
                return;
            }
        }

        panelArea.setSyntaxEditingStyle(Language.detectLanguage(name, contentsS).getSyntaxConstant());
        panelArea.setText(contentsS);
        panelArea.setCaretPosition(0);
        scrollPane.setColumnHeaderView(panel);
        panel2.add(scrollPane);
    }

    static CharsetEncoder asciiEncoder = StandardCharsets.US_ASCII.newEncoder(); // or "ISO-8859-1" for ISO Latin 1

    public static boolean isPureAscii(String v) {
        return asciiEncoder.canEncode(v);
    }

    public FileViewer(final FileContainer container, final String name, final byte[] contents) {
        this.name = name;
        this.contents = contents;
        this.container = container;
        this.setName(name);
        this.setLayout(new BorderLayout());

        this.add(panel2, BorderLayout.CENTER);

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
        searchNext.addActionListener(arg0 -> search(field.getText(), true));
        searchPrev.addActionListener(arg0 -> search(field.getText(), false));
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

        setContents();
    }

    /**
     * This was really interesting to write.
     *
     * @author Konloch
     */
    public void search(String search, boolean next) {
        try {
            JTextArea area = panelArea;
            if (search.isEmpty()) {
                highlight(area, "");
                return;
            }

            int startLine = area.getDocument().getDefaultRootElement()
                    .getElementIndex(area.getCaretPosition()) + 1;
            int currentLine = 1;
            boolean canSearch = false;
            String[] test;
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

    private final DefaultHighlighter.DefaultHighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(
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

    public void refresh(JButton src) {
        if (!canRefresh) {
            src.setEnabled(true);
            return;
        }

        panel2.removeAll();
        try {
            image = ImageIO.read(new ByteArrayInputStream(contents));
        } catch (IOException e) {
            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
        }
        JLabel label = new JLabel("", new ImageIcon(image), JLabel.CENTER);
        panel2.add(label, BorderLayout.CENTER);
        panel2.updateUI();

        src.setEnabled(true);
    }
}
