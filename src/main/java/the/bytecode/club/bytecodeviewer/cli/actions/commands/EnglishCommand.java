package the.bytecode.club.bytecodeviewer.cli.actions.commands;

import org.apache.commons.cli.CommandLine;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.Constants;
import the.bytecode.club.bytecodeviewer.cli.CLICommand;
import the.bytecode.club.bytecodeviewer.translation.Language;

import java.io.File;

/**
 * @author Konloch
 * @since 10/2/2024
 */
public class EnglishCommand extends CLICommand
{

    public EnglishCommand()
    {
        super("english", "Forces English language translations and continues to boot into the GUI", false, false);
    }

    @Override
    public void runCommand(CommandLine cmd)
    {
        Configuration.language = Language.ENGLISH;
    }
}
