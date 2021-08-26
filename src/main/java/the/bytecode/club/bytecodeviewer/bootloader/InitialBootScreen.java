package the.bytecode.club.bytecodeviewer.bootloader;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.gui.components.HTMLPane;
import the.bytecode.club.bytecodeviewer.resources.IconResources;

import static the.bytecode.club.bytecodeviewer.Configuration.language;

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
 * @author Konloch
 * @author Bibl (don't ban me pls)
 * @created 19 Jul 2015 04:12:21
 */
public class InitialBootScreen extends JFrame
{
    private final JProgressBar progressBar = new JProgressBar();

    public InitialBootScreen() throws IOException
    {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Configuration.canExit = true;
                System.exit(0);
            }
        });
        this.setIconImages(IconResources.iconList);

        setSize(getSafeSize());

        setTitle("Bytecode Viewer Boot Screen - Starting Up");
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{0, 0};
        gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        getContentPane().setLayout(gridBagLayout);

        JScrollPane scrollPane = new JScrollPane();
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.gridheight = 24;
        gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridx = 0;
        gbc_scrollPane.gridy = 0;
        getContentPane().add(scrollPane, gbc_scrollPane);
    
        scrollPane.setViewportView(HTMLPane.fromResource(language.getHTMLPath("intro")));

        GridBagConstraints gbc_progressBar = new GridBagConstraints();
        gbc_progressBar.fill = GridBagConstraints.HORIZONTAL;
        gbc_progressBar.gridx = 0;
        gbc_progressBar.gridy = 24;
        getContentPane().add(progressBar, gbc_progressBar);
        this.setLocationRelativeTo(null);
    }
    
    public static Dimension getSafeSize()
    {
        int i = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        if (i >= 840)
           return new Dimension(600, 800);
        else if (i >= 640)
            return new Dimension(500, 600);
        else if (i >= 440)
            return new Dimension(400, 400);
        else
            return Toolkit.getDefaultToolkit().getScreenSize();
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }
    
    private static final long serialVersionUID = -1098467609722393444L;
}
