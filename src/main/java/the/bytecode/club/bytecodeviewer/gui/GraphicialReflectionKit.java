package the.bytecode.club.bytecodeviewer.gui;

import javax.swing.JFrame;
import java.awt.Dimension;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import javax.swing.JPanel;

import the.bytecode.club.bytecodeviewer.Resources;

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
 * A graphical way to execute reflection.
 *
 * @author Konloch
 */

public class GraphicialReflectionKit extends JFrame {
    public GraphicialReflectionKit() {
        this.setIconImages(Resources.iconList);
        setSize(new Dimension(382, 356));
        setTitle("Graphicial Reflection Kit");

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        tabbedPane.addTab("Invoke Method", null, panel, null);

        JPanel panel_1 = new JPanel();
        tabbedPane.addTab("Get Field Value", null, panel_1, null);

        JPanel panel_2 = new JPanel();
        tabbedPane.addTab("Cast Field", null, panel_2, null);
    }

    private static final long serialVersionUID = 6728356108271228236L;
}
