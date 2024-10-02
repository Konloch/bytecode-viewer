package the.bytecode.club.bytecodeviewer.cli;

import org.apache.commons.cli.*;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.cli.actions.commands.*;
import the.bytecode.club.bytecodeviewer.util.SleepUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * @author Konloch
 * @since 10/2/2024
 */
public class BCVCommandLine
{

    private final Options OPTIONS = new Options();
    private final CommandLineParser PARSER = new DefaultParser();
    private final ArrayList<CLICommand> COMMANDS = new ArrayList<>();
    private boolean isCLI = false;

    public void init(String[] args)
    {
        OPTIONS.addOption("i", true, "sets the input.");
        OPTIONS.addOption("o", true, "sets the output.");
        OPTIONS.addOption("t", true, "sets the target class to decompile, append all to decomp all as zip.");
        OPTIONS.addOption("nowait", true, "won't wait the 5 seconds to allow the user to read the CLI.");

        COMMANDS.add(new CleanCommand());
        COMMANDS.add(new CleanBootCommand());
        COMMANDS.add(new DecompilerCommand());
        COMMANDS.add(new LanguageCommand());
        COMMANDS.add(new HelpCommand());
        COMMANDS.add(new ListCommand());

        for(CLICommand command : COMMANDS)
            OPTIONS.addOption(command.name, command.hasArgs, command.description);

        isCLI = containsCLICommand(args);

        parseCommandLine(args);
    }

    private boolean containsCLICommand(String[] args)
    {
        if (args == null || args.length == 0)
            return false;

        try
        {
            CommandLine cmd = PARSER.parse(OPTIONS, args);

            for(CLICommand command : COMMANDS)
                if(cmd.hasOption(command.name) && command.isCLI)
                    return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }

    private void parseCommandLine(String[] args)
    {
        try
        {
            CommandLine cmd = PARSER.parse(OPTIONS, args);

            //TODO this is a backwards way of searching and will cause collisions
            // I'm sure the Apache CLI has a better way of navigating this

            for(CLICommand command : COMMANDS)
            {
                if(cmd.hasOption(command.name))
                {
                    command.runCommand(cmd);
                    return;
                }
            }

            if(isCLI)
                handleCLIDecompilation(cmd);
        }
        catch (Exception e)
        {
            BytecodeViewer.handleException(e);
        }
    }

    private void handleCLIDecompilation(CommandLine cmd)
    {
        if (cmd.getOptionValue("i") == null)
        {
            System.err.println("Set the input with -i");
            return;
        }

        if (cmd.getOptionValue("o") == null)
        {
            System.err.println("Set the output with -o");
            return;
        }

        if (cmd.getOptionValue("t") == null)
        {
            System.err.println("Set the target with -t");
            return;
        }

        //wait 5 seconds to allow time for reading
        if (!cmd.hasOption("nowait"))
           SleepUtil.sleep(5 * 1000);

        //decompiler configuration
        File input = new File(cmd.getOptionValue("i"));
        File output = new File(cmd.getOptionValue("o"));
        String decompiler = cmd.getOptionValue("decompiler");

        if (!input.exists())
        {
            System.err.println(input.getAbsolutePath() + " does not exist.");
            return;
        }

        if (output.exists())
        {
            System.err.println("WARNING: Deleted old " + output.getAbsolutePath() + ".");
            output.delete();
        }

        //check if zip, jar, apk, dex, or class
        //if its zip/jar/apk/dex attempt unzip as whole zip
        //if its just class allow any

        if (decompiler != null
            && !decompiler.equalsIgnoreCase("procyon")
            && !decompiler.equalsIgnoreCase("cfr")
            && !decompiler.equalsIgnoreCase("fernflower")
            && !decompiler.equalsIgnoreCase("krakatau")
            && !decompiler.equalsIgnoreCase("krakatau-bytecode")
            && !decompiler.equalsIgnoreCase("jd-gui")
            && !decompiler.equalsIgnoreCase("smali")
            && !decompiler.equalsIgnoreCase("asmifier"))
        {
            System.out.println("Error, no decompiler called '" + decompiler + "' found. Type -list" + " for the list");
        }

        //TODO decompiling happens here
    }

    public CLICommand getCommand(String name)
    {
        for(CLICommand command : COMMANDS)
            if(command.nameFormatted.equals(name))
                return command;

        return null;
    }

    public boolean isCLI()
    {
        return isCLI;
    }
}
