package the.bytecode.club.bytecodeviewer.cli.actions.commands;

import org.apache.commons.cli.CommandLine;
import the.bytecode.club.bytecodeviewer.Constants;
import the.bytecode.club.bytecodeviewer.cli.CLICommand;

import java.io.File;

/**
 * @author Konloch
 * @since 10/2/2024
 */
public class CleanBootCommand extends CLICommand
{

    public CleanBootCommand()
    {
        super("cleanboot", "Deletes the BCV directory and continues to boot into the GUI", false, false);
    }

    @Override
    public void runCommand(CommandLine cmd)
    {
        new File(Constants.getBCVDirectory()).delete();

        System.out.println("BCV Directory Deleted - Continuing to GUI...");
    }
}
