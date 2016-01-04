package the.bytecode.club.bytecodeviewer.gui;

import com.jhe.hexed.JHexEditor;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Resources;
import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;

import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Kalen 'Konloch' Kinloch - http://bytecodeviewer.com  *
 * *
 * This program is free software: you can redistribute it and/or modify    *
 * it under the terms of the GNU General Public License as published by  *
 * the Free Software Foundation, either version 3 of the License, or     *
 * (at your option) any later version.                                   *
 * *
 * This program is distributed in the hope that it will be useful,       *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 * GNU General Public License for more details.                          *
 * *
 * You should have received a copy of the GNU General Public License     *
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. *
 ***************************************************************************/

/**
 * This represents the opened classfile.
 *
 * @author Konloch
 * @author WaterWolf
 */

public class ClassViewer extends Viewer {
    private static final long serialVersionUID = -8650495368920680024L;
    private List<Thread> decompileThreads = new ArrayList<>();

    public void setPanes() {
        for (int i = 0; i < BytecodeViewer.viewer.allPanes.size(); i++) {
            ButtonGroup group = BytecodeViewer.viewer.allPanes.get(i);
            for (Map.Entry<JRadioButtonMenuItem, Decompiler> entry : BytecodeViewer.viewer.allDecompilers.get(group).entrySet()) {
                if (group.isSelected(entry.getKey().getModel())) {
                    decompilers.set(i, entry.getValue());
                }
            }
        }
    }

    public boolean isPaneEditable(int pane) {
        setPanes();
        ButtonGroup buttonGroup = BytecodeViewer.viewer.allPanes.get(pane);
        Decompiler selected = decompilers.get(pane);
        if (buttonGroup != null && BytecodeViewer.viewer.editButtons.get(buttonGroup) != null && BytecodeViewer.viewer.editButtons.get(buttonGroup).get(selected)!= null && BytecodeViewer.viewer.editButtons.get(buttonGroup).get(selected).isSelected()) {
            return true;
        }
        return false;
    }

    public void requestFocus(int pane) {
        this.fields.get(pane).requestFocus();
    }

    public void updatePane(int pane, RSyntaxTextArea text, Decompiler decompiler) {
        if (decompiler == Decompiler.KRAKATAU_DA) {
            krakataus.set(pane, text);
        } else if (decompiler == Decompiler.SMALI) {
            smalis.set(pane, text);
        } else {
            javas.set(pane, text);
        }
    }

    /**
     * Whoever wrote this function, THANK YOU!
     *
     * @param splitter
     * @param proportion
     * @return
     */
    public static JSplitPane setDividerLocation(final JSplitPane splitter,
                                                final double proportion) {
        if (splitter.isShowing()) {
            if (splitter.getWidth() > 0 && splitter.getHeight() > 0) {
                splitter.setDividerLocation(proportion);
            } else {
                splitter.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent ce) {
                        splitter.removeComponentListener(this);
                        setDividerLocation(splitter, proportion);
                    }
                });
            }
        } else {
            splitter.addHierarchyListener(new HierarchyListener() {
                @Override
                public void hierarchyChanged(HierarchyEvent e) {
                    if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0
                            && splitter.isShowing()) {
                        splitter.removeHierarchyListener(this);
                        setDividerLocation(splitter, proportion);
                    }
                }
            });
        }
        return splitter;
    }

    JSplitPane sp;
    JSplitPane sp2;
    public List<Decompiler> decompilers = Arrays.asList(null, null, null);
    public List<JPanel> panels = Arrays.asList(new JPanel(new BorderLayout()), new JPanel(new BorderLayout()), new JPanel(new BorderLayout()));
    public List<JPanel> searches = Arrays.asList(new JPanel(new BorderLayout()), new JPanel(new BorderLayout()), new JPanel(new BorderLayout()));
    public List<JCheckBox> exacts = Arrays.asList(new JCheckBox("Exact"), new JCheckBox("Exact"), new JCheckBox("Exact"));
    public List<JTextField> fields = Arrays.asList(new JTextField(), new JTextField(), new JTextField());
    public List<RSyntaxTextArea> javas = Arrays.asList(null, null, null);
    public List<RSyntaxTextArea> smalis = Arrays.asList(null, null, null);
    public List<RSyntaxTextArea> krakataus = Arrays.asList(null, null, null);

    /**
     * This was really interesting to write.
     *
     * @author Konloch
     */
    public void search(int pane, String search, boolean next) {
        try {
            Component[] com = panels.get(pane).getComponents();
            for (Component c : com) {
                if (c instanceof RTextScrollPane) {
                    RSyntaxTextArea area = (RSyntaxTextArea) ((RTextScrollPane) c)
                            .getViewport().getComponent(0);

                    if (search.isEmpty()) {
                        highlight(pane, area, "");
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
                            if (pane == 0 && !exacts.get(0).isSelected() || pane == 1
                                    && !exacts.get(1).isSelected()) {
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
                                    .getDefaultRootElement()
                                    .getElement(firstPos - 1).getStartOffset());
                        }
                    } else {
                        canSearch = true;
                        for (String s : test) {
                            if (pane == 0 && !exacts.get(0).isSelected() || pane == 1
                                    && !exacts.get(1).isSelected() || pane == 2
                                    && !exacts.get(2).isSelected()) {
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
                                && area.getDocument()
                                .getDefaultRootElement()
                                .getElementIndex(
                                        area.getCaretPosition()) + 1 == startLine) {
                            area.setCaretPosition(area.getDocument()
                                    .getDefaultRootElement()
                                    .getElement(lastGoodLine - 1)
                                    .getStartOffset());
                        }
                    }
                    highlight(pane, area, search);
                }
            }
        } catch (Exception e) {
            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
        }
    }

    private DefaultHighlighter.DefaultHighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(
            new Color(255, 62, 150));

    public void highlight(int pane, JTextComponent textComp, String pattern) {
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

            if ((pane == 0 && !exacts.get(0).isSelected()) || pane == 1
                    && !exacts.get(1).isSelected() || pane == 2
                    && !exacts.get(2).isSelected()) {
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

    public ClassViewer(final String name, final String container, final ClassNode cn) {
        for (int i = 0; i < panels.size(); i++) {
            final JTextField textField = fields.get(i);
            JPanel searchPanel = searches.get(i);
            JCheckBox checkBox = exacts.get(i);
            JButton byteSearchNext = new JButton();
            JButton byteSearchPrev = new JButton();
            JPanel byteButtonPane = new JPanel(new BorderLayout());
            byteButtonPane.add(byteSearchNext, BorderLayout.WEST);
            byteButtonPane.add(byteSearchPrev, BorderLayout.EAST);
            byteSearchNext.setIcon(Resources.nextIcon);
            byteSearchPrev.setIcon(Resources.prevIcon);
            searchPanel.add(byteButtonPane, BorderLayout.WEST);
            searchPanel.add(textField, BorderLayout.CENTER);
            searchPanel.add(checkBox, BorderLayout.EAST);
            byteSearchNext.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent arg0) {
                    search(0, textField.getText(), true);
                }
            });
            byteSearchPrev.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent arg0) {
                    search(0, textField.getText(), false);
                }
            });
            textField.addKeyListener(new KeyListener() {
                @Override
                public void keyReleased(KeyEvent arg0) {
                    if (arg0.getKeyCode() == KeyEvent.VK_ENTER)
                        search(0, textField.getText(), true);
                }

                @Override
                public void keyPressed(KeyEvent arg0) {
                }

                @Override
                public void keyTyped(KeyEvent arg0) {
                }
            });
        }

        this.name = name;
        this.container = container;
        this.cn = cn;
        updateName();
        this.setLayout(new BorderLayout());

        this.sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panels.get(0), panels.get(1));
        JHexEditor hex = new JHexEditor(BytecodeViewer.getClassBytes(container, cn.name + ".class"));
        this.sp2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sp, panels.get(2));
        this.add(sp2, BorderLayout.CENTER);

        hex.setMaximumSize(new Dimension(0, Integer.MAX_VALUE));
        hex.setSize(0, Integer.MAX_VALUE);

        BytecodeViewer.viewer.setIcon(true);
        startPaneUpdater(null);
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                resetDivider();
            }
        });
    }

    public void resetDivider() {
        sp.setResizeWeight(0.5);
        if (decompilers.get(1) != null && decompilers.get(0) != null)
            sp = setDividerLocation(sp, 0.5);
        else if (decompilers.get(0) != null)
            sp = setDividerLocation(sp, 1);
        else if (decompilers.get(1) != null) {
            sp.setResizeWeight(1);
            sp = setDividerLocation(sp, 0);
        } else
            sp = setDividerLocation(sp, 0);
        if (decompilers.get(2) != null) {
            sp2.setResizeWeight(0.7);
            sp2 = setDividerLocation(sp2, 0.7);
            if ((decompilers.get(1) == null && decompilers.get(0) != null) || (decompilers.get(0) == null && decompilers.get(1) != null))
                sp2 = setDividerLocation(sp2, 0.5);
            else if (decompilers.get(0) == null && decompilers.get(1) == null)
                sp2 = setDividerLocation(sp2, 0);
        } else {
            sp.setResizeWeight(1);
            sp2.setResizeWeight(0);
            sp2 = setDividerLocation(sp2, 1);
        }
    }

    public void startPaneUpdater(final JButton button) {
        this.cn = BytecodeViewer.getClassNode(container, cn.name); //update the classnode
        setPanes();

        for (JPanel jpanel : panels) {
            jpanel.removeAll();
        }
        for (int i = 0; i < javas.size(); i++) {
            javas.set(i, null);
        }
        for (int i = 0; i < smalis.size(); i++) {
            smalis.set(i, null);
        }

        if (this.cn == null) {
            for (JPanel jpanel : panels) {
                jpanel.add(new JLabel("This file has been removed from the reload."));
            }
            return;
        }

        for (int i = 0; i < decompilers.size(); i++) {
            if (decompilers.get(i) != null) {
                if (decompilers.get(i) != Decompiler.HEXCODE) {
                    panels.get(i).add(searches.get(i), BorderLayout.NORTH);
                }
                PaneUpdaterThread t = new PaneUpdaterThread(this, decompilers.get(i), i, panels.get(i), button);
                decompileThreads.add(t);
                t.start();
            }
        }
    }

    public Object[] getSmali() {
        for (int i = 0; i < smalis.size(); i++) {
            RSyntaxTextArea text = smalis.get(i);
            if (text != null) {
                return new Object[]{cn, text.getText()};
            }
        }
        return null;
    }

    public Object[] getKrakatau() {
        for (int i = 0; i < krakataus.size(); i++) {
            RSyntaxTextArea text = krakataus.get(i);
            if (text != null) {
                return new Object[]{cn, text.getText()};
            }
        }
        return null;
    }

    public Object[] getJava() {
        for (int i = 0; i < javas.size(); i++) {
            RSyntaxTextArea text = javas.get(i);
            if (text != null) {
                return new Object[]{cn, text.getText()};
            }
        }
        return null;
    }

    public void reset() {
        for (Thread t : decompileThreads) {
            t.stop();
        }
    }
}
