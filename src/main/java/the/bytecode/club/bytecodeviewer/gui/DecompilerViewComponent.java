package the.bytecode.club.bytecodeviewer.gui;

import the.bytecode.club.bytecodeviewer.util.RefreshWorkPane;

import javax.swing.*;

/**
 * @author Konloch
 * @since 6/21/2021
 */
public class DecompilerViewComponent
{
	private final String name;
	private final boolean hasBytecodeOption;
	private final JMenu menu;
	private final JRadioButtonMenuItem java = new JRadioButtonMenuItem("Java");
	private final JRadioButtonMenuItem bytecode = new JRadioButtonMenuItem("Bytecode");
	private final JCheckBoxMenuItem editable = new JCheckBoxMenuItem("Editable");
	
	public DecompilerViewComponent(String name) {
		this(name, false);
	}
	
	public DecompilerViewComponent(String name, boolean hasBytecodeOption) {
		this.name = name;
		this.menu = new JMenu(name);
		this.hasBytecodeOption = hasBytecodeOption;
		createMenu();
	}
	
	private void createMenu()
	{
		menu.add(java);
		if(hasBytecodeOption)
			menu.add(bytecode);
		menu.add(new JSeparator());
		menu.add(editable);
		
		java.addActionListener(new RefreshWorkPane());
	}
	
	public void addToGroup(ButtonGroup group)
	{
		group.add(java);
		if(hasBytecodeOption)
			group.add(bytecode);
	}
	
	public JMenu getMenu()
	{
		return menu;
	}
	
	public JRadioButtonMenuItem getJava()
	{
		return java;
	}
	
	public JRadioButtonMenuItem getBytecode()
	{
		return bytecode;
	}
	
	public JCheckBoxMenuItem getEditable()
	{
		return editable;
	}
}
