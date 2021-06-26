package the.bytecode.club.bytecodeviewer.gui.resourceviewer;

import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.objectweb.asm.ClassWriter;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;
import the.bytecode.club.bytecodeviewer.gui.components.SearchableRSyntaxTextArea;
import the.bytecode.club.bytecodeviewer.gui.hexviewer.JHexEditor;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ClassViewer;
import the.bytecode.club.bytecodeviewer.gui.util.PaneUpdaterThread;

import javax.swing.*;
import java.awt.*;

/**
 * @author Konloch
 * @since 6/24/2021
 */
public class ResourceViewPanel
{
	public final JPanel panel = new JPanel(new BorderLayout());
	
	public ClassViewer viewer;
	public int decompilerViewIndex = -1;
	public SearchableRSyntaxTextArea textArea;
	public PaneUpdaterThread updateThread;
	public final int panelIndex;
	
	//TODO change the compile mode for Krakatau and Smali assembly
	public ResourcePanelCompileMode compileMode = ResourcePanelCompileMode.JAVA;
	
	public ResourceViewPanel(int panelIndex) {this.panelIndex = panelIndex;}
	
	public void createPane(ClassViewer viewer)
	{
		panel.removeAll();
		textArea = null;
		
		if(viewer.cn == null)
		{
			panel.add(new JLabel("This file has been removed from the reload."));
		}
	}
	
	public void updatePane(ClassViewer cv, byte[] b, JButton button, boolean isPanelEditable)
	{
		updateThread = new PaneUpdaterThread(panelIndex, decompilerViewIndex) {
			@Override
			public void doShit() {
				try {
					BytecodeViewer.viewer.updateBusyStatus(true);
					
					if(ResourceViewPanel.this.decompilerViewIndex > 0)
					{
						//hex viewer
						if (ResourceViewPanel.this.decompilerViewIndex == 5)
						{
							final ClassWriter cw = new ClassWriter(0);
							cv.cn.accept(cw);
							
							final JHexEditor hex = new JHexEditor(cw.toByteArray());
							hex.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));
							
							SwingUtilities.invokeLater(() -> panel.add(hex));
						}
						else
						{
							viewer = cv;
							updateUpdaterTextArea = (SearchableRSyntaxTextArea) Configuration.rstaTheme.apply(new SearchableRSyntaxTextArea());
							
							final Decompiler decompiler = Decompiler.decompilersByIndex.get(ResourceViewPanel.this.decompilerViewIndex);
							final String decompiledSource = decompiler.getDecompiler().decompileClassNode(cv.cn, b);
							//decompilerUpdate.update(decompiler.getDecompilerName(), cv.cn, b, decompiler.getDecompiler(), updateUpdaterTextArea);
							
							SwingUtilities.invokeLater(() ->
							{
								panel.add(updateUpdaterTextArea.getScrollPane());
								
								textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
								textArea.setCodeFoldingEnabled(true);
								textArea.setAntiAliasingEnabled(true);
								textArea.setText(decompiledSource);
								textArea.setCaretPosition(0);
								textArea.setEditable(isPanelEditable);
								
								textArea.getScrollPane().setColumnHeaderView(new JLabel(decompiler.getDecompilerName() + " - Editable: " + textArea.isEditable()));
								textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN,
										(int) BytecodeViewer.viewer.fontSpinner.getValue()));
							});
							textArea = updateUpdaterTextArea;
						}
					}
				} catch (java.lang.IndexOutOfBoundsException | java.lang.NullPointerException e) {
					//ignore
				} catch (Exception e) {
					new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
				} finally {
					cv.resetDivider();
					BytecodeViewer.viewer.updateBusyStatus(false);
					if (button != null)
						button.setEnabled(true);
				}
			}
		};
	}
}
