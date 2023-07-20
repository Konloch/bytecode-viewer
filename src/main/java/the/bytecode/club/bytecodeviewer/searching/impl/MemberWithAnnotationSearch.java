package the.bytecode.club.bytecodeviewer.searching.impl;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.gui.theme.LAFTheme;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainer;
import the.bytecode.club.bytecodeviewer.searching.EnterKeyEvent;
import the.bytecode.club.bytecodeviewer.searching.LDCSearchTreeNodeResult;
import the.bytecode.club.bytecodeviewer.searching.SearchPanel;
import the.bytecode.club.bytecodeviewer.translation.TranslatedComponents;
import the.bytecode.club.bytecodeviewer.translation.components.TranslatedJLabel;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

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
 * Annotation Searching
 *
 * @author GraxCode
 */

public class MemberWithAnnotationSearch implements SearchPanel {
  JTextField annotation;
  JPanel myPanel = null;

  public MemberWithAnnotationSearch() {
    annotation = new JTextField("");
    annotation.addKeyListener(EnterKeyEvent.SINGLETON);
    LAFTheme.registerThemeUpdate(annotation);
  }

  @Override
  public JPanel getPanel() {
    if (myPanel == null) {
      myPanel = new JPanel(new BorderLayout(16, 16));
      myPanel.add(new TranslatedJLabel("Annotation name: ", TranslatedComponents.ANNOTATION_NAME), BorderLayout.WEST);
      myPanel.add(annotation, BorderLayout.CENTER);
      LAFTheme.registerThemeUpdate(myPanel);
    }

    return myPanel;
  }

  public void search(final ResourceContainer container, final String resourceWorkingName, final ClassNode node, boolean caseSensitive) {
    final String srchText = annotation.getText().trim();

    if (srchText.isEmpty()) return;

    node.fields.stream().filter(fn -> hasAnnotation(srchText, Arrays.asList(fn.invisibleAnnotations, fn.visibleAnnotations)))
            .forEach(fn -> BytecodeViewer.viewer.searchBoxPane.treeRoot.add(new LDCSearchTreeNodeResult(container, resourceWorkingName, node, null, fn, fn.name + " " + fn.desc, "")));
    node.methods.stream().filter(mn -> hasAnnotation(srchText, Arrays.asList(mn.invisibleAnnotations, mn.visibleAnnotations)))
            .forEach(mn -> BytecodeViewer.viewer.searchBoxPane.treeRoot.add(new LDCSearchTreeNodeResult(container, resourceWorkingName, node, mn, null, mn.name + mn.desc, "")));
  }

  public static boolean hasAnnotation(String annotation, List<List<AnnotationNode>> annoLists) {
    if (annoLists == null) return false;
    for (List<AnnotationNode> annos : annoLists) {
      if (annos == null) continue;
      if (annos.stream().anyMatch(ant -> {
        String internalName = Type.getType(ant.desc).getInternalName();
        return internalName.equals(annotation) || internalName.endsWith('/' + annotation) || ant.desc.endsWith('/' + annotation.replace('.', '$'));
        // in case dot is used for inner class annotations
      })) return true;
    }
    return false;
  }
}
