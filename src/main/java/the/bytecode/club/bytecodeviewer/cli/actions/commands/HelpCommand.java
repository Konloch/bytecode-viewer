package the.bytecode.club.bytecodeviewer.cli.actions.commands;

import org.apache.commons.cli.CommandLine;
import the.bytecode.club.bytecodeviewer.cli.CLICommand;

/**
 * @author Konloch
 * @since 10/2/2024
 */
public class HelpCommand extends CLICommand
{

    public HelpCommand()
    {
        super("help", "prints the help menu.", false, true);
    }

    @Override
    public void runCommand(CommandLine cmd)
    {
        for (String s : new String[]{
            "==BCV CLI Commands==",
            "-clean                        Deletes the BCV directory",
            "-help                         Displays the help menu",
            "-list                         Displays the available CLI decompilers",
            "-decompiler <decompiler>      Selects the decompiler, procyon by default",
            "-i <input file>               Selects the input file",
            "-o <output file>              Selects the output file",
            "-t <target classname>         Must either be the fully qualified classname or \"all\" to decompile all as zip",
            "-nowait                       Doesn't wait for the user to read the CLI messages",
            "",
            "==BCV GUI Commands==",
            "-cleanboot                    Deletes the BCV directory and continues to boot into the GUI",
            "-language <language>          Sets specific language translations"
        })
            System.out.println(s);
    }
}
