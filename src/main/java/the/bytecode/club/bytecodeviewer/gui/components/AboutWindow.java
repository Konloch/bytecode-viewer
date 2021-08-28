package the.bytecode.club.bytecodeviewer.gui.components;

import java.awt.CardLayout;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import the.bytecode.club.bytecodeviewer.bootloader.InitialBootScreen;
import the.bytecode.club.bytecodeviewer.resources.IconResources;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;

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
 * The about window - used to explain what BCV is, how to use it, etc.
 *
 * @author Konloch
 */

public class AboutWindow extends JFrame
{
    public AboutWindow()
    {
        this.setIconImages(IconResources.iconList);
        setSize(InitialBootScreen.getSafeSize());
        setTitle(TranslatedStrings.ABOUT_TITLE.toString());
        getContentPane().setLayout(new CardLayout(0, 0));
        
        JScrollPane scrollPane = new JScrollPane();
        getContentPane().add(scrollPane);
        
        try
        {
            scrollPane.setViewportView(HTMLPane.fromResource(language.getHTMLPath("intro")));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        this.setLocationRelativeTo(null);
    }

    private static final long serialVersionUID = -8230501978224923296L;
}
