package the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.fife.ui.rtextarea.RTextScrollPane;
import org.imgscalr.Scalr;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.Resources;
import the.bytecode.club.bytecodeviewer.gui.components.ImageJLabel;
import the.bytecode.club.bytecodeviewer.gui.components.SearchableRSyntaxTextArea;
import the.bytecode.club.bytecodeviewer.gui.components.listeners.PressKeyListener;
import the.bytecode.club.bytecodeviewer.gui.components.listeners.ReleaseKeyListener;
import the.bytecode.club.bytecodeviewer.gui.hexviewer.JHexEditor;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.TabbedPane;
import the.bytecode.club.bytecodeviewer.util.FileContainer;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;
import the.bytecode.club.bytecodeviewer.util.SyntaxLanguage;

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

public class FileViewer extends ResourceViewer
{
    public final byte[] contents;
    public final String workingName;
    
    public final SearchableRSyntaxTextArea textArea = (SearchableRSyntaxTextArea)
            Configuration.rstaTheme.apply(new SearchableRSyntaxTextArea());
    
    public final JPanel mainPanel = new JPanel(new BorderLayout());
    public BufferedImage image;
    public boolean canRefresh;

    public FileViewer(final FileContainer container, final String name, final byte[] contents, String workingName)
    {
        this.name = name;
        this.contents = contents;
        this.workingName = workingName;
        this.container = container;
        this.setName(name);
        this.setLayout(new BorderLayout());
        
        this.add(mainPanel, BorderLayout.CENTER);

        setContents();
    }
    
    public void setContents()
    {
        final String nameLowerCase = this.name.toLowerCase();
        final String contentsAsString = new String(contents);
        
        //image viewer
        if (!MiscUtils.isPureAscii(contentsAsString))
        {
            //TODO webp?
            if (nameLowerCase.endsWith(".png") || nameLowerCase.endsWith(".jpg") || nameLowerCase.endsWith(".jpeg") ||
                    nameLowerCase.endsWith(".gif") || nameLowerCase.endsWith(".tif") || nameLowerCase.endsWith(".bmp"))
            {
                canRefresh = true;
                
                image = MiscUtils.loadImage(image, contents); //gifs fail because of this
                
                mainPanel.add(new ImageJLabel(image), BorderLayout.CENTER);
                mainPanel.addMouseWheelListener(e ->
                {
                    int notches = e.getWheelRotation();
                    
                    if (notches < 0) //zoom in
                        image = Scalr.resize(image, Scalr.Method.SPEED, image.getWidth() + 10,
                                image.getHeight() + 10);
                    else //zoom out
                        image = Scalr.resize(image, Scalr.Method.SPEED, image.getWidth() - 10,
                                image.getHeight() - 10);
                    
                    mainPanel.removeAll();
                    mainPanel.add(new ImageJLabel(image), BorderLayout.CENTER);
                    mainPanel.updateUI();
                });
                return;
            }
            //hex viewer
            else if (BytecodeViewer.viewer.forcePureAsciiAsText.isSelected())
            {
                JHexEditor hex = new JHexEditor(contents);
                mainPanel.add(hex);
                return;
            }
        }
        
        textArea.setCodeFoldingEnabled(true);
        textArea.setSyntaxEditingStyle(SyntaxLanguage.detectLanguage(nameLowerCase, contentsAsString).getSyntaxConstant());
        textArea.setText(contentsAsString);
        textArea.setCaretPosition(0);
        
        mainPanel.add(textArea.getScrollPane());
    }
    
    @Override
    public void refreshTitle()
    {
        if(tabbedPane != null)
            tabbedPane.label.setText(getTabName());
    }
    
    public void refresh(JButton src)
    {
        refreshTitle();
        
        if (!canRefresh)
        {
            src.setEnabled(true);
            return;
        }

        mainPanel.removeAll();
      
        image = MiscUtils.loadImage(image, contents);
        
        JLabel label = new JLabel("", new ImageIcon(image), JLabel.CENTER);
        mainPanel.add(label, BorderLayout.CENTER);
        mainPanel.updateUI();

        src.setEnabled(true);
    }
    
    private static final long serialVersionUID = 6103372882168257164L;
}
