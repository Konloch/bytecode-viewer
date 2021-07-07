package the.bytecode.club.bytecodeviewer.translation;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Constant-like strings not associated with any specific JComponent
 *
 * @author Konloch
 * @since 7/6/2021
 */

public enum TranslatedStrings
{
	EDITABLE("Editable"),
	JAVA("Java"),
	PROCYON("Procyon"),
	CFR("CFR"),
	FERNFLOWER("FernFlower"),
	KRAKATAU("Krakatau"),
	JDGUI("JD-GUI"),
	JADX("JADX"),
	SMALI("Smali"),
	SMALI_DEX("Smali/DEX"),
	HEXCODE("Hexcode"),
	BYTECODE("Bytecode"),
	ASM_TEXTIFY("ASM Textify"),
	ERROR("Error"),
	DISASSEMBLER("Disassembler"),
	SUGGESTED_FIX_DECOMPILER_ERROR("Suggested Fix: Click refresh class, if it fails again try another decompiler."),
	SUGGESTED_FIX_COMPILER_ERROR("Suggested Fix: Try View>Pane>Krakatau>Bytecode and enable Editable."),
	DRAG_CLASS_JAR("Drag class/jar/zip/APK/DEX here"),
	;
	
	public static final HashSet<String> nameSet = new HashSet<>();
	
	static
	{
		for(TranslatedStrings s : values())
			nameSet.add(s.name());
	}
	
	private String text;
	
	TranslatedStrings(String text) {this.text = text;}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	public String getText()
	{
		return text;
	}
	
	@Override
	public String toString()
	{
		return getText();
	}
}