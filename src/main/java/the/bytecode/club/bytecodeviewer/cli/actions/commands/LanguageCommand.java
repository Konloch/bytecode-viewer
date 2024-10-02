package the.bytecode.club.bytecodeviewer.cli.actions.commands;

import org.apache.commons.cli.CommandLine;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.cli.CLICommand;
import the.bytecode.club.bytecodeviewer.translation.Language;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;

import javax.swing.*;

/**
 * @author Konloch
 * @since 10/2/2024
 */
public class LanguageCommand extends CLICommand
{

    public LanguageCommand()
    {
        super("language", "Forces specific language translations and continues to boot into the GUI", true, false);
    }

    @Override
    public void runCommand(CommandLine cmd)
    {
        Language language = Language.ENGLISH;

        String inputLanguage = cmd.getOptionValue("language");
        String inputLanguageLowerCase = inputLanguage.toLowerCase();
        boolean found = false;

        //strict matching
        for(Language lang : Language.values())
        {
            if(lang.name().equalsIgnoreCase(inputLanguage))
            {
                language = lang;
                found = true;
                break;
            }

            if(lang.getReadableName().equalsIgnoreCase(inputLanguage))
            {
                language = lang;
                found = true;
                break;
            }

            for(String languageCode : lang.getLanguageCode())
            {
                if(languageCode.equalsIgnoreCase(inputLanguage))
                {
                    language = lang;
                    found = true;
                    break;
                }
            }
        }

        //loose matching by name
        if(!found)
        {
            for (Language lang : Language.values())
            {
                if (lang.name().toLowerCase().contains(inputLanguageLowerCase))
                {
                    language = lang;
                    found = true;
                    break;
                }
            }
        }

        if(!found)
        {
            for (Language lang : Language.values())
            {
                if (lang.getReadableName().toLowerCase().contains(inputLanguageLowerCase))
                {
                    language = lang;
                    found = true;
                    break;
                }
            }
        }

        //loose matching by language code
        if(!found)
        {
            for (Language lang : Language.values())
            {
                for(String languageCode : lang.getLanguageCode())
                {
                    if(languageCode.toLowerCase().contains(inputLanguageLowerCase))
                    {
                        language = lang;
                        found = true;
                        break;
                    }
                }
            }
        }

        if(found)
        {
            System.out.println("Changing language to: " + language);

            Language finalLanguage = language;
            SwingUtilities.invokeLater(()-> MiscUtils.setLanguage(finalLanguage));
        }
        else
        {
            System.out.println("Could not find supported language: " + language);
        }
    }
}
