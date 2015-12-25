package the.bytecode.club.bytecodeviewer;

import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.api.ExceptionUI;
import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;

import java.io.File;

/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Kalen 'Konloch' Kinloch - http://bytecodeviewer.com  *
 * *
 * This program is free software: you can redistribute it and/or modify    *
 * it under the terms of the GNU General Public License as published by  *
 * the Free Software Foundation, either version 3 of the License, or     *
 * (at your option) any later version.                                   *
 * *
 * This program is distributed in the hope that it will be useful,       *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 * GNU General Public License for more details.                          *
 * *
 * You should have received a copy of the GNU General Public License     *
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. *
 ***************************************************************************/

/**
 * Used to allow BCV to be integrated as CLI instead of GUI.
 *
 * @author Konloch
 */

public class CommandLineInput {
    private static final Options options = new Options();
    private static final CommandLineParser parser = new DefaultParser();

    /*BECAUSE WHO DOESN'T LOVE MAGIC NUMBERS*/
    public static int STOP = -1;
    public static int OPEN_FILE = 0;
    public static int CLI = 1;

    private CommandLine parsed;

    public CommandLineInput(String[] args) throws ParseException {
        parsed = parser.parse(options, args);
    }

    static {
        options.addOption(new Option("help", null, false, "prints the help menu."));
        options.addOption(new Option("list", null, false, "lists all the available decompilers for BCV " + BytecodeViewer.version + "."));
        options.addOption(new Option("decompiler", null, true, "sets the decompiler, procyon by default."));
        options.addOption(new Option("i", null, true, "sets the input."));
        options.addOption(new Option("o", null, true, "sets the output."));
        options.addOption(new Option("t", null, true, "sets the target class to decompile, append all to decomp all as zip."));
        options.addOption(new Option("nowait", null, true, "won't wait the 5 seconds to allow the user to read the CLI."));
    }

    public boolean containsCommand() {
        if (parsed.hasOption("help") ||
                parsed.hasOption("list") ||
                parsed.hasOption("decompiler") ||
                parsed.hasOption("i") ||
                parsed.hasOption("o") ||
                parsed.hasOption("t") ||
                parsed.hasOption("nowait")) {
            return true;
        }
        return false;
    }

    public int parseCommandLine() {
        if (!containsCommand()) {
            return OPEN_FILE;
        }
        try {
            if (parsed.hasOption("list")) {
                System.out.println("Procyon");
                System.out.println("CFR");
                System.out.println("FernFlower");
                System.out.println("Krakatau");
                System.out.println("Krakatau-Bytecode");
                System.out.println("JD-GUI");
                System.out.println("Smali");
                return STOP;
            } else if (parsed.hasOption("help")) {
                for (String s : new String[]{"-help                         Displays the help menu", "-list                         Displays the available decompilers", "-decompiler <decompiler>      Selects the decompiler, procyon by default", "-i <input file>               Selects the input file", "-o <output file>              Selects the output file", "-t <target classname>         Must either be the fully qualified classname or \"all\" to decompile all as zip", "-nowait                       Doesn't wait for the user to read the CLI messages"})
                    System.out.println(s);
                return STOP;
            } else {
                if (parsed.getOptionValue("i") == null) {
                    System.err.println("Set the input with -i");
                    return STOP;
                }
                if (parsed.getOptionValue("o") == null) {
                    System.err.println("Set the output with -o");
                    return STOP;
                }
                if (parsed.getOptionValue("t") == null) {
                    System.err.println("Set the target with -t");
                    return STOP;
                }

                File input = new File(parsed.getOptionValue("i"));
                File output = new File(parsed.getOptionValue("o"));
                String decompiler = parsed.getOptionValue("decompiler");

                if (!input.exists()) {
                    System.err.println(input.getAbsolutePath() + " does not exist.");
                    return STOP;
                }

                if (output.exists()) {
                    System.err.println("WARNING: Deleted old " + output.getAbsolutePath() + ".");
                    output.delete();
                }

                //check if zip, jar, apk, dex, or class
                //if its zip/jar/apk/dex attempt unzip as whole zip
                //if its just class allow any

                if (decompiler != null &&
                        !decompiler.equalsIgnoreCase("procyon") &&
                        !decompiler.equalsIgnoreCase("cfr") &&
                        !decompiler.equalsIgnoreCase("fernflower") &&
                        !decompiler.equalsIgnoreCase("krakatau") &&
                        !decompiler.equalsIgnoreCase("krakatau-bytecode") &&
                        !decompiler.equalsIgnoreCase("jd-gui") &&
                        !decompiler.equalsIgnoreCase("smali")) {
                    System.out.println("Error, no decompiler called '" + decompiler + "' found. Type -decompiler-list for the list");
                }


                if (!parsed.hasOption("nowait")) Thread.sleep(5 * 1000);

                return CLI;
            }
        } catch (Exception e) {
            new ExceptionUI(e);
        }

        return OPEN_FILE;
    }

    public void executeCommandLine() {
        try {
            File input = new File(parsed.getOptionValue("i"));
            File output = new File(parsed.getOptionValue("o"));
            String target = parsed.getOptionValue("t");

            Decompiler use = null;
            if (parsed.getOptionValue("decompiler") == null) {
                System.out.println("You can define another decompiler by appending -decompiler \"name\", by default procyon has been set.");
                use = Decompiler.PROCYON;
            } else if ((use = Decompiler.getByName(parsed.getOptionValue("decompiler"))) == null) {
                System.out.println("Decompiler not found. By default Procyon has been set.");
                use = Decompiler.PROCYON;
            }

            System.out.println("Decompiling " + input.getAbsolutePath() + " with " + use.getName());
            BytecodeViewer.openFiles(new File[]{input}, false);
            String containerName = BytecodeViewer.files.get(0).name;
            Thread.sleep(5 * 1000);
            if (target.equalsIgnoreCase("all")) {
                use.decompileToZip(output.getAbsolutePath());
            } else {
                try {
                    ClassNode cn = BytecodeViewer.getClassNode(containerName, target);
                    byte[] bytes = BytecodeViewer.getClassBytes(containerName, target);
                    String contents = use.decompileClassNode(cn, bytes);
                    FileUtils.write(output, contents, "UTF-8", false);
                } catch (Exception e) {
                    new ExceptionUI(e);
                }
            }
            System.out.println("Finished.");
            System.out.println("Bytecode Viewer CLI v" + BytecodeViewer.version + " by @Konloch - http://bytecodeviewer.com");
            System.exit(0);
        } catch (Exception e) {
            new ExceptionUI(e);
        }
    }
}
