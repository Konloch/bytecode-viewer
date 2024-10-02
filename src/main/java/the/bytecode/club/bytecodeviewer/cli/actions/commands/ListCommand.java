package the.bytecode.club.bytecodeviewer.cli.actions.commands;

import org.apache.commons.cli.CommandLine;
import the.bytecode.club.bytecodeviewer.cli.CLICommand;

import static the.bytecode.club.bytecodeviewer.Constants.VERSION;

/**
 * @author Konloch
 * @since 10/2/2024
 */
public class ListCommand extends CLICommand
{

    public ListCommand()
    {
        super("list", "lists all the available decompilers for BCV " + VERSION + ".", false, true);
    }

    @Override
    public void runCommand(CommandLine cmd)
    {
        for (String s : new String[]{
            "==BCV CLI Decompilers==",
            "Procyon",
            "CFR",
            "FernFlower",
            "Krakatau",
            "Krakatau-Bytecode",
            "JD-GUI",
            "Smali",
            "ASMifier"
        })
            System.out.println(s);
    }
}
