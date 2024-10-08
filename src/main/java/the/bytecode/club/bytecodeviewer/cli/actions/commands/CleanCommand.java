package the.bytecode.club.bytecodeviewer.cli.actions.commands;

import org.apache.commons.cli.CommandLine;
import the.bytecode.club.bytecodeviewer.Constants;
import the.bytecode.club.bytecodeviewer.cli.CLICommand;

import java.io.File;

import static the.bytecode.club.bytecodeviewer.cli.CLIAction.GUI;

/**
 * @author Konloch
 * @since 10/2/2024
 */
public class CleanCommand extends CLICommand
{

    public CleanCommand()
    {
        super("clean", "Deletes the BCV directory", false, true);
    }

    @Override
    public void runCommand(CommandLine cmd)
    {
        new File(Constants.getBCVDirectory()).delete();

        System.out.println("BCV Directory Deleted - Exiting...");
    }
}
