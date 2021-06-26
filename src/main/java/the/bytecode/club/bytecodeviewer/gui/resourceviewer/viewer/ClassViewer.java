package the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer;

import the.bytecode.club.bytecodeviewer.gui.resourceviewer.ResourcePanelCompileMode;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.ResourceViewPanel;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.TabbedPane;
import the.bytecode.club.bytecodeviewer.gui.hexviewer.JHexEditor;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.Settings;
import the.bytecode.club.bytecodeviewer.util.FileContainer;
import the.bytecode.club.bytecodeviewer.util.MethodParser;

import static the.bytecode.club.bytecodeviewer.util.MethodParser.Method;

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

public class ClassViewer extends ResourceViewer
{
    public JSplitPane sp;
    public JSplitPane sp2;
    public ResourceViewPanel resourceViewPanel1 = new ResourceViewPanel(0);
    public ResourceViewPanel resourceViewPanel2 = new ResourceViewPanel(1);
    public ResourceViewPanel resourceViewPanel3 = new ResourceViewPanel(2);
    
    public File[] tempFiles;
    public ClassViewer THIS = this;
    public List<MethodParser> methods = Arrays.asList(new MethodParser(), new MethodParser(), new MethodParser());
    public final String workingName;
    
    public ClassViewer(final FileContainer container, final String name, final ClassNode cn, String workingName)
    {
        this.workingName = workingName;
        this.container = container;
        
        this.name = name;
        this.cn = cn;
        this.setName(name);
        this.setLayout(new BorderLayout());

        this.sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, resourceViewPanel1.panel, resourceViewPanel2.panel);
        final ClassWriter cw = new ClassWriter(0);
        cn.accept(cw);
        JHexEditor hex = new JHexEditor(cw.toByteArray());
        this.sp2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sp, resourceViewPanel3.panel);
        this.add(sp2, BorderLayout.CENTER);

        hex.setMaximumSize(new Dimension(0, Integer.MAX_VALUE));
        hex.setSize(0, Integer.MAX_VALUE);

        startPaneUpdater(null);
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resetDivider();
            }
        });
    }

    public void resetDivider()
    {
        SwingUtilities.invokeLater(() ->
        {
            sp.setResizeWeight(0.5);
            
            if (resourceViewPanel2.decompilerViewIndex != 0 && resourceViewPanel1.decompilerViewIndex != 0) {
                setDividerLocation(sp, 0.5);
            } else if (resourceViewPanel1.decompilerViewIndex != 0) {
                setDividerLocation(sp, 1);
            } else if (resourceViewPanel2.decompilerViewIndex != 0) {
                sp.setResizeWeight(1);
                setDividerLocation(sp, 0);
            } else {
                setDividerLocation(sp, 0);
            }
            
            if (resourceViewPanel3.decompilerViewIndex != 0) {
                sp2.setResizeWeight(0.7);
                setDividerLocation(sp2, 0.7);
                if ((resourceViewPanel2.decompilerViewIndex == 0 && resourceViewPanel1.decompilerViewIndex != 0)
                        || (resourceViewPanel1.decompilerViewIndex == 0 && resourceViewPanel2.decompilerViewIndex != 0)) {
                    setDividerLocation(sp2, 0.5);
                } else if (resourceViewPanel1.decompilerViewIndex == 0) {
                    setDividerLocation(sp2, 0);
                }
            } else {
                sp.setResizeWeight(1);
                sp2.setResizeWeight(0);
                setDividerLocation(sp2, 1);
            }
        });
    }

    public void startPaneUpdater(final JButton button)
    {
        this.cn = BytecodeViewer.getClassNode(container, cn.name); //update the classnode
        setPanes();
        
        refreshTitle();
        
        resourceViewPanel1.createPane(this);
        resourceViewPanel2.createPane(this);
        resourceViewPanel3.createPane(this);

        final ClassWriter cw = new ClassWriter(0);
        try {
            cn.accept(cw);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Thread.sleep(200);
                cn.accept(cw);
            } catch (InterruptedException ignored) { }
        }

        final byte[] b = cw.toByteArray();
        resourceViewPanel1.updatePane(this, b, button, isPanel1Editable());
        resourceViewPanel2.updatePane(this, b, button, isPanel2Editable());
        resourceViewPanel3.updatePane(this, b, button, isPanel3Editable());

        Thread t = new Thread(() ->
        {
            BytecodeViewer.viewer.updateBusyStatus(true);
            while (Configuration.currentlyDumping)
            {
                //wait until it's not dumping
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            tempFiles = BytecodeViewer.dumpTempFile(container);

            BytecodeViewer.viewer.updateBusyStatus(false);

            if (resourceViewPanel1.decompilerViewIndex > 0)
                resourceViewPanel1.updateThread.startNewThread();
            if (resourceViewPanel2.decompilerViewIndex > 0)
                resourceViewPanel2.updateThread.startNewThread();
            if (resourceViewPanel3.decompilerViewIndex > 0)
                resourceViewPanel3.updateThread.startNewThread();
        });
        t.start();

        if (isPanel1Editable() || isPanel2Editable() || isPanel3Editable())
        {
            if (Configuration.warnForEditing)
                return;
    
            Configuration.warnForEditing = true;
            if (!BytecodeViewer.viewer.autoCompileOnRefresh.isSelected()
                    && !BytecodeViewer.viewer.compileOnSave.isSelected())
            {
                BytecodeViewer.showMessage("Make sure to compile (File>Compile or Ctrl + T) whenever you want to "
                        + "test or export your changes.\nYou can set compile automatically on refresh or on save "
                        + "in the settings menu.");
        
                Settings.saveSettings();
            }
        }
    }
    
    @Override
    public void refreshTitle()
    {
        if(tabbedPane != null)
            tabbedPane.label.setText(getTabName());
    }
    
    public Object[] getSmali()
    {
        if (resourceViewPanel1.compileMode == ResourcePanelCompileMode.SMALI_ASSEMBLY)
            return new Object[]{cn, resourceViewPanel1.textArea.getText()};
        if (resourceViewPanel2.compileMode == ResourcePanelCompileMode.SMALI_ASSEMBLY)
            return new Object[]{cn, resourceViewPanel2.textArea.getText()};
        if (resourceViewPanel3.compileMode == ResourcePanelCompileMode.SMALI_ASSEMBLY)
            return new Object[]{cn, resourceViewPanel3.textArea.getText()};

        return null;
    }

    public Object[] getKrakatau()
    {
        if (resourceViewPanel1.compileMode == ResourcePanelCompileMode.KRAKATAU_ASSEMBLY)
            return new Object[]{cn, resourceViewPanel1.textArea.getText()};
        if (resourceViewPanel2.compileMode == ResourcePanelCompileMode.KRAKATAU_ASSEMBLY)
            return new Object[]{cn, resourceViewPanel2.textArea.getText()};
        if (resourceViewPanel3.compileMode == ResourcePanelCompileMode.KRAKATAU_ASSEMBLY)
            return new Object[]{cn, resourceViewPanel3.textArea.getText()};

        return null;
    }

    public Object[] getJava()
    {
        if (resourceViewPanel1.textArea != null)
            return new Object[]{cn, resourceViewPanel1.textArea.getText()};
        if (resourceViewPanel2.textArea != null)
            return new Object[]{cn, resourceViewPanel2.textArea.getText()};
        if (resourceViewPanel3.textArea != null)
            return new Object[]{cn, resourceViewPanel3.textArea.getText()};

        return null;
    }
    
    public void setPanes() {
        resourceViewPanel1.decompilerViewIndex = BytecodeViewer.viewer.viewPane1.getSelectedViewer();
        resourceViewPanel2.decompilerViewIndex = BytecodeViewer.viewer.viewPane2.getSelectedViewer();
        resourceViewPanel3.decompilerViewIndex = BytecodeViewer.viewer.viewPane3.getSelectedViewer();
    }

    public boolean isPanel1Editable() {
        setPanes();
        return BytecodeViewer.viewer.viewPane1.isPaneEditable();
    }

    public boolean isPanel2Editable() {
        setPanes();
        return BytecodeViewer.viewer.viewPane2.isPaneEditable();
    }

    public boolean isPanel3Editable() {
        setPanes();
        return BytecodeViewer.viewer.viewPane3.isPaneEditable();
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
        switch (paneId) {
            case 0:
                area = classViewer.resourceViewPanel1.updateThread.updateUpdaterTextArea;
                break;
            case 1:
                area = classViewer.resourceViewPanel2.updateThread.updateUpdaterTextArea;
                break;
            case 2:
                area = classViewer.resourceViewPanel3.updateThread.updateUpdaterTextArea;
                break;
        }

        if (area != null) {
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
        } catch (BadLocationException ignored) { }
        return "";
    }

    public static int getMaxViewLine(RSyntaxTextArea area)
    {
        Container parent = area.getParent();
        if (parent instanceof JViewport)
        {
            JViewport viewport = (JViewport) parent;
            int y = viewport.getViewSize().height - viewport.getExtentSize().height;
            int lineHeight = area.getLineHeight();
            return y >= lineHeight ? y / lineHeight : 0;
        }
        
        return 0;
    }

    public static int getViewLine(RSyntaxTextArea area)
    {
        Container parent = area.getParent();
        if (parent instanceof JViewport)
        {
            JViewport viewport = (JViewport) parent;
            Point point = viewport.getViewPosition();
            int lineHeight = area.getLineHeight();
            return point.y >= lineHeight ? point.y / lineHeight : 0;
        }
        
        return 0;
    }

    public static void setViewLine(RSyntaxTextArea area, int line)
    {
        Container parent = area.getParent();
        if (parent instanceof JViewport)
        {
            JViewport viewport = (JViewport) parent;
            int maxLine = ClassViewer.getMaxViewLine(area);
            line = Math.min(line, maxLine);
            viewport.setViewPosition(new Point(0, line * area.getLineHeight()));
        }
    }

    public static void setCaretLine(RSyntaxTextArea area, int line)
    {
        try {
            area.setCaretPosition(area.getLineStartOffset(line));
        } catch (BadLocationException ignored) { }
    }
    
    private static final long serialVersionUID = -8650495368920680024L;
}
