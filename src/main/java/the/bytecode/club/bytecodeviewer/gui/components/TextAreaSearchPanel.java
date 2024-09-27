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
package the.bytecode.club.bytecodeviewer.gui.components;

import the.bytecode.club.bytecodeviewer.gui.components.listeners.ReleaseKeyListener;
import the.bytecode.club.bytecodeviewer.resources.IconResources;
import the.bytecode.club.bytecodeviewer.translation.TranslatedComponents;
import the.bytecode.club.bytecodeviewer.translation.components.TranslatedJCheckBox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * This panel represents the decompiler name and search box on the top of every {@link the.bytecode.club.bytecodeviewer.gui.resourceviewer.BytecodeViewPanel}
 * <p>
 * Created by Bl3nd.
 * Date: 9/6/2024
 */
public class TextAreaSearchPanel extends JPanel
{
    private final JCheckBox caseSensitiveSearch = new TranslatedJCheckBox("Match case", TranslatedComponents.MATCH_CASE);
    private final JLabel titleHeader = new JLabel("");
    private final JTextField searchInput = new JTextField();
    private final JTextArea textArea;

    public TextAreaSearchPanel(JTextArea textArea)
    {
        super(new BorderLayout());

        this.textArea = textArea;

        setup();
        setVisible(true);
    }

    private void setup()
    {
        this.add(titleHeader, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));

        searchPanel.add(Box.createHorizontalStrut(35));
        JButton searchNext = new JButton(IconResources.nextIcon);
        searchPanel.add(searchNext);
        searchNext.addActionListener(arg0 -> ((SearchableRSyntaxTextArea) textArea).search(searchInput.getText(), true, caseSensitiveSearch.isSelected()));

        JButton searchPrev = new JButton(IconResources.prevIcon);
        searchPanel.add(searchPrev);
        searchPrev.addActionListener(arg0 -> ((SearchableRSyntaxTextArea) textArea).search(searchInput.getText(), false, caseSensitiveSearch.isSelected()));

        searchPanel.add(searchInput);
        searchInput.addKeyListener(new ReleaseKeyListener(keyEvent ->
        {
            if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER)
                ((SearchableRSyntaxTextArea) textArea).search(searchInput.getText(), true, caseSensitiveSearch.isSelected());
        }));

        searchPanel.add(caseSensitiveSearch);

        // This is needed to add more room to the right of the sensitive search check box
        searchPanel.add(Box.createHorizontalStrut(2));

        this.add(searchPanel, BorderLayout.SOUTH);
    }

    public JLabel getTitleHeader()
    {
        return titleHeader;
    }

    public JTextField getSearchInput()
    {
        return searchInput;
    }
}
