package the.bytecode.club.bytecodeviewer.translation.components;

import javax.swing.JCheckBoxMenuItem;
import the.bytecode.club.bytecodeviewer.translation.TranslatedComponentReference;
import the.bytecode.club.bytecodeviewer.translation.TranslatedComponents;

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
 * @since 6/28/2021
 */
public class TranslatedJCheckBoxMenuItem extends JCheckBoxMenuItem
{
	public TranslatedJCheckBoxMenuItem(String text, TranslatedComponents translatedComponents)
	{
		super(text);
		
		if(translatedComponents != null)
		{
			TranslatedComponentReference componentReference = translatedComponents.getTranslatedComponentReference();
			componentReference.runOnUpdate.add(()->
			{
				if(componentReference.value != null && !componentReference.value.isEmpty())
					setText(componentReference.value);
			});
			componentReference.translate();
		}
	}
}
