package the.bytecode.club.bytecodeviewer.gui.resourceviewer;

import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;
import the.bytecode.club.bytecodeviewer.gui.components.DecompilerViewComponent;
import the.bytecode.club.bytecodeviewer.translation.Translation;
import the.bytecode.club.bytecodeviewer.translation.components.TranslatedJMenu;
import the.bytecode.club.bytecodeviewer.translation.components.TranslatedJRadioButtonMenuItem;

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
	public final JRadioButtonMenuItem none = new TranslatedJRadioButtonMenuItem("None", Translation.NONE);
	public final DecompilerViewComponent procyon = new DecompilerViewComponent("Procyon");
	public final DecompilerViewComponent CFR = new DecompilerViewComponent("CFR");
	public final DecompilerViewComponent JADX = new DecompilerViewComponent("JADX");
	public final DecompilerViewComponent JD = new DecompilerViewComponent("JD-GUI");
	public final DecompilerViewComponent fern = new DecompilerViewComponent("FernFlower");
	public final DecompilerViewComponent krakatau = new DecompilerViewComponent("Krakatau", true);
	public final DecompilerViewComponent smali = new DecompilerViewComponent("Smali/DEX");
	public final JRadioButtonMenuItem hexcode = new TranslatedJRadioButtonMenuItem("Hexcode", Translation.HEXCODE);
	public final JRadioButtonMenuItem bytecode = new TranslatedJRadioButtonMenuItem("Bytecode", Translation.BYTECODE);
	public final JRadioButtonMenuItem asmTextify = new TranslatedJRadioButtonMenuItem("ASM Textify", Translation.ASM_TEXTIFY);
	
	public DecompilerSelectionPane(int paneID)
	{
		this.paneID = paneID;
		if(paneID == 1)
			this.menu = new TranslatedJMenu("Pane " + paneID, Translation.PANE_1);
		else if(paneID == 2)
			this.menu = new TranslatedJMenu("Pane " + paneID, Translation.PANE_2);
		else
			this.menu = new TranslatedJMenu("Pane " + paneID, Translation.PANE_3);
		
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
	
	public Decompiler getSelectedDecompiler()
	{
		if (group.isSelected(none.getModel()))
			return Decompiler.NONE;
		else if (group.isSelected(procyon.getJava().getModel()))
			return Decompiler.PROCYON_DECOMPILER;
		else if (group.isSelected(CFR.getJava().getModel()))
			return Decompiler.CFR_DECOMPILER;
		else if (group.isSelected(fern.getJava().getModel()))
			return Decompiler.FERNFLOWER_DECOMPILER;
		else if (group.isSelected(bytecode.getModel()))
			return Decompiler.BYTECODE_DISASSEMBLER;
		else if (group.isSelected(hexcode.getModel()))
			return Decompiler.HEXCODE_VIEWER;
		else if (group.isSelected(smali.getJava().getModel()))
			return Decompiler.SMALI_DISASSEMBLER;
		else if (group.isSelected(krakatau.getJava().getModel()))
			return Decompiler.KRAKATAU_DECOMPILER;
		else if (group.isSelected(krakatau.getBytecode().getModel()))
			return Decompiler.KRAKATAU_DISASSEMBLER;
		else if (group.isSelected(JD.getJava().getModel()))
			return Decompiler.JD_DECOMPILER;
		else if (group.isSelected(JADX.getJava().getModel()))
			return Decompiler.JADX_DECOMPILER;
		else if (group.isSelected(asmTextify.getModel()))
			return Decompiler.ASM_TEXTIFY_DISASSEMBLER;
		
		System.out.println("DEFAULTING TO NULL");
		
		//default to none
		return Decompiler.NONE;
	}
	
	public void setSelectedDecompiler(Decompiler decompiler)
	{
		switch (decompiler)
		{
			case NONE:
				group.setSelected(none.getModel(), true);
				break;
			case PROCYON_DECOMPILER:
				group.setSelected(procyon.getJava().getModel(), true);
				break;
			case CFR_DECOMPILER:
				group.setSelected(CFR.getJava().getModel(), true);
				break;
			case FERNFLOWER_DECOMPILER:
				group.setSelected(fern.getJava().getModel(), true);
				break;
			case BYTECODE_DISASSEMBLER:
				group.setSelected(bytecode.getModel(), true);
				break;
			case HEXCODE_VIEWER:
				group.setSelected(hexcode.getModel(), true);
				break;
			case SMALI_DISASSEMBLER:
				group.setSelected(smali.getJava().getModel(), true);
				break;
			case KRAKATAU_DECOMPILER:
				group.setSelected(krakatau.getJava().getModel(), true);
				break;
			case KRAKATAU_DISASSEMBLER:
				group.setSelected(krakatau.getBytecode().getModel(), true);
				break;
			case JD_DECOMPILER:
				group.setSelected(JD.getJava().getModel(), true);
				break;
			case JADX_DECOMPILER:
				group.setSelected(JADX.getJava().getModel(), true);
				break;
			case ASM_TEXTIFY_DISASSEMBLER:
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
		if(group.isSelected(smali.getJava().getModel()) && smali.getEditable().isSelected())
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
