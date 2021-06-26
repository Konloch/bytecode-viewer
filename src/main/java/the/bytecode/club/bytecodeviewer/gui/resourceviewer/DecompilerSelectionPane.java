package the.bytecode.club.bytecodeviewer.gui.resourceviewer;

import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;
import the.bytecode.club.bytecodeviewer.gui.components.DecompilerViewComponent;

import javax.swing.*;

/**
 * @author Konloch
 * @since 6/21/2021
 */
public class DecompilerSelectionPane
{
	public final int paneID;
	public final JMenu menu;
	public final ButtonGroup group = new ButtonGroup();
	public final JRadioButtonMenuItem none = new JRadioButtonMenuItem("None");
	public final DecompilerViewComponent procyon = new DecompilerViewComponent("Procyon");
	public final DecompilerViewComponent CFR = new DecompilerViewComponent("CFR");
	public final DecompilerViewComponent JADX = new DecompilerViewComponent("JADX");
	public final DecompilerViewComponent JD = new DecompilerViewComponent("JD-GUI");
	public final DecompilerViewComponent fern = new DecompilerViewComponent("FernFlower");
	public final DecompilerViewComponent krakatau = new DecompilerViewComponent("Krakatau", true);
	public final DecompilerViewComponent smali = new DecompilerViewComponent("Smali/DEX");
	public final JRadioButtonMenuItem hexcode = new JRadioButtonMenuItem("Hexcode");
	public final JRadioButtonMenuItem bytecode = new JRadioButtonMenuItem("Bytecode");
	public final JRadioButtonMenuItem asmTextify = new JRadioButtonMenuItem("ASM Textify");
	
	public DecompilerSelectionPane(int paneID) {
		this.paneID = paneID;
		this.menu = new JMenu("Pane " + paneID);
		buildMenu();
	}
	
	public void buildMenu()
	{
		group.add(none);
		procyon.addToGroup(group);
		CFR.addToGroup(group);
		JADX.addToGroup(group);
		JD.addToGroup(group);
		fern.addToGroup(group);
		krakatau.addToGroup(group);
		smali.addToGroup(group);
		group.add(bytecode);
		group.add(hexcode);
		group.add(asmTextify);
		
		menu.add(none);
		menu.add(new JSeparator());
		menu.add(procyon.getMenu());
		menu.add(CFR.getMenu());
		menu.add(JADX.getMenu());
		menu.add(JD.getMenu());
		menu.add(fern.getMenu());
		menu.add(krakatau.getMenu());
		menu.add(new JSeparator());
		menu.add(smali.getMenu());
		menu.add(new JSeparator());
		menu.add(hexcode);
		menu.add(bytecode);
		menu.add(asmTextify);
	}
	
	public int getSelectedViewer()
	{
		if (group.isSelected(none.getModel()))
			return 0;
		else if (group.isSelected(procyon.getJava().getModel()))
			return 1;
		else if (group.isSelected(CFR.getJava().getModel()))
			return 2;
		else if (group.isSelected(fern.getJava().getModel()))
			return 3;
		else if (group.isSelected(bytecode.getModel()))
			return 4;
		else if (group.isSelected(hexcode.getModel()))
			return 5;
		else if (group.isSelected(smali.getJava().getModel()))
			return 6;
		else if (group.isSelected(krakatau.getJava().getModel()))
			return 7;
		else if (group.isSelected(krakatau.getBytecode().getModel()))
			return 8;
		else if (group.isSelected(JD.getJava().getModel()))
			return 9;
		else if (group.isSelected(JADX.getJava().getModel()))
			return 10;
		else if (group.isSelected(asmTextify.getModel()))
			return 11;
		
		System.out.println("DEFAULTING TO NULL");
		
		//default to none
		return 0;
	}
	
	public void setSelectedViewer(int decompiler)
	{
		switch (decompiler)
		{
			case 0:
				group.setSelected(none.getModel(), true);
				break;
			case 1:
				group.setSelected(procyon.getJava().getModel(), true);
				break;
			case 2:
				group.setSelected(CFR.getJava().getModel(), true);
				break;
			case 3:
				group.setSelected(fern.getJava().getModel(), true);
				break;
			case 4:
				group.setSelected(bytecode.getModel(), true);
				break;
			case 5:
				group.setSelected(hexcode.getModel(), true);
				break;
			case 6:
				group.setSelected(smali.getJava().getModel(), true);
				break;
			case 7:
				group.setSelected(krakatau.getJava().getModel(), true);
				break;
			case 8:
				group.setSelected(krakatau.getBytecode().getModel(), true);
				break;
			case 9:
				group.setSelected(JD.getJava().getModel(), true);
				break;
			case 10:
				group.setSelected(JADX.getJava().getModel(), true);
				break;
			case 11:
				group.setSelected(asmTextify.getModel(), true);
				break;
		}
	}
	
	public boolean isPaneEditable()
	{
		if(group.isSelected(procyon.getJava().getModel()) && procyon.getEditable().isSelected())
			return true;
		if(group.isSelected(CFR.getJava().getModel()) && CFR.getEditable().isSelected())
			return true;
		if(group.isSelected(JADX.getJava().getModel()) && JADX.getEditable().isSelected())
			return true;
		if(group.isSelected(JD.getJava().getModel()) && JD.getEditable().isSelected())
			return true;
		if(group.isSelected(fern.getJava().getModel()) && fern.getEditable().isSelected())
			return true;
		if((group.isSelected(krakatau.getJava().getModel()) || group.isSelected(krakatau.getBytecode().getModel())) && krakatau.getEditable().isSelected())
			return true;
		if(group.isSelected(smali.getJava().getModel()) && krakatau.getEditable().isSelected())
			return true;
		
		return false;
	}
	
	public ButtonGroup getGroup()
	{
		return group;
	}
	
	public JRadioButtonMenuItem getNone()
	{
		return none;
	}
	
	public DecompilerViewComponent getProcyon()
	{
		return procyon;
	}
	
	public DecompilerViewComponent getCFR()
	{
		return CFR;
	}
	
	public DecompilerViewComponent getJADX()
	{
		return JADX;
	}
	
	public DecompilerViewComponent getJD()
	{
		return JD;
	}
	
	public DecompilerViewComponent getFern()
	{
		return fern;
	}
	
	public DecompilerViewComponent getKrakatau()
	{
		return krakatau;
	}
	
	public DecompilerViewComponent getSmali()
	{
		return smali;
	}
	
	public JRadioButtonMenuItem getHexcode()
	{
		return hexcode;
	}
	
	public JRadioButtonMenuItem getBytecode()
	{
		return bytecode;
	}
	
	public JRadioButtonMenuItem getAsmTextify()
	{
		return asmTextify;
	}
}
