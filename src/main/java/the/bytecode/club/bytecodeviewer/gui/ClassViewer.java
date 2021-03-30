package the.bytecode.club.bytecodeviewer.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.ParagraphView;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import com.jhe.hexed.JHexEditor;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.util.FileContainer;
import the.bytecode.club.bytecodeviewer.Resources;
import the.bytecode.club.bytecodeviewer.Settings;
import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;
import the.bytecode.club.bytecodeviewer.util.MethodParser;

import static the.bytecode.club.bytecodeviewer.util.MethodParser.*;

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
 * This represents the opened classfile.
 *
 * @author Konloch
 * @author WaterWolf
 */

public class ClassViewer extends Viewer
{
    private static final long serialVersionUID = -8650495368920680024L;
    ArrayList<MethodData> lnData = new ArrayList<MethodData>();
    String name;
    JSplitPane sp;
    JSplitPane sp2;
    public JPanel panel1Search = new JPanel(new BorderLayout());
    public JPanel panel2Search = new JPanel(new BorderLayout());
    public JPanel panel3Search = new JPanel(new BorderLayout());
    public JCheckBox check1 = new JCheckBox("Exact");
    public JCheckBox check2 = new JCheckBox("Exact");
    public JCheckBox check3 = new JCheckBox("Exact");
    public JPanel panel1 = new JPanel(new BorderLayout());
    public JPanel panel2 = new JPanel(new BorderLayout());
    public JPanel panel3 = new JPanel(new BorderLayout());
    int pane1 = -1;
    int pane2 = -1;
    int pane3 = -1;
    public List<MethodParser> methods = Arrays.asList(new MethodParser(), new MethodParser(), new MethodParser());
    public List<Integer> activeLines = Arrays.asList(0, 0, 0);
    public RSyntaxTextArea smali1 = null;
    public RSyntaxTextArea smali2 = null;
    public RSyntaxTextArea smali3 = null;
    public RSyntaxTextArea krakatau1 = null;
    public RSyntaxTextArea krakatau2 = null;
    public RSyntaxTextArea krakatau3 = null;
    public RSyntaxTextArea java1 = null;
    public RSyntaxTextArea java2 = null;
    public RSyntaxTextArea java3 = null;
    public File[] tempFiles;
    public ClassViewer THIS = this;

    /**
     * This was really interesting to write.
     *
     * @author Konloch
     */
    public void search(int pane, String search, boolean next)
    {
        try
        {
            Component[] com = null;
            if (pane == 0) // bytecode
                com = panel1.getComponents();
            else if (pane == 1)
                com = panel2.getComponents();
            else if (pane == 2)
                com = panel3.getComponents();

            if (com == null) // someone fucked up, lets prevent a nullpointer.
                return;

            for (Component c : com)
            {
                if (c instanceof RTextScrollPane)
                {
                    RSyntaxTextArea area = (RSyntaxTextArea) ((RTextScrollPane) c)
                            .getViewport().getComponent(0);

                    if (search.isEmpty())
                    {
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

                    if (next)
                    {
                        for (String s : test)
                        {
                            if (pane == 0 && !check1.isSelected() ||
                                    pane == 1 && !check2.isSelected())
                            {
                                s = s.toLowerCase();
                                search = search.toLowerCase();
                            }

                            if (currentLine == startLine)
                            {
                                canSearch = true;
                            }
                            else if (s.contains(search))
                            {
                                if (canSearch)
                                {
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
                    }
                    else
                    {
                        canSearch = true;
                        for (String s : test)
                        {
                            if (pane == 0 && !check1.isSelected() || pane == 1
                                    && !check2.isSelected() || pane == 2
                                    && !check3.isSelected()) {
                                s = s.toLowerCase();
                                search = search.toLowerCase();
                            }

                            if (s.contains(search))
                            {
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
                                .getElementIndex(area.getCaretPosition()) + 1 == startLine)
                        {
                            area.setCaretPosition(area.getDocument()
                                    .getDefaultRootElement()
                                    .getElement(lastGoodLine - 1)
                                    .getStartOffset());
                        }
                    }
                    highlight(pane, area, search);
                }
            }
        }
        catch (Exception e)
        {
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

            if ((pane == 0 && !check1.isSelected()) || pane == 1
                    && !check2.isSelected() || pane == 2
                    && !check3.isSelected()) {
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

    final JTextField field1 = new JTextField();
    final JTextField field2 = new JTextField();
    final JTextField field3 = new JTextField();
    public TabbedPane tabbedPane;

    public ClassViewer(final FileContainer container, final String name, final ClassNode cn) {
        this.container = container;
        JButton byteSearchNext = new JButton();
        JButton byteSearchPrev = new JButton();
        JPanel byteButtonPane = new JPanel(new BorderLayout());
        byteButtonPane.add(byteSearchNext, BorderLayout.WEST);
        byteButtonPane.add(byteSearchPrev, BorderLayout.EAST);
        byteSearchNext.setIcon(Resources.nextIcon);
        byteSearchPrev.setIcon(Resources.prevIcon);
        panel1Search.add(byteButtonPane, BorderLayout.WEST);
        panel1Search.add(field1, BorderLayout.CENTER);
        panel1Search.add(check1, BorderLayout.EAST);
        byteSearchNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                search(0, field1.getText(), true);
            }
        });
        byteSearchPrev.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                search(0, field1.getText(), false);
            }
        });
        field1.addKeyListener(new KeyListener() {
            @Override
            public void keyReleased(KeyEvent arg0) {
                if (arg0.getKeyCode() == KeyEvent.VK_ENTER)
                    search(0, field1.getText(), true);
            }

            @Override
            public void keyPressed(KeyEvent arg0) {
            }

            @Override
            public void keyTyped(KeyEvent arg0) {
            }
        });

        JButton searchNext2 = new JButton();
        JButton searchPrev2 = new JButton();
        JPanel buttonPane2 = new JPanel(new BorderLayout());
        buttonPane2.add(searchNext2, BorderLayout.WEST);
        buttonPane2.add(searchPrev2, BorderLayout.EAST);
        searchNext2.setIcon(Resources.nextIcon);
        searchPrev2.setIcon(Resources.prevIcon);
        panel2Search.add(buttonPane2, BorderLayout.WEST);
        panel2Search.add(field2, BorderLayout.CENTER);
        panel2Search.add(check2, BorderLayout.EAST);
        searchNext2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                search(1, field2.getText(), true);
            }
        });
        searchPrev2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                search(1, field2.getText(), false);
            }
        });
        field2.addKeyListener(new KeyListener() {
            @Override
            public void keyReleased(KeyEvent arg0) {
                if (arg0.getKeyCode() == KeyEvent.VK_ENTER)
                    search(1, field2.getText(), true);
            }

            @Override
            public void keyPressed(KeyEvent arg0) {
            }

            @Override
            public void keyTyped(KeyEvent arg0) {
            }
        });

        JButton searchNext3 = new JButton();
        JButton searchPrev3 = new JButton();
        JPanel buttonPane3 = new JPanel(new BorderLayout());
        buttonPane3.add(searchNext3, BorderLayout.WEST);
        buttonPane3.add(searchPrev3, BorderLayout.EAST);
        searchNext3.setIcon(Resources.nextIcon);
        searchPrev3.setIcon(Resources.prevIcon);
        panel3Search.add(buttonPane3, BorderLayout.WEST);
        panel3Search.add(field3, BorderLayout.CENTER);
        panel3Search.add(check3, BorderLayout.EAST);
        searchNext3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                search(2, field3.getText(), true);
            }
        });
        searchPrev3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                search(2, field3.getText(), false);
            }
        });
        field3.addKeyListener(new KeyListener() {
            @Override
            public void keyReleased(KeyEvent arg0) {
                if (arg0.getKeyCode() == KeyEvent.VK_ENTER)
                    search(2, field3.getText(), true);
            }

            @Override
            public void keyPressed(KeyEvent arg0) {
            }

            @Override
            public void keyTyped(KeyEvent arg0) {
            }
        });

        this.name = name;
        this.cn = cn;
        this.setName(name);
        this.setLayout(new BorderLayout());

        this.sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel1, panel2);
        final ClassWriter cw = new ClassWriter(0);
        cn.accept(cw);
        JHexEditor hex = new JHexEditor(cw.toByteArray());
        this.sp2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sp, panel3);
        this.add(sp2, BorderLayout.CENTER);

        hex.setMaximumSize(new Dimension(0, Integer.MAX_VALUE));
        hex.setSize(0, Integer.MAX_VALUE);

        startPaneUpdater(null);
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                resetDivider();
            }
        });
    }

    public void resetDivider() {
        sp.setResizeWeight(0.5);
        if (pane2 != 0 && pane1 != 0)
            sp = setDividerLocation(sp, 0.5);
        else if (pane1 != 0)
            sp = setDividerLocation(sp, 1);
        else if (pane2 != 0) {
            sp.setResizeWeight(1);
            sp = setDividerLocation(sp, 0);
        } else
            sp = setDividerLocation(sp, 0);
        if (pane3 != 0) {
            sp2.setResizeWeight(0.7);
            sp2 = setDividerLocation(sp2, 0.7);
            if ((pane2 == 0 && pane1 != 0) || (pane1 == 0 && pane2 != 0))
                sp2 = setDividerLocation(sp2, 0.5);
            else if (pane1 == 0 && pane2 == 0)
                sp2 = setDividerLocation(sp2, 0);
        } else {
            sp.setResizeWeight(1);
            sp2.setResizeWeight(0);
            sp2 = setDividerLocation(sp2, 1);
        }
    }

    PaneUpdaterThread t1;
    PaneUpdaterThread t2;
    PaneUpdaterThread t3;

    public void startPaneUpdater(final JButton button) {
        this.cn = BytecodeViewer.getClassNode(container, cn.name); //update the classnode
        setPanes();

        panel1.removeAll();
        panel2.removeAll();
        panel3.removeAll();
        smali1 = null;
        smali2 = null;
        smali3 = null;
        java1 = null;
        java2 = null;
        java3 = null;

        if (this.cn == null) {
            panel1.add(new JLabel("This file has been removed from the reload."));
            panel2.add(new JLabel("This file has been removed from the reload."));
            panel3.add(new JLabel("This file has been removed from the reload."));
            return;
        }

        if (pane1 != 0 && pane1 != 5)
            panel1.add(panel1Search, BorderLayout.NORTH);
        if (pane2 != 0 && pane2 != 5)
            panel2.add(panel2Search, BorderLayout.NORTH);
        if (pane3 != 0 && pane3 != 5)
            panel3.add(panel3Search, BorderLayout.NORTH);

        final ClassWriter cw = new ClassWriter(0);
        try {
            cn.accept(cw);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Thread.sleep(200);
                cn.accept(cw);
            } catch (InterruptedException e1) {
            }
        }

        final byte[] b = cw.toByteArray();
        t1 = new PaneUpdaterThread() {
            @Override
            public void doShit() {
                try {
                    paneId = 0;
                    viewer = THIS;
                    decompiler = pane1;

                    BytecodeViewer.viewer.setIcon(true);
                    if (pane1 == 1) { // procyon
                        panelArea = new RSyntaxTextArea();
                        panelArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                        panelArea.setCodeFoldingEnabled(true);
                        panelArea.setAntiAliasingEnabled(true);
                        scrollPane = new RTextScrollPane(panelArea);
                        panelArea.setText(Decompiler.procyon.decompileClassNode(cn, b));

                        panelArea.setCaretPosition(0);
                        panelArea.setEditable(isPanel1Editable());
                        panelArea.addKeyListener(new KeyListener() {
                            public void keyPressed(KeyEvent e) {

                                if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                                    field1.requestFocus();
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
                        scrollPane.setColumnHeaderView(new JLabel("Procyon Decompiler - Editable: " + panelArea.isEditable()));
                        panelArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                panel1.add(scrollPane);
                            }
                        });

                        java1 = panelArea;
                    }

                    if (pane1 == 2) {// cfr
                        panelArea = new RSyntaxTextArea();
                        panelArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                        panelArea.setCodeFoldingEnabled(true);
                        panelArea.setAntiAliasingEnabled(true);
                        scrollPane = new RTextScrollPane(panelArea);
                        panelArea.setText(Decompiler.cfr.decompileClassNode(cn, b));
                        panelArea.setCaretPosition(0);
                        panelArea.setEditable(isPanel1Editable());
                        panelArea.addKeyListener(new KeyListener()
                        {
                            public void keyPressed(KeyEvent e)
                            {
                                if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0))
                                {
                                    field1.requestFocus();
                                }

                                BytecodeViewer.checkHotKey(e);
                            }

                            @Override
                            public void keyReleased(KeyEvent arg0)
                            {
                            }

                            @Override
                            public void keyTyped(KeyEvent arg0)
                            {
                            }
                        });
                        scrollPane.setColumnHeaderView(new JLabel("CFR Decompiler - Editable: " + panelArea.isEditable()));
                        panelArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));

                        SwingUtilities.invokeLater(new Runnable()
                        {
                            public void run()
                            {
                                panel1.add(scrollPane);
                            }
                        });

                        java1 = panelArea;
                    }

                    if (pane1 == 3) {// fern
                        panelArea = new RSyntaxTextArea();
                        panelArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                        panelArea.setCodeFoldingEnabled(true);
                        panelArea.setAntiAliasingEnabled(true);
                        scrollPane = new RTextScrollPane(panelArea);
                        panelArea.setText(Decompiler.fernflower.decompileClassNode(cn, b));
                        panelArea.setCaretPosition(0);
                        panelArea.setEditable(isPanel1Editable());
                        panelArea.addKeyListener(new KeyListener()
                        {
                            public void keyPressed(KeyEvent e)
                            {
                                if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0))
                                {
                                    field1.requestFocus();
                                }

                                BytecodeViewer.checkHotKey(e);
                            }

                            @Override
                            public void keyReleased(KeyEvent arg0)
                            {
                            }

                            @Override
                            public void keyTyped(KeyEvent arg0)
                            {
                            }
                        });
                        scrollPane.setColumnHeaderView(new JLabel("FernFlower Decompiler - Editable: " + panelArea.isEditable()));
                        panelArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));

                        SwingUtilities.invokeLater(new Runnable()
                        {
                            public void run()
                            {
                                panel1.add(scrollPane);
                            }
                        });

                        java1 = panelArea;
                    }

                    if (pane1 == 4) {// bytecode
                        panelArea = new RSyntaxTextArea();
                        panelArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                        panelArea.setCodeFoldingEnabled(true);
                        panelArea.setAntiAliasingEnabled(true);
                        scrollPane = new RTextScrollPane(panelArea);
                        panelArea.setText(Decompiler.bytecode.decompileClassNode(cn, b));
                        panelArea.setCaretPosition(0);
                        panelArea.setEditable(false);
                        panelArea.addKeyListener(new KeyListener()
                        {
                            public void keyPressed(KeyEvent e)
                            {
                                if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0))
                                {
                                    field1.requestFocus();
                                }

                                BytecodeViewer.checkHotKey(e);
                            }

                            @Override
                            public void keyReleased(KeyEvent arg0)
                            {
                            }

                            @Override
                            public void keyTyped(KeyEvent arg0)
                            {
                            }
                        });
                        scrollPane.setColumnHeaderView(new JLabel("Bytecode Decompiler - Editable: " + panelArea.isEditable()));
                        panelArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));

                        SwingUtilities.invokeLater(new Runnable()
                        {
                            public void run()
                            {
                                panel1.add(scrollPane);
                            }
                        });
                    }

                    if (pane1 == 5) {// hex
                        final ClassWriter cw = new ClassWriter(0);
                        cn.accept(cw);
                        final JHexEditor hex = new JHexEditor(cw.toByteArray());
                        hex.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));

                        SwingUtilities.invokeLater(new Runnable()
                        {
                            public void run()
                            {
                                panel1.add(hex);
                            }
                        });
                    }

                    if (pane1 == 6) {//smali bytecode
                        panelArea = new RSyntaxTextArea();
                        panelArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                        panelArea.setCodeFoldingEnabled(true);
                        panelArea.setAntiAliasingEnabled(true);
                        scrollPane = new RTextScrollPane(panelArea);
                        panelArea.setText(Decompiler.smali.decompileClassNode(container, cn, b));
                        panelArea.setCaretPosition(0);
                        panelArea.setEditable(isPanel1Editable());
                        smali1 = panelArea;
                        smali1.addKeyListener(new KeyListener()
                        {
                            public void keyPressed(KeyEvent e)
                            {
                                if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0))
                                {
                                    field1.requestFocus();
                                }

                                BytecodeViewer.checkHotKey(e);
                            }

                            @Override
                            public void keyReleased(KeyEvent arg0)
                            {
                            }

                            @Override
                            public void keyTyped(KeyEvent arg0)
                            {
                            }
                        });
                        scrollPane.setColumnHeaderView(new JLabel("Smali Decompiler - Editable: " + panelArea.isEditable()));
                        panelArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));

                        SwingUtilities.invokeLater(new Runnable()
                        {
                            public void run()
                            {
                                panel1.add(scrollPane);
                            }
                        });
                    }

                    if (pane1 == 7) {// krakatau
                        panelArea = new RSyntaxTextArea();
                        panelArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                        panelArea.setCodeFoldingEnabled(true);
                        panelArea.setAntiAliasingEnabled(true);
                        scrollPane = new RTextScrollPane(panelArea);
                        panelArea.setText(Decompiler.krakatau.decompileClassNode(tempFiles[0], tempFiles[1], cn, b));
                        panelArea.setCaretPosition(0);
                        panelArea.setEditable(isPanel1Editable());
                        panelArea.addKeyListener(new KeyListener()
                        {
                            public void keyPressed(KeyEvent e)
                            {
                                if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0))
                                {
                                    field1.requestFocus();
                                }

                                BytecodeViewer.checkHotKey(e);
                            }

                            @Override
                            public void keyReleased(KeyEvent arg0)
                            {
                            }

                            @Override
                            public void keyTyped(KeyEvent arg0)
                            {
                            }
                        });
                        scrollPane.setColumnHeaderView(new JLabel("Krakatau Decompiler - Editable: " + panelArea.isEditable()));
                        panelArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));

                        SwingUtilities.invokeLater(new Runnable()
                        {
                            public void run()
                            {
                                panel1.add(scrollPane);
                            }
                        });

                        java1 = panelArea;
                    }


                    if (pane1 == 8) {// kraktau bytecode
                        panelArea = new RSyntaxTextArea();
                        panelArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                        panelArea.setCodeFoldingEnabled(true);
                        panelArea.setAntiAliasingEnabled(true);
                        scrollPane = new RTextScrollPane(panelArea);
                        panelArea.setText(Decompiler.krakatauDA.decompileClassNode(tempFiles[0], tempFiles[1], cn, b));
                        panelArea.setCaretPosition(0);
                        panelArea.setEditable(isPanel1Editable());
                        krakatau1 = panelArea;
                        krakatau1.addKeyListener(new KeyListener()
                        {
                            public void keyPressed(KeyEvent e)
                            {
                                if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0))
                                {
                                    field1.requestFocus();
                                }

                                BytecodeViewer.checkHotKey(e);
                            }

                            @Override
                            public void keyReleased(KeyEvent arg0)
                            {
                            }

                            @Override
                            public void keyTyped(KeyEvent arg0)
                            {
                            }
                        });
                        scrollPane.setColumnHeaderView(new JLabel("Krakatau Disassembler - Editable: " + panelArea.isEditable()));
                        panelArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));

                        SwingUtilities.invokeLater(new Runnable()
                        {
                            public void run()
                            {
                                panel1.add(scrollPane);
                            }
                        });
                    }

                    if (pane1 == 9) {// JD-GUI
                        panelArea = new RSyntaxTextArea();
                        panelArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                        panelArea.setCodeFoldingEnabled(true);
                        panelArea.setAntiAliasingEnabled(true);
                        scrollPane = new RTextScrollPane(panelArea);
                        panelArea.setText(Decompiler.jdgui.decompileClassNode(cn, b));
                        panelArea.setCaretPosition(0);
                        panelArea.setEditable(isPanel1Editable());
                        panelArea.addKeyListener(new KeyListener()
                        {
                            public void keyPressed(KeyEvent e)
                            {
                                if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0))
                                {
                                    field1.requestFocus();
                                }

                                BytecodeViewer.checkHotKey(e);
                            }

                            @Override
                            public void keyReleased(KeyEvent arg0)
                            {
                            }

                            @Override
                            public void keyTyped(KeyEvent arg0)
                            {
                            }
                        });
                        scrollPane.setColumnHeaderView(new JLabel("JD-GUI Decompiler - Editable: " + panelArea.isEditable()));
                        panelArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));

                        SwingUtilities.invokeLater(new Runnable()
                        {
                            public void run()
                            {
                                panel1.add(scrollPane);
                            }
                        });

                        java1 = panelArea;
                    }

                    if (pane1 == 10) {// JADX
                        panelArea = new RSyntaxTextArea();
                        panelArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                        panelArea.setCodeFoldingEnabled(true);
                        panelArea.setAntiAliasingEnabled(true);
                        scrollPane = new RTextScrollPane(panelArea);
                        panelArea.setText(Decompiler.jadx.decompileClassNode(cn, b));
                        panelArea.setCaretPosition(0);
                        panelArea.setEditable(isPanel1Editable());
                        panelArea.addKeyListener(new KeyListener()
                        {
                            public void keyPressed(KeyEvent e)
                            {
                                if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0))
                                {
                                    field1.requestFocus();
                                }

                                BytecodeViewer.checkHotKey(e);
                            }

                            @Override
                            public void keyReleased(KeyEvent arg0)
                            {
                            }

                            @Override
                            public void keyTyped(KeyEvent arg0)
                            {
                            }
                        });
                        scrollPane.setColumnHeaderView(new JLabel("JADX Decompiler - Editable: " + panelArea.isEditable()));
                        panelArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));

                        SwingUtilities.invokeLater(new Runnable()
                        {
                            public void run()
                            {
                                panel1.add(scrollPane);
                            }
                        });

                        java1 = panelArea;
                    }

                    if (pane1 == 11) {// asm text
                        panelArea = new RSyntaxTextArea();
                        panelArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                        panelArea.setAntiAliasingEnabled(true);
                        scrollPane = new RTextScrollPane(panelArea);
                        panelArea.setText(Decompiler.textifier.decompileClassNode(cn, b));
                        panelArea.setCaretPosition(0);
                        panelArea.setEditable(false);
                        panelArea.addKeyListener(new KeyListener()
                        {
                            public void keyPressed(KeyEvent e)
                            {
                                if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0))
                                {
                                    field1.requestFocus();
                                }

                                BytecodeViewer.checkHotKey(e);
                            }

                            @Override
                            public void keyReleased(KeyEvent arg0)
                            {
                            }

                            @Override
                            public void keyTyped(KeyEvent arg0)
                            {
                            }
                        });
                        scrollPane.setColumnHeaderView(new JLabel("ASM Textified - Editable: " + panelArea.isEditable()));
                        panelArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));

                        SwingUtilities.invokeLater(new Runnable()
                        {
                            public void run()
                            {
                                panel1.add(scrollPane);
                            }
                        });
                        java1 = panelArea;
                    }

                } catch (java.lang.IndexOutOfBoundsException | java.lang.NullPointerException e) {
                    //ignore
                } catch (Exception e) {
                    new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
                } finally {
                    resetDivider();
                    BytecodeViewer.viewer.setIcon(false);
                    if (button != null)
                        button.setEnabled(true);
                }
            }
        };


        t2 = new PaneUpdaterThread() {
            @Override
            public void doShit() {
                try {
                    paneId = 1;
                    viewer = THIS;
                    decompiler = pane2;

                    BytecodeViewer.viewer.setIcon(true);
                    if (pane2 == 1) {
                        panelArea = new RSyntaxTextArea();
                        panelArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                        panelArea.setCodeFoldingEnabled(true);
                        panelArea.setAntiAliasingEnabled(true);
                        scrollPane = new RTextScrollPane(panelArea);
                        panelArea.setText(Decompiler.procyon.decompileClassNode(cn, b));
                        panelArea.setCaretPosition(0);
                        panelArea.setEditable(isPanel2Editable());
                        panelArea.addKeyListener(new KeyListener() {
                            public void keyPressed(KeyEvent e) {
                                if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                                    field2.requestFocus();
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
                        scrollPane.setColumnHeaderView(new JLabel("Procyon Decompiler - Editable: " + panelArea.isEditable()));
                        panelArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                panel2.add(scrollPane);
                            }
                        });

                        java2 = panelArea;
                    }

                    if (pane2 == 2) {
                        panelArea = new RSyntaxTextArea();
                        panelArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                        panelArea.setCodeFoldingEnabled(true);
                        panelArea.setAntiAliasingEnabled(true);
                        scrollPane = new RTextScrollPane(panelArea);
                        panelArea.setText(Decompiler.cfr.decompileClassNode(cn, b));
                        panelArea.setCaretPosition(0);
                        panelArea.setEditable(isPanel2Editable());
                        panelArea.addKeyListener(new KeyListener() {
                            public void keyPressed(KeyEvent e) {
                                if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                                    field2.requestFocus();
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
                        scrollPane.setColumnHeaderView(new JLabel("CFR Decompiler - Editable: " + panelArea.isEditable()));
                        panelArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                panel2.add(scrollPane);
                            }
                        });

                        java2 = panelArea;
                    }

                    if (pane2 == 3) {
                        panelArea = new RSyntaxTextArea();
                        panelArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                        panelArea.setCodeFoldingEnabled(true);
                        panelArea.setAntiAliasingEnabled(true);
                        scrollPane = new RTextScrollPane(panelArea);
                        panelArea.setText(Decompiler.fernflower.decompileClassNode(cn, b));
                        panelArea.setCaretPosition(0);
                        panelArea.setEditable(isPanel2Editable());
                        panelArea.addKeyListener(new KeyListener() {
                            public void keyPressed(KeyEvent e) {
                                if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                                    field2.requestFocus();
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
                        scrollPane.setColumnHeaderView(new JLabel("FernFlower Decompiler - Editable: " + panelArea.isEditable()));
                        panelArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                panel2.add(scrollPane);
                            }
                        });

                        java2 = panelArea;
                    }

                    if (pane2 == 4) {
                        panelArea = new RSyntaxTextArea();
                        panelArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                        panelArea.setCodeFoldingEnabled(true);
                        panelArea.setAntiAliasingEnabled(true);
                        scrollPane = new RTextScrollPane(panelArea);
                        panelArea.setText(Decompiler.bytecode.decompileClassNode(cn, b));
                        panelArea.setCaretPosition(0);
                        panelArea.setEditable(false);
                        panelArea.addKeyListener(new KeyListener() {
                            public void keyPressed(KeyEvent e) {
                                if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                                    field2.requestFocus();
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
                        scrollPane.setColumnHeaderView(new JLabel("Bytecode Decompiler - Editable: " + panelArea.isEditable()));
                        panelArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                panel2.add(scrollPane);
                            }
                        });

                    }

                    if (pane2 == 5) {
                        final ClassWriter cw = new ClassWriter(0);
                        cn.accept(cw);
                        final JHexEditor hex = new JHexEditor(cw.toByteArray());
                        hex.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                panel2.add(hex);
                            }
                        });
                    }

                    if (pane2 == 6) {
                        panelArea = new RSyntaxTextArea();
                        panelArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                        panelArea.setCodeFoldingEnabled(true);
                        panelArea.setAntiAliasingEnabled(true);
                        scrollPane = new RTextScrollPane(panelArea);
                        panelArea.setText(Decompiler.smali.decompileClassNode(container, cn, b));
                        panelArea.setCaretPosition(0);
                        panelArea.setEditable(isPanel2Editable());
                        smali2 = panelArea;
                        panelArea.addKeyListener(new KeyListener() {
                            public void keyPressed(KeyEvent e) {
                                if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                                    field2.requestFocus();
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
                        scrollPane.setColumnHeaderView(new JLabel("Smali Decompiler - Editable: " + panelArea.isEditable()));
                        panelArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                panel2.add(scrollPane);
                            }
                        });

                    }

                    if (pane2 == 7) {// krakatau
                        panelArea = new RSyntaxTextArea();
                        panelArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                        panelArea.setCodeFoldingEnabled(true);
                        panelArea.setAntiAliasingEnabled(true);
                        scrollPane = new RTextScrollPane(panelArea);
                        panelArea.setText(Decompiler.krakatau.decompileClassNode(tempFiles[0], tempFiles[1], cn, b));
                        panelArea.setCaretPosition(0);
                        panelArea.setEditable(isPanel2Editable());
                        panelArea.addKeyListener(new KeyListener() {
                            public void keyPressed(KeyEvent e) {
                                if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                                    field2.requestFocus();
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
                        scrollPane.setColumnHeaderView(new JLabel("Krakatau Decompiler - Editable: " + panelArea.isEditable()));
                        panelArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                panel2.add(scrollPane);
                            }
                        });

                        java2 = panelArea;
                    }

                    if (pane2 == 8) {// kraktau bytecode
                        panelArea = new RSyntaxTextArea();
                        panelArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                        panelArea.setCodeFoldingEnabled(true);
                        panelArea.setAntiAliasingEnabled(true);
                        scrollPane = new RTextScrollPane(panelArea);
                        panelArea.setText(Decompiler.krakatauDA.decompileClassNode(tempFiles[0], tempFiles[1], cn, b));
                        panelArea.setCaretPosition(0);
                        panelArea.setEditable(isPanel2Editable());
                        krakatau2 = panelArea;
                        krakatau2.addKeyListener(new KeyListener() {
                            public void keyPressed(KeyEvent e) {
                                if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                                    field2.requestFocus();
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
                        scrollPane.setColumnHeaderView(new JLabel("Krakatau Disassembler"));
                        panelArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                panel2.add(scrollPane);
                            }
                        });

                    }

                    if (pane2 == 9) {// JD-GUI
                        panelArea = new RSyntaxTextArea();
                        panelArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                        panelArea.setCodeFoldingEnabled(true);
                        panelArea.setAntiAliasingEnabled(true);
                        scrollPane = new RTextScrollPane(panelArea);
                        panelArea.setText(Decompiler.jdgui.decompileClassNode(cn, b));
                        panelArea.setCaretPosition(0);
                        panelArea.setEditable(isPanel2Editable());
                        panelArea.addKeyListener(new KeyListener() {
                            public void keyPressed(KeyEvent e) {
                                if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                                    field2.requestFocus();
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
                        scrollPane.setColumnHeaderView(new JLabel("JD-GUI Decompiler - Editable: " + panelArea.isEditable()));
                        panelArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                panel2.add(scrollPane);
                            }
                        });

                        java2 = panelArea;
                    }

                    if (pane2 == 10) {// JADX
                        panelArea = new RSyntaxTextArea();
                        panelArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                        panelArea.setCodeFoldingEnabled(true);
                        panelArea.setAntiAliasingEnabled(true);
                        scrollPane = new RTextScrollPane(panelArea);
                        panelArea.setText(Decompiler.jadx.decompileClassNode(cn, b));
                        panelArea.setCaretPosition(0);
                        panelArea.setEditable(isPanel2Editable());
                        panelArea.addKeyListener(new KeyListener()
                        {
                            public void keyPressed(KeyEvent e)
                            {
                                if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0))
                                {
                                    field1.requestFocus();
                                }

                                BytecodeViewer.checkHotKey(e);
                            }

                            @Override
                            public void keyReleased(KeyEvent arg0)
                            {
                            }

                            @Override
                            public void keyTyped(KeyEvent arg0)
                            {
                            }
                        });
                        scrollPane.setColumnHeaderView(new JLabel("JADX Decompiler - Editable: " + panelArea.isEditable()));
                        panelArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));

                        SwingUtilities.invokeLater(new Runnable()
                        {
                            public void run()
                            {
                                panel2.add(scrollPane);
                            }
                        });

                        java2 = panelArea;
                    }

                    if (pane2 == 11) {// asm text
                        panelArea = new RSyntaxTextArea();
                        panelArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                        panelArea.setAntiAliasingEnabled(true);
                        scrollPane = new RTextScrollPane(panelArea);
                        panelArea.setText(Decompiler.textifier.decompileClassNode(cn, b));
                        panelArea.setCaretPosition(0);
                        panelArea.setEditable(false);
                        panelArea.addKeyListener(new KeyListener()
                        {
                            public void keyPressed(KeyEvent e)
                            {
                                if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0))
                                {
                                    field2.requestFocus();
                                }

                                BytecodeViewer.checkHotKey(e);
                            }

                            @Override
                            public void keyReleased(KeyEvent arg0)
                            {
                            }

                            @Override
                            public void keyTyped(KeyEvent arg0)
                            {
                            }
                        });
                        scrollPane.setColumnHeaderView(new JLabel("ASM Textified - Editable: " + panelArea.isEditable()));
                        panelArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));

                        SwingUtilities.invokeLater(new Runnable()
                        {
                            public void run()
                            {
                                panel2.add(scrollPane);
                            }
                        });
                        java2 = panelArea;
                    }
                } catch (java.lang.IndexOutOfBoundsException | java.lang.NullPointerException e) {
                    //ignore
                } catch (Exception e) {
                    new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
                } finally {
                    resetDivider();
                    BytecodeViewer.viewer.setIcon(false);
                    if (button != null)
                        button.setEnabled(true);
                }
            }
        };


        t3 = new PaneUpdaterThread() {
            @Override
            public void doShit() {
                try {
                    paneId = 2;
                    viewer = THIS;
                    decompiler = pane3;

                    BytecodeViewer.viewer.setIcon(true);
                    if (pane3 == 1) {
                        panelArea = new RSyntaxTextArea();
                        panelArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                        panelArea.setCodeFoldingEnabled(true);
                        panelArea.setAntiAliasingEnabled(true);
                        scrollPane = new RTextScrollPane(panelArea);
                        panelArea.setText(Decompiler.procyon.decompileClassNode(cn, b));
                        panelArea.setCaretPosition(0);
                        panelArea.setEditable(isPanel3Editable());
                        panelArea.addKeyListener(new KeyListener() {
                            public void keyPressed(KeyEvent e) {
                                if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                                    field3.requestFocus();
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
                        scrollPane.setColumnHeaderView(new JLabel("Procyon Decompiler - Editable: " + panelArea.isEditable()));
                        panelArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                panel3.add(scrollPane);
                            }
                        });

                        java3 = panelArea;
                    }

                    if (pane3 == 2) {
                        panelArea = new RSyntaxTextArea();
                        panelArea
                                .setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                        panelArea.setCodeFoldingEnabled(true);
                        panelArea.setAntiAliasingEnabled(true);
                        scrollPane = new RTextScrollPane(panelArea);
                        panelArea.setText(Decompiler.cfr.decompileClassNode(cn, b));
                        panelArea.setCaretPosition(0);
                        panelArea.setEditable(isPanel3Editable());
                        panelArea.addKeyListener(new KeyListener() {
                            public void keyPressed(KeyEvent e) {
                                if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                                    field3.requestFocus();
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
                        scrollPane.setColumnHeaderView(new JLabel("CFR Decompiler - Editable: " + panelArea.isEditable()));
                        panelArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                panel3.add(scrollPane);
                            }
                        });

                        java3 = panelArea;
                    }

                    if (pane3 == 3) {
                        panelArea = new RSyntaxTextArea();
                        panelArea
                                .setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                        panelArea.setCodeFoldingEnabled(true);
                        panelArea.setAntiAliasingEnabled(true);
                        scrollPane = new RTextScrollPane(panelArea);
                        panelArea.setText(Decompiler.fernflower.decompileClassNode(cn, b));
                        panelArea.setCaretPosition(0);
                        panelArea.setEditable(isPanel3Editable());
                        panelArea.addKeyListener(new KeyListener() {
                            public void keyPressed(KeyEvent e) {
                                if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                                    field3.requestFocus();
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
                        scrollPane.setColumnHeaderView(new JLabel("FernFlower Decompiler - Editable: " + panelArea.isEditable()));
                        panelArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                panel3.add(scrollPane);
                            }
                        });

                        java3 = panelArea;
                    }

                    if (pane3 == 4) {
                        panelArea = new RSyntaxTextArea();
                        panelArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                        panelArea.setCodeFoldingEnabled(true);
                        panelArea.setAntiAliasingEnabled(true);
                        scrollPane = new RTextScrollPane(panelArea);
                        panelArea.setText(Decompiler.bytecode.decompileClassNode(cn, b));
                        panelArea.setCaretPosition(0);
                        panelArea.setEditable(false);
                        panelArea.addKeyListener(new KeyListener() {
                            public void keyPressed(KeyEvent e) {
                                if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                                    field3.requestFocus();
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
                        scrollPane.setColumnHeaderView(new JLabel("Bytecode Decompiler - Editable: " + panelArea.isEditable()));
                        panelArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                panel3.add(scrollPane);
                            }
                        });
                    }

                    if (pane3 == 5) {
                        final ClassWriter cw = new ClassWriter(0);
                        cn.accept(cw);
                        final JHexEditor hex = new JHexEditor(cw.toByteArray());
                        hex.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                panel3.add(hex);
                            }
                        });

                    }

                    if (pane3 == 6) {
                        panelArea = new RSyntaxTextArea();
                        panelArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                        panelArea.setCodeFoldingEnabled(true);
                        panelArea.setAntiAliasingEnabled(true);
                        scrollPane = new RTextScrollPane(panelArea);
                        panelArea.setText(Decompiler.smali.decompileClassNode(container, cn, b));
                        panelArea.setCaretPosition(0);
                        panelArea.setEditable(isPanel3Editable());
                        smali3 = panelArea;
                        smali3.addKeyListener(new KeyListener() {
                            public void keyPressed(KeyEvent e) {
                                if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                                    field3.requestFocus();
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
                        scrollPane.setColumnHeaderView(new JLabel("Smali Decompiler - Editable: " + panelArea.isEditable()));
                        panelArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                panel3.add(scrollPane);
                            }
                        });

                    }

                    if (pane3 == 7) {// krakatau
                        panelArea = new RSyntaxTextArea();
                        panelArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                        panelArea.setCodeFoldingEnabled(true);
                        panelArea.setAntiAliasingEnabled(true);
                        scrollPane = new RTextScrollPane(panelArea);
                        panelArea.setText(Decompiler.krakatau.decompileClassNode(tempFiles[0], tempFiles[1], cn, b));
                        panelArea.setCaretPosition(0);
                        panelArea.setEditable(isPanel3Editable());
                        panelArea.addKeyListener(new KeyListener() {
                            public void keyPressed(KeyEvent e) {
                                if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                                    field3.requestFocus();
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
                        scrollPane.setColumnHeaderView(new JLabel("Krakatau Decompiler - Editable: " + panelArea.isEditable()));
                        java3 = panelArea;
                        panelArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                panel3.add(scrollPane);
                            }
                        });
                    }

                    if (pane3 == 8) {// kraktau bytecode
                        panelArea = new RSyntaxTextArea();
                        panelArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                        panelArea.setCodeFoldingEnabled(true);
                        panelArea.setAntiAliasingEnabled(true);
                        scrollPane = new RTextScrollPane(panelArea);
                        panelArea.setText(Decompiler.krakatauDA.decompileClassNode(tempFiles[0], tempFiles[1], cn, b));
                        panelArea.setCaretPosition(0);
                        panelArea.setEditable(isPanel3Editable());
                        krakatau3 = panelArea;
                        krakatau3.addKeyListener(new KeyListener() {
                            public void keyPressed(KeyEvent e) {
                                if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                                    field3.requestFocus();
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
                        scrollPane.setColumnHeaderView(new JLabel("Krakatau Disassembler"));
                        panelArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                panel3.add(scrollPane);
                            }
                        });

                    }

                    if (pane3 == 9) {// JD-GUI
                        panelArea = new RSyntaxTextArea();
                        panelArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                        panelArea.setCodeFoldingEnabled(true);
                        panelArea.setAntiAliasingEnabled(true);
                        scrollPane = new RTextScrollPane(panelArea);
                        panelArea.setText(Decompiler.jdgui.decompileClassNode(cn, b));
                        panelArea.setCaretPosition(0);
                        panelArea.setEditable(isPanel3Editable());
                        panelArea.addKeyListener(new KeyListener() {
                            public void keyPressed(KeyEvent e) {
                                if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                                    field3.requestFocus();
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
                        scrollPane.setColumnHeaderView(new JLabel("JD-GUI Decompiler - Editable: " + panelArea.isEditable()));
                        java3 = panelArea;
                        panelArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                panel3.add(scrollPane);
                            }
                        });
                    }

                    if (pane3 == 10) {// JADX
                        panelArea = new RSyntaxTextArea();
                        panelArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                        panelArea.setCodeFoldingEnabled(true);
                        panelArea.setAntiAliasingEnabled(true);
                        scrollPane = new RTextScrollPane(panelArea);
                        panelArea.setText(Decompiler.jadx.decompileClassNode(cn, b));
                        panelArea.setCaretPosition(0);
                        panelArea.setEditable(isPanel3Editable());
                        panelArea.addKeyListener(new KeyListener()
                        {
                            public void keyPressed(KeyEvent e)
                            {
                                if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0))
                                {
                                    field1.requestFocus();
                                }

                                BytecodeViewer.checkHotKey(e);
                            }

                            @Override
                            public void keyReleased(KeyEvent arg0)
                            {
                            }

                            @Override
                            public void keyTyped(KeyEvent arg0)
                            {
                            }
                        });
                        scrollPane.setColumnHeaderView(new JLabel("JADX Decompiler - Editable: " + panelArea.isEditable()));
                        panelArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));

                        SwingUtilities.invokeLater(new Runnable()
                        {
                            public void run()
                            {
                                panel3.add(scrollPane);
                            }
                        });

                        java3 = panelArea;
                    }

                    if (pane3 == 11) {// asm text
                        panelArea = new RSyntaxTextArea();
                        panelArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                        panelArea.setAntiAliasingEnabled(true);
                        scrollPane = new RTextScrollPane(panelArea);
                        panelArea.setText(Decompiler.textifier.decompileClassNode(cn, b));
                        panelArea.setCaretPosition(0);
                        panelArea.setEditable(false);
                        panelArea.addKeyListener(new KeyListener()
                        {
                            public void keyPressed(KeyEvent e)
                            {
                                if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0))
                                {
                                    field3.requestFocus();
                                }

                                BytecodeViewer.checkHotKey(e);
                            }

                            @Override
                            public void keyReleased(KeyEvent arg0)
                            {
                            }

                            @Override
                            public void keyTyped(KeyEvent arg0)
                            {
                            }
                        });
                        scrollPane.setColumnHeaderView(new JLabel("ASM Textified - Editable: " + panelArea.isEditable()));
                        panelArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));

                        SwingUtilities.invokeLater(new Runnable()
                        {
                            public void run()
                            {
                                panel3.add(scrollPane);
                            }
                        });
                        java3 = panelArea;
                    }
                } catch (java.lang.IndexOutOfBoundsException | java.lang.NullPointerException e) {
                    //ignore
                } catch (Exception e) {
                    //new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
                } finally {
                    resetDivider();
                    BytecodeViewer.viewer.setIcon(false);
                    if (button != null)
                        button.setEnabled(true);
                }
            }
        };

        Thread t = new Thread()
        {
            @Override
            public void run()
            {
                BytecodeViewer.viewer.setIcon(true);
                while(BytecodeViewer.currentlyDumping)
                {
                    //wait until it's not dumping
                    try
                    {
                        Thread.sleep(100);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                tempFiles = BytecodeViewer.dumpTempFile(container);

                BytecodeViewer.viewer.setIcon(false);

                if (pane1 > 0)
                    t1.start();
                if (pane2 > 0)
                    t2.start();
                if (pane3 > 0)
                    t3.start();
            }
        };
        t.start();

        if(isPanel1Editable() || isPanel2Editable() || isPanel3Editable())
        {
            if(!BytecodeViewer.warnForEditing)
            {
                BytecodeViewer.warnForEditing = true;
                if(!BytecodeViewer.viewer.autoCompileOnRefresh.isSelected() && !BytecodeViewer.viewer.compileOnSave.isSelected())
                {
                    BytecodeViewer.showMessage("Make sure to compile (File>Compile or Ctrl + T) whenever you want to test or export your changes.\nYou can set compile automatically on refresh or on save in the settings menu.");
                    Settings.saveSettings();
                }
            }
        }
    }

    public Object[] getSmali() {
        if (smali1 != null)
            return new Object[]{cn, smali1.getText()};
        if (smali2 != null)
            return new Object[]{cn, smali2.getText()};
        if (smali3 != null)
            return new Object[]{cn, smali3.getText()};

        return null;
    }

    public Object[] getKrakatau() {
        if (krakatau1 != null)
            return new Object[]{cn, krakatau1.getText()};
        if (krakatau2 != null)
            return new Object[]{cn, krakatau2.getText()};
        if (krakatau3 != null)
            return new Object[]{cn, krakatau3.getText()};

        return null;
    }

    public Object[] getJava() {
        if (java1 != null)
            return new Object[]{cn, java1.getText()};
        if (java2 != null)
            return new Object[]{cn, java2.getText()};
        if (java3 != null)
            return new Object[]{cn, java3.getText()};

        return null;
    }

    public static class MethodData {
        public String name, desc;
        public int srcLN, bytecodeLN;

        @Override
        public boolean equals(final Object o) {
            return equals((MethodData) o);
        }

        public boolean equals(final MethodData md) {
            return this.name.equals(md.name) && this.desc.equals(md.desc);
        }

        public String constructPattern() {
            final StringBuilder pattern = new StringBuilder();
            pattern.append(name + " *\\(");
            final org.objectweb.asm.Type[] types = org.objectweb.asm.Type
                    .getArgumentTypes(desc);
            pattern.append("(.*)");
            for (int i = 0; i < types.length; i++) {
                final Type type = types[i];
                final String clazzName = type.getClassName();
                pattern.append(clazzName.substring(clazzName.lastIndexOf(".") + 1)
                        + "(.*)");
            }
            pattern.append("\\) *\\{");
            return pattern.toString();
        }
    }

    class WrapEditorKit extends StyledEditorKit {
        private static final long serialVersionUID = 1719109651258205346L;
        ViewFactory defaultFactory = new WrapColumnFactory();

        @Override
        public ViewFactory getViewFactory() {
            return defaultFactory;
        }
    }

    class WrapColumnFactory implements ViewFactory {
        public View create(final Element elem) {
            final String kind = elem.getName();
            if (kind != null) {
                if (kind.equals(AbstractDocument.ParagraphElementName))
                    return new NoWrapParagraphView(elem);
                else if (kind.equals(AbstractDocument.SectionElementName))
                    return new BoxView(elem, View.Y_AXIS);
                else if (kind.equals(StyleConstants.ComponentElementName))
                    return new ComponentView(elem);
                else if (kind.equals(StyleConstants.IconElementName))
                    return new IconView(elem);
            }

            return new LabelView(elem);
        }
    }

    public class NoWrapParagraphView extends ParagraphView {
        public NoWrapParagraphView(final Element elem) {
            super(elem);
        }

        @Override
        public void layout(final int width, final int height) {
            super.layout(Short.MAX_VALUE, height);
        }

        @Override
        public float getMinimumSpan(final int axis) {
            return super.getPreferredSpan(axis);
        }
    }


    public void setPanes() {
        if (BytecodeViewer.viewer.panelGroup1.isSelected(BytecodeViewer.viewer.panel1None.getModel()))
            pane1 = 0;
        else if (BytecodeViewer.viewer.panelGroup1.isSelected(BytecodeViewer.viewer.panel1Proc.getModel()))
            pane1 = 1;
        else if (BytecodeViewer.viewer.panelGroup1.isSelected(BytecodeViewer.viewer.panel1CFR.getModel()))
            pane1 = 2;
        else if (BytecodeViewer.viewer.panelGroup1.isSelected(BytecodeViewer.viewer.panel1Fern.getModel()))
            pane1 = 3;
        else if (BytecodeViewer.viewer.panelGroup1.isSelected(BytecodeViewer.viewer.panel1Bytecode.getModel()))
            pane1 = 4;
        else if (BytecodeViewer.viewer.panelGroup1.isSelected(BytecodeViewer.viewer.panel1Hexcode.getModel()))
            pane1 = 5;
        else if (BytecodeViewer.viewer.panelGroup1.isSelected(BytecodeViewer.viewer.panel1Smali.getModel()))
            pane1 = 6;
        else if (BytecodeViewer.viewer.panelGroup1.isSelected(BytecodeViewer.viewer.panel1Krakatau.getModel()))
            pane1 = 7;
        else if (BytecodeViewer.viewer.panelGroup1.isSelected(BytecodeViewer.viewer.panel1KrakatauBytecode.getModel()))
            pane1 = 8;
        else if (BytecodeViewer.viewer.panelGroup1.isSelected(BytecodeViewer.viewer.panel1JDGUI.getModel()))
            pane1 = 9;
        else if (BytecodeViewer.viewer.panelGroup1.isSelected(BytecodeViewer.viewer.jadxJ1.getModel()))
            pane1 = 10;
        else if (BytecodeViewer.viewer.panelGroup1.isSelected(BytecodeViewer.viewer.asmText1.getModel()))
            pane1 = 11;

        if (BytecodeViewer.viewer.panelGroup2.isSelected(BytecodeViewer.viewer.panel2None.getModel()))
            pane2 = 0;
        else if (BytecodeViewer.viewer.panelGroup2.isSelected(BytecodeViewer.viewer.panel2Proc.getModel()))
            pane2 = 1;
        else if (BytecodeViewer.viewer.panelGroup2.isSelected(BytecodeViewer.viewer.panel2CFR.getModel()))
            pane2 = 2;
        else if (BytecodeViewer.viewer.panelGroup2.isSelected(BytecodeViewer.viewer.panel2Fern.getModel()))
            pane2 = 3;
        else if (BytecodeViewer.viewer.panelGroup2.isSelected(BytecodeViewer.viewer.panel2Bytecode.getModel()))
            pane2 = 4;
        else if (BytecodeViewer.viewer.panelGroup2.isSelected(BytecodeViewer.viewer.panel2Hexcode.getModel()))
            pane2 = 5;
        else if (BytecodeViewer.viewer.panelGroup2.isSelected(BytecodeViewer.viewer.panel2Smali.getModel()))
            pane2 = 6;
        else if (BytecodeViewer.viewer.panelGroup2.isSelected(BytecodeViewer.viewer.panel2Krakatau.getModel()))
            pane2 = 7;
        else if (BytecodeViewer.viewer.panelGroup2.isSelected(BytecodeViewer.viewer.panel2KrakatauBytecode.getModel()))
            pane2 = 8;
        else if (BytecodeViewer.viewer.panelGroup2.isSelected(BytecodeViewer.viewer.panel2JDGUI.getModel()))
            pane2 = 9;
        else if (BytecodeViewer.viewer.panelGroup2.isSelected(BytecodeViewer.viewer.jadxJ2.getModel()))
            pane2 = 10;
        else if (BytecodeViewer.viewer.panelGroup2.isSelected(BytecodeViewer.viewer.asmText2.getModel()))
            pane2 = 11;

        if (BytecodeViewer.viewer.panelGroup3.isSelected(BytecodeViewer.viewer.panel3None.getModel()))
            pane3 = 0;
        else if (BytecodeViewer.viewer.panelGroup3.isSelected(BytecodeViewer.viewer.panel3Proc.getModel()))
            pane3 = 1;
        else if (BytecodeViewer.viewer.panelGroup3.isSelected(BytecodeViewer.viewer.panel3CFR.getModel()))
            pane3 = 2;
        else if (BytecodeViewer.viewer.panelGroup3.isSelected(BytecodeViewer.viewer.panel3Fern.getModel()))
            pane3 = 3;
        else if (BytecodeViewer.viewer.panelGroup3.isSelected(BytecodeViewer.viewer.panel3Bytecode.getModel()))
            pane3 = 4;
        else if (BytecodeViewer.viewer.panelGroup3.isSelected(BytecodeViewer.viewer.panel3Hexcode.getModel()))
            pane3 = 5;
        else if (BytecodeViewer.viewer.panelGroup3.isSelected(BytecodeViewer.viewer.panel3Smali.getModel()))
            pane3 = 6;
        else if (BytecodeViewer.viewer.panelGroup3.isSelected(BytecodeViewer.viewer.panel3Krakatau.getModel()))
            pane3 = 7;
        else if (BytecodeViewer.viewer.panelGroup3.isSelected(BytecodeViewer.viewer.panel3KrakatauBytecode.getModel()))
            pane3 = 8;
        else if (BytecodeViewer.viewer.panelGroup3.isSelected(BytecodeViewer.viewer.panel3JDGUI.getModel()))
            pane3 = 9;
        else if (BytecodeViewer.viewer.panelGroup3.isSelected(BytecodeViewer.viewer.jadxJ3.getModel()))
            pane3 = 10;
        else if (BytecodeViewer.viewer.panelGroup3.isSelected(BytecodeViewer.viewer.asmText3.getModel()))
            pane3 = 11;
    }

    public boolean isPanel1Editable()
    {
        setPanes();

        if (pane1 == 1 && BytecodeViewer.viewer.panel1Proc_E.isSelected())
            return true;
        if (pane1 == 2 && BytecodeViewer.viewer.panel1CFR_E.isSelected())
            return true;
        if (pane1 == 3 && BytecodeViewer.viewer.panel1Fern_E.isSelected())
            return true;
        if (pane1 == 6 && BytecodeViewer.viewer.panel1Smali_E.isSelected())
            return true;
        if (pane1 == 9 && BytecodeViewer.viewer.panel1JDGUI_E.isSelected())
            return true;
        if ((pane1 == 7 || pane1 == 8) && BytecodeViewer.viewer.panel1Krakatau_E.isSelected())
            return true;


        return false;
    }

    public boolean isPanel2Editable() {
        setPanes();

        if (pane2 == 1 && BytecodeViewer.viewer.panel2Proc_E.isSelected())
            return true;
        if (pane2 == 2 && BytecodeViewer.viewer.panel2CFR_E.isSelected())
            return true;
        if (pane2 == 3 && BytecodeViewer.viewer.panel2Fern_E.isSelected())
            return true;
        if (pane2 == 6 && BytecodeViewer.viewer.panel2Smali_E.isSelected())
            return true;
        if (pane2 == 9 && BytecodeViewer.viewer.panel2JDGUI_E.isSelected())
            return true;
        if ((pane2 == 7 || pane2 == 8) && BytecodeViewer.viewer.panel2Krakatau_E.isSelected())
            return true;


        return false;
    }

    public boolean isPanel3Editable() {
        setPanes();

        if (pane3 == 1 && BytecodeViewer.viewer.panel3Proc_E.isSelected())
            return true;
        if (pane3 == 2 && BytecodeViewer.viewer.panel3CFR_E.isSelected())
            return true;
        if (pane3 == 3 && BytecodeViewer.viewer.panel3Fern_E.isSelected())
            return true;
        if (pane3 == 6 && BytecodeViewer.viewer.panel3Smali_E.isSelected())
            return true;
        if (pane3 == 9 && BytecodeViewer.viewer.panel3JDGUI_E.isSelected())
            return true;
        if ((pane3 == 7 || pane3 == 8) && BytecodeViewer.viewer.panel3Krakatau_E.isSelected())
            return true;


        return false;
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





    public static void selectMethod(RSyntaxTextArea area, int methodLine) {
        if (methodLine != area.getCaretLineNumber()) {
            setCaretLine(area, methodLine);
            setViewLine(area, methodLine);
        }
    }

    public static void selectMethod(ClassViewer classViewer, int paneId, Method method) {
        RSyntaxTextArea area = null;
        switch(paneId)
        {
            case 0:
                area = classViewer.t1.panelArea;
                break;
            case 1:
                area = classViewer.t2.panelArea;
                break;
            case 2:
                area = classViewer.t3.panelArea;
                break;
        }

        if (area != null)
        {
            MethodParser methods = classViewer.methods.get(paneId);
            if (methods != null) {
                int methodLine = methods.findMethod(method);
                if (methodLine != -1) {
                    selectMethod(area, methodLine);
                }
            }
        }
    }

    public static String getLineText(RSyntaxTextArea area, int line) {
        try {
            if (line < area.getLineCount()) {
                int start = area.getLineStartOffset(line);
                int end = area.getLineEndOffset(line);
                return area.getText(start, end - start).trim();
            }
        } catch (BadLocationException ignored) {
        }
        return "";
    }

    public static int getMaxViewLine(RSyntaxTextArea area) {
        Container parent = area.getParent();
        if (parent instanceof JViewport) {
            JViewport viewport = (JViewport) parent;
            int y = viewport.getViewSize().height - viewport.getExtentSize().height;
            int lineHeight = area.getLineHeight();
            return y >= lineHeight ? y / lineHeight : 0;
        }
        return 0;
    }

    public static int getViewLine(RSyntaxTextArea area) {
        Container parent = area.getParent();
        if (parent instanceof JViewport) {
            JViewport viewport = (JViewport) parent;
            Point point = viewport.getViewPosition();
            int lineHeight = area.getLineHeight();
            return point.y >= lineHeight ? point.y / lineHeight : 0;
        }
        return 0;
    }

    public static void setViewLine(RSyntaxTextArea area, int line) {
        Container parent = area.getParent();
        if (parent instanceof JViewport) {
            JViewport viewport = (JViewport) parent;
            int maxLine = ClassViewer.getMaxViewLine(area);
            line = line < maxLine ? line : maxLine;
            viewport.setViewPosition(new Point(0, line * area.getLineHeight()));
        }
    }

    public static void setCaretLine(RSyntaxTextArea area, int line) {
        try {
            area.setCaretPosition(area.getLineStartOffset(line));
        } catch (BadLocationException ignored) {}
    }
}
