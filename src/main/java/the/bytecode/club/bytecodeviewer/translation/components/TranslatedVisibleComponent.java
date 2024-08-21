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

package the.bytecode.club.bytecodeviewer.translation.components;

import the.bytecode.club.bytecodeviewer.gui.components.VisibleComponent;
import the.bytecode.club.bytecodeviewer.translation.TranslatedComponentReference;
import the.bytecode.club.bytecodeviewer.translation.TranslatedComponents;

/**
 * @author Konloch
 * @since 7/7/2021
 */
public class TranslatedVisibleComponent extends VisibleComponent
{
	public TranslatedVisibleComponent(String title, TranslatedComponents translatedComponents)
	{
		super(title);
		
		if(translatedComponents != null)
		{
			TranslatedComponentReference componentReference = translatedComponents.getTranslatedComponentReference();
			componentReference.runOnUpdate.add(()->
			{
				if(componentReference.value != null && !componentReference.value.isEmpty())
					setTitle(componentReference.value);
			});
			componentReference.translate();
		}
	}
}
