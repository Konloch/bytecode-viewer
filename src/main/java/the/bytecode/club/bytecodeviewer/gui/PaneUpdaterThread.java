package the.bytecode.club.bytecodeviewer.gui;

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

import com.jhe.hexed.JHexEditor;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.api.ExceptionUI;
import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Updates a pane
 * 
 * @author Konloch
 */
public class PaneUpdaterThread extends Thread {

	private Decompiler decompiler;
	private int paneId;
	private JPanel target;
	private ClassViewer viewer;
    private JButton button;

	public PaneUpdaterThread(ClassViewer viewer, Decompiler decompiler, int paneId, JPanel target, JButton button) {
		this.decompiler = decompiler;
		this.paneId = paneId;
		this.target = target;
		this.viewer = viewer;
        this.button = button;
	}

	public void run() {
		try {
			final byte[] b = BytecodeViewer.getClassBytes(viewer.container, viewer.cn.name + ".class");
            if (decompiler != Decompiler.HEXCODE) {
                RSyntaxTextArea panelArea = new RSyntaxTextArea();
                panelArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                panelArea.setCodeFoldingEnabled(true);
                panelArea.setAntiAliasingEnabled(true);
                final RTextScrollPane scrollPane = new RTextScrollPane(panelArea);
                panelArea.setText(decompiler.decompileClassNode(viewer.cn, b));
                panelArea.setCaretPosition(0);
                panelArea.setEditable(viewer.isPaneEditable(paneId));
                panelArea.addKeyListener(new KeyListener() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                            viewer.requestFocus(paneId);
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
                scrollPane.setColumnHeaderView(new JLabel(decompiler.getName() + " Decompiler - Editable: " + panelArea.isEditable()));
                panelArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        target.add(scrollPane);
                    }
                });
                viewer.updatePane(paneId, panelArea, decompiler);
            } else {
                final JHexEditor hex = new JHexEditor(b);
                hex.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int)BytecodeViewer.viewer.fontSpinner.getValue()));
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        target.add(hex);
                    }
                });
            }
		} catch(Exception e) {
			new ExceptionUI(e);
		} finally {
			viewer.resetDivider();
			BytecodeViewer.viewer.setIcon(false);
			if(button != null)
				button.setEnabled(true);
		}
	}
}