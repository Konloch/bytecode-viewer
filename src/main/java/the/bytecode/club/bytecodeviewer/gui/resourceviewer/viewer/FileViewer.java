package the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;
import the.bytecode.club.bytecodeviewer.gui.components.ImageJLabel;
import the.bytecode.club.bytecodeviewer.gui.components.SearchableRSyntaxTextArea;
import the.bytecode.club.bytecodeviewer.gui.hexviewer.HexViewer;
import the.bytecode.club.bytecodeviewer.resources.Resource;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainer;
import the.bytecode.club.bytecodeviewer.resources.ResourceType;
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
 * Represents any open non-class file inside of a tab.
 *
 * @author Konloch
 */

public class FileViewer extends ResourceViewer
{
    public static final float ZOOM_STEP_SIZE = 1.5f;
    public final SearchableRSyntaxTextArea textArea = (SearchableRSyntaxTextArea)
            Configuration.rstaTheme.apply(new SearchableRSyntaxTextArea());
    public final JPanel mainPanel = new JPanel(new BorderLayout());
    public BufferedImage originalImage;
    public BufferedImage image;
    public boolean canRefresh;
    public int zoomSteps = 0;

    public FileViewer(final ResourceContainer container, final String name)
    {
        super(new Resource(name, container.getWorkingName(name), container));
        
        this.setName(name);
        this.setLayout(new BorderLayout());
        
        this.add(mainPanel, BorderLayout.CENTER);

        setContents();
    }
    
    public void setContents()
    {
        final byte[] contents = resource.getResourceBytes();
        final String nameLowerCase = this.resource.name.toLowerCase();
        final String onlyName = FilenameUtils.getName(nameLowerCase);
        final boolean hexViewerOnly = BytecodeViewer.viewer.viewPane1.getSelectedDecompiler() == Decompiler.HEXCODE_VIEWER &&
                BytecodeViewer.viewer.viewPane2.getSelectedDecompiler() == Decompiler.NONE &&
                BytecodeViewer.viewer.viewPane3.getSelectedDecompiler() == Decompiler.NONE;
        
        //image viewer
        if (MiscUtils.guessIfBinary(contents) || hexViewerOnly)
        {
            //TODO:
            //  + Add file header checks
            //  + Check for CAFEBABE
            //  + ClassRead then quick-decompile using Pane1 Decompiler
            //      (If none selected, try Pane2, Pane3, default to Procyon)
            
            
            //check by file extension to display image
            if (!onlyName.contains(":") &&
                    ResourceType.imageExtensionMap.containsKey(FilenameUtils.getExtension(onlyName)) &&
                    !hexViewerOnly)
            {
                canRefresh = true;

                image = MiscUtils.loadImage(image, contents);
                if (image == null) {
                    HexViewer hex = new HexViewer(contents);
                    mainPanel.add(hex);
                    return;
                }
                originalImage = image;

                mainPanel.add(new ImageJLabel(image), BorderLayout.CENTER);
                mainPanel.addMouseWheelListener(e -> {
                    int notches = e.getWheelRotation();
                    int width = originalImage.getWidth();
                    int height = originalImage.getHeight();
                    int oldZoomSteps = zoomSteps;

                    if (notches < 0) {
                        //zoom in
                        zoomSteps++;
                    } else {
                        //zoom out
                        zoomSteps--;
                    }

                    try {
                        double factor = Math.pow(ZOOM_STEP_SIZE, zoomSteps);
                        int newWidth = (int) (width * factor);
                        int newHeight = (int) (height * factor);
                        image = Scalr.resize(originalImage, Scalr.Method.SPEED, newWidth, newHeight);

                        mainPanel.removeAll();
                        mainPanel.add(new ImageJLabel(image), BorderLayout.CENTER);
                        mainPanel.updateUI();
                    } catch (Throwable ignored) {
                        zoomSteps = oldZoomSteps;
                    }
                });
                return;
            }
            //hex viewer
            else if (BytecodeViewer.viewer.forcePureAsciiAsText.isSelected() || hexViewerOnly)
            {
                HexViewer hex = new HexViewer(contents);
                mainPanel.add(hex);
                return;
            }
        }
        
        textArea.setCodeFoldingEnabled(true);
        SyntaxLanguage.setLanguage(textArea, nameLowerCase);
        textArea.setText(new String(contents));
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));
        textArea.setCaretPosition(0);
        
        mainPanel.add(textArea.getScrollPane());
    }
    
    @Override
    public void refresh(JButton src)
    {
        refreshTitle();
        
        if (!canRefresh)
        {
            if(src != null)
                src.setEnabled(true);
            
            return;
        }

        mainPanel.removeAll();
      
        image = MiscUtils.loadImage(image, resource.getResourceBytes());
        
        JLabel label = new JLabel("", new ImageIcon(image), JLabel.CENTER);
        mainPanel.add(label, BorderLayout.CENTER);
        mainPanel.updateUI();
    
        if(src != null)
            src.setEnabled(true);
    }
    
    private static final long serialVersionUID = 6103372882168257164L;
}
