package the.bytecode.club.bytecodeviewer.cli;

import org.apache.commons.cli.CommandLine;

/**
 * @author Konloch
 * @since 10/2/2024
 */
public abstract class CLICommand
{

    public final String name;
    public final String nameFormatted;
    public final String description;
    public final boolean hasArgs;
    public final boolean isCLI;

    protected CLICommand(String name, String description, boolean hasArgs, boolean isCLI)
    {
        this.name = name;
        this.nameFormatted = "-" + name;
        this.description = description;
        this.hasArgs = hasArgs;
        this.isCLI = isCLI;
    }

    public abstract void runCommand(CommandLine cmd);

}
