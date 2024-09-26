/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Konloch - Konloch.com / BytecodeViewer.com           *
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

package the.bytecode.club.bytecodeviewer.bootloader;

import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.gui.components.HTMLPane;
import the.bytecode.club.bytecodeviewer.resources.IconResources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import static the.bytecode.club.bytecodeviewer.Configuration.language;

/**
 * @author Konloch
 * @author Bibl (don't ban me pls)
 * @since 19 Jul 2015 04:12:21
 */
public class InitialBootScreen extends JFrame
{
    private final JProgressBar progressBar = new JProgressBar();

    public InitialBootScreen() throws IOException
    {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
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
        gridBagLayout.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        getContentPane().setLayout(gridBagLayout);

        JScrollPane scrollPane = new JScrollPane();
        GridBagConstraints scrollPaneConstraints = new GridBagConstraints();
        scrollPaneConstraints.gridheight = 24;
        scrollPaneConstraints.insets = new Insets(0, 0, 5, 0);
        scrollPaneConstraints.fill = GridBagConstraints.BOTH;
        scrollPaneConstraints.gridx = 0;
        scrollPaneConstraints.gridy = 0;
        getContentPane().add(scrollPane, scrollPaneConstraints);

        scrollPane.setViewportView(HTMLPane.fromResource(language.getHTMLPath("intro")));

        GridBagConstraints progressBarConstraints = new GridBagConstraints();
        progressBarConstraints.fill = GridBagConstraints.HORIZONTAL;
        progressBarConstraints.gridx = 0;
        progressBarConstraints.gridy = 24;
        getContentPane().add(progressBar, progressBarConstraints);
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

    public JProgressBar getProgressBar()
    {
        return progressBar;
    }

    private static final long serialVersionUID = -1098467609722393444L;
}
