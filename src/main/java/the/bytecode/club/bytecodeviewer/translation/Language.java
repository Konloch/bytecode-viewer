package the.bytecode.club.bytecodeviewer.translation;

import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.io.IOUtils;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Resources;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * All of the supported languages
 *
 * @author Konloch
 * @since 6/28/2021
 */
public enum Language
{
	ENGLISH("/translations/english.json", "English", "en"),
	MANDARIN("/translations/mandarin.json", "普通话", "zh_cn", "zh"),
	//HINDI("/translations/hindi.json", "hi"),
	/*SPANISH("/translations/spanish.json", "es"),
	FRENCH("/translations/french.json", "fr"),
	ARABIC("/translations/arabic.json", "ab"),
	RUSSIAN("/translations/russian.json", "ru"),*/
	GERMAN("/translations/german.json", "Deutsche", "de"),
	;
	
	private static final HashedMap<String, Language> languageCodeLookup;
	
	static
	{
		languageCodeLookup = new HashedMap<>();
		for(Language l : values())
			l.languageCode.forEach((langCode)->
					languageCodeLookup.put(langCode, l));
	}
	
	private final String resourcePath;
	private final String readableName;
	private final HashSet<String> languageCode;
	
	Language(String resourcePath, String readableName, String... languageCodes)
	{
		this.resourcePath = resourcePath;
		this.languageCode = new HashSet<>(Arrays.asList(languageCodes));
		this.readableName = readableName;
	}
	
	public void loadLanguage() throws IOException
	{
		printMissingLanguageKeys();
		
		HashMap<String, String> translationMap = BytecodeViewer.gson.fromJson(
				IOUtils.toString(Resources.class.getResourceAsStream(resourcePath), StandardCharsets.UTF_8),
				new TypeToken<HashMap<String, String>>(){}.getType());
		
		for(Translation translation : Translation.values())
		{
			TranslatedComponent text = translation.getTranslatedComponent();
			
			//skip translating if the language config is missing the translation key
			if(!translationMap.containsKey(text.key))
				continue;
			
			//update translation text value
			text.value = translationMap.get(text.key);
			
			//check if translation key has been assigned to a component,
			//on fail print an error alerting the devs
			if(translation.getTranslatedComponent().runOnUpdate.isEmpty())
			{
				System.err.println("Translation Reference " + translation.name() + " is missing component attachment, skipping...");
				continue;
			}
			
			//trigger translation event
			translation.getTranslatedComponent().runOnUpdate.forEach(Runnable::run);
		}
	}
	
	//TODO
	// When adding new Translation Components:
	// 1) start by adding the strings into the english.json
	// 2) run this function to get the keys and add them into the Translation.java enum
	// 3) replace the swing component (MainViewerGUI) with a translated component
	//    and reference the correct translation key
	// 4) add the translation key to the rest of the translation files
	public void printMissingLanguageKeys() throws IOException
	{
		if(this != ENGLISH)
			return;
		
		LinkedMap<String, String> translationMap = BytecodeViewer.gson.fromJson(
				IOUtils.toString(Resources.class.getResourceAsStream(resourcePath), StandardCharsets.UTF_8),
				new TypeToken<LinkedMap<String, String>>(){}.getType());
		
		HashSet<String> existingKeys = new HashSet<>();
		for(Translation t : Translation.values())
			existingKeys.add(t.name());
		
		for(String key : translationMap.keySet())
			if(!existingKeys.contains(key) && !key.startsWith("TODO"))
				System.err.println(key + ",");
	}
	
	public String getResourcePath()
	{
		return resourcePath;
	}
	
	public HashSet<String> getLanguageCode()
	{
		return languageCode;
	}
	
	public String getReadableName()
	{
		return readableName;
	}
	
	public static HashedMap<String, Language> getLanguageCodeLookup()
	{
		return languageCodeLookup;
	}
}
