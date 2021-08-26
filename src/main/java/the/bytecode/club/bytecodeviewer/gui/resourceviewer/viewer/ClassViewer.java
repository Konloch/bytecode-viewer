package the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.Arrays;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.SettingsSerializer;
import the.bytecode.club.bytecodeviewer.api.ASMUtil;
import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.BytecodeViewPanel;
import the.bytecode.club.bytecodeviewer.resources.Resource;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainer;
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
 * @since 09/26/2011
 */

public class ClassViewer extends ResourceViewer
{
    public JSplitPane sp;
    public JSplitPane sp2;
    public BytecodeViewPanel bytecodeViewPanel1 = new BytecodeViewPanel(0, this);
    public BytecodeViewPanel bytecodeViewPanel2 = new BytecodeViewPanel(1, this);
    public BytecodeViewPanel bytecodeViewPanel3 = new BytecodeViewPanel(2, this);
    
    public List<MethodParser> methods = Arrays.asList(new MethodParser(), new MethodParser(), new MethodParser());
    
    public ClassViewer(final ResourceContainer container, final String name)
    {
        super(new Resource(name, container.getWorkingName(name), container));
        
        this.setName(name);
        this.setLayout(new BorderLayout());
        this.sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, bytecodeViewPanel1, bytecodeViewPanel2);
        this.sp2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sp, bytecodeViewPanel3);
        this.add(sp2, BorderLayout.CENTER);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resetDivider();
            }
        });
    }

    @Override
    public void refresh(final JButton button)
    {
        setPanes();
        refreshTitle();

        bytecodeViewPanel1.createPane(this);
        bytecodeViewPanel2.createPane(this);
        bytecodeViewPanel3.createPane(this);

        byte[] classBytes = getResourceBytes();
        
        //TODO remove this once all of the importers have been properly updated to use a FileContainerImporter
        if(classBytes == null || classBytes.length == 0 || Configuration.forceResourceUpdateFromClassNode)
        {
            //TODO remove this error message when all of the importers have been updated
            // only APK and DEX are left
            if(!Configuration.forceResourceUpdateFromClassNode)
            {
                System.err.println("WARNING: Class Resource imported using the old importer!");
                System.err.println("TODO: Update it to use the FileContainerImporter");
            }
            
            classBytes = ASMUtil.nodeToBytes(resource.getResourceClassNode());
        }
        
        bytecodeViewPanel1.updatePane(this, classBytes, button, isPanel1Editable());
        bytecodeViewPanel2.updatePane(this, classBytes, button, isPanel2Editable());
        bytecodeViewPanel3.updatePane(this, classBytes, button, isPanel3Editable());

        Thread dumpBuild = new Thread(() ->
        {
            BytecodeViewer.updateBusyStatus(true);
            
            while (Configuration.currentlyDumping)
            {
                //wait until it's not dumping
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            BytecodeViewer.updateBusyStatus(false);

            if (bytecodeViewPanel1.decompiler != Decompiler.NONE)
                bytecodeViewPanel1.updateThread.startNewThread();
            if (bytecodeViewPanel2.decompiler != Decompiler.NONE)
                bytecodeViewPanel2.updateThread.startNewThread();
            if (bytecodeViewPanel3.decompiler != Decompiler.NONE)
                bytecodeViewPanel3.updateThread.startNewThread();
        }, "ClassViewer Temp Dump");
        dumpBuild.start();

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
        
                SettingsSerializer.saveSettingsAsync();
            }
        }
    }
    
    public void setPanes() {
        bytecodeViewPanel1.decompiler = BytecodeViewer.viewer.viewPane1.getSelectedDecompiler();
        bytecodeViewPanel2.decompiler = BytecodeViewer.viewer.viewPane2.getSelectedDecompiler();
        bytecodeViewPanel3.decompiler = BytecodeViewer.viewer.viewPane3.getSelectedDecompiler();
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
                area = classViewer.bytecodeViewPanel1.updateThread.updateUpdaterTextArea;
                break;
            case 1:
                area = classViewer.bytecodeViewPanel2.updateThread.updateUpdaterTextArea;
                break;
            case 2:
                area = classViewer.bytecodeViewPanel3.updateThread.updateUpdaterTextArea;
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
    
    public void resetDivider()
    {
        SwingUtilities.invokeLater(() ->
        {
            sp.setResizeWeight(0.5);
            
            if (bytecodeViewPanel2.decompiler != Decompiler.NONE && bytecodeViewPanel1.decompiler != Decompiler.NONE) {
                setDividerLocation(sp, 0.5);
            } else if (bytecodeViewPanel1.decompiler != Decompiler.NONE) {
                setDividerLocation(sp, 1);
            } else if (bytecodeViewPanel2.decompiler != Decompiler.NONE) {
                sp.setResizeWeight(1);
                setDividerLocation(sp, 0);
            } else {
                setDividerLocation(sp, 0);
            }
            
            if (bytecodeViewPanel3.decompiler != Decompiler.NONE) {
                sp2.setResizeWeight(0.7);
                setDividerLocation(sp2, 0.7);
                if ((bytecodeViewPanel2.decompiler == Decompiler.NONE && bytecodeViewPanel1.decompiler != Decompiler.NONE)
                        || (bytecodeViewPanel1.decompiler == Decompiler.NONE && bytecodeViewPanel2.decompiler != Decompiler.NONE)) {
                    setDividerLocation(sp2, 0.5);
                } else if (bytecodeViewPanel1.decompiler == Decompiler.NONE) {
                    setDividerLocation(sp2, 0);
                }
            } else {
                sp.setResizeWeight(1);
                sp2.setResizeWeight(0);
                setDividerLocation(sp2, 1);
            }
        });
    }
    
    /**
     * Whoever wrote this function, THANK YOU!
     */
    public static JSplitPane setDividerLocation(final JSplitPane splitter,
                                                final double proportion)
    {
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
    
    private static final long serialVersionUID = -8650495368920680024L;
}
