package the.bytecode.club.bytecodeviewer;

import java.io.File;
import me.konloch.kontainer.io.DiskWriter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;
import the.bytecode.club.bytecodeviewer.translation.Language;
import the.bytecode.club.bytecodeviewer.util.JarUtils;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;

import static the.bytecode.club.bytecodeviewer.Constants.VERSION;
import static the.bytecode.club.bytecodeviewer.Constants.fs;
import static the.bytecode.club.bytecodeviewer.Constants.tempDirectory;

/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Kalen 'Konloch' Kinloch - http://bytecodeviewer.com  *
 *                                                                         *
 * This program is free software: you can redistribute it and/or modify    *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation, either version 3 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>. *
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
    public static int GUI = 0;
    public static int CLI = 1;

    static {
        options.addOption("help", false, "prints the help menu.");
        options.addOption("list", false, "lists all the available decompilers for BCV " + VERSION + ".");
        options.addOption("decompiler", true, "sets the decompiler, procyon by default.");
        options.addOption("i", true, "sets the input.");
        options.addOption("o", true, "sets the output.");
        options.addOption("t", true, "sets the target class to decompile, append all to decomp all as zip.");
        options.addOption("nowait", true, "won't wait the 5 seconds to allow the user to read the CLI.");
    }

    public static boolean containsCommand(String[] args) {
        if (args == null || args.length == 0)
            return false;

        try {
            CommandLine cmd = parser.parse(options, args);
            if (
                    cmd.hasOption("help") ||
                            cmd.hasOption("clean") ||
                            cmd.hasOption("english") ||
                            cmd.hasOption("list") ||
                            cmd.hasOption("decompiler") ||
                            cmd.hasOption("i") ||
                            cmd.hasOption("o") ||
                            cmd.hasOption("t") ||
                            cmd.hasOption("nowait")
            ) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static int parseCommandLine(String[] args) {
        if (!containsCommand(args))
            return GUI;
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("list")) {
                System.out.println("Procyon");
                System.out.println("CFR");
                System.out.println("FernFlower");
                System.out.println("Krakatau");
                System.out.println("Krakatau-Bytecode");
                System.out.println("JD-GUI");
                System.out.println("Smali");
                return STOP;
            } else if (cmd.hasOption("clean")) {
                new File(Constants.getBCVDirectory()).delete();
                if(cmd.getOptionValue("i") == null && cmd.getOptionValue("o") == null
                        && cmd.getOptionValue("t") == null)
                    return GUI;
            } else if (cmd.hasOption("english")) {
                Configuration.language = Language.ENGLISH;
                return GUI;
            } else if (cmd.hasOption("help")) {
                for (String s : new String[]{
                        "-help                         Displays the help menu",
                        "-clean                        Deletes the BCV directory",
                        "-english                      Forces English language translations",
                        "-list                         Displays the available decompilers",
                        "-decompiler <decompiler>      Selects the decompiler, procyon by default",
                        "-i <input file>               Selects the input file",
                        "-o <output file>              Selects the output file",
                        "-t <target classname>         Must either be the fully qualified classname or \"all\" to decompile all as zip",
                        "-nowait                       Doesn't wait for the user to read the CLI messages"
                })
                    System.out.println(s);
                return STOP;
            } else {
                if (cmd.getOptionValue("i") == null) {
                    System.err.println("Set the input with -i");
                    return STOP;
                }
                if (cmd.getOptionValue("o") == null) {
                    System.err.println("Set the output with -o");
                    return STOP;
                }
                if (cmd.getOptionValue("t") == null) {
                    System.err.println("Set the target with -t");
                    return STOP;
                }

                File input = new File(cmd.getOptionValue("i"));
                File output = new File(cmd.getOptionValue("o"));
                String decompiler = cmd.getOptionValue("decompiler");

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

                if (
                        decompiler != null &&
                                !decompiler.equalsIgnoreCase("procyon") &&
                                !decompiler.equalsIgnoreCase("cfr") &&
                                !decompiler.equalsIgnoreCase("fernflower") &&
                                !decompiler.equalsIgnoreCase("krakatau") &&
                                !decompiler.equalsIgnoreCase("krakatau-bytecode") &&
                                !decompiler.equalsIgnoreCase("jd-gui") &&
                                !decompiler.equalsIgnoreCase("smali")
                ) {
                    System.out.println("Error, no decompiler called '" + decompiler + "' found. Type -decompiler-list"
                            + " for the list");
                }


                if (!cmd.hasOption("nowait"))
                    Thread.sleep(5 * 1000);

                return CLI;
            }
        } catch (Exception e) {
            BytecodeViewer.handleException(e);
        }

        return GUI;
    }

    public static void executeCommandLine(String[] args) {
        try {
            CommandLine cmd = parser.parse(options, args);
            String decompiler = cmd.getOptionValue("decompiler");
            File input = new File(cmd.getOptionValue("i"));
            File output = new File(cmd.getOptionValue("o"));
            String target = cmd.getOptionValue("t");

            if (cmd.getOptionValue("decompiler") == null) {
                System.out.println("You can define another decompiler by appending -decompiler \"name\", by default "
                        + "procyon has been set.");
                decompiler = "procyon";
            }

            //check if zip, jar, apk, dex, or class
            //if its zip/jar/apk/dex attempt unzip as whole zip
            //if its just class allow any

            File tempZip =
                    new File(tempDirectory + fs + "temp_" + MiscUtils.getRandomizedName() + ".jar");
            if (tempZip.exists())
                tempZip.delete();

            JarUtils.saveAsJarClassesOnly(BytecodeViewer.getLoadedClasses(), tempZip.getAbsolutePath());

            if (decompiler.equalsIgnoreCase("procyon")) {
                System.out.println("Decompiling " + input.getAbsolutePath() + " with Procyon");
                BytecodeViewer.openFiles(new File[]{input}, false);

                Thread.sleep(5 * 1000);

                if (target.equalsIgnoreCase("all")) {
                    Decompiler.PROCYON_DECOMPILER.getDecompiler().decompileToZip(tempZip.getAbsolutePath(), output.getAbsolutePath());
                } else {
                    try {
                        ClassNode cn = BytecodeViewer.blindlySearchForClassNode(target);
                        final ClassWriter cw = accept(cn);
                        String contents = Decompiler.PROCYON_DECOMPILER.getDecompiler().decompileClassNode(cn, cw.toByteArray());
                        DiskWriter.replaceFile(output.getAbsolutePath(), contents, false);
                    } catch (Exception e) {
                        BytecodeViewer.handleException(e);
                    }
                }
            } else if (decompiler.equalsIgnoreCase("cfr")) {
                System.out.println("Decompiling " + input.getAbsolutePath() + " with CFR");
                BytecodeViewer.openFiles(new File[]{input}, false);

                Thread.sleep(5 * 1000);

                if (target.equalsIgnoreCase("all")) {
                    Decompiler.CFR_DECOMPILER.getDecompiler().decompileToZip(tempZip.getAbsolutePath(), output.getAbsolutePath());
                } else {
                    try {
                        ClassNode cn = BytecodeViewer.blindlySearchForClassNode(target);
                        final ClassWriter cw = accept(cn);
                        String contents = Decompiler.CFR_DECOMPILER.getDecompiler().decompileClassNode(cn, cw.toByteArray());
                        DiskWriter.replaceFile(output.getAbsolutePath(), contents, false);
                    } catch (Exception e) {
                        BytecodeViewer.handleException(e);
                    }
                }
            } else if (decompiler.equalsIgnoreCase("fernflower")) {
                System.out.println("Decompiling " + input.getAbsolutePath() + " with FernFlower");
                BytecodeViewer.openFiles(new File[]{input}, false);

                Thread.sleep(5 * 1000);

                if (target.equalsIgnoreCase("all")) {
                    Decompiler.FERNFLOWER_DECOMPILER.getDecompiler().decompileToZip(tempZip.getAbsolutePath(), output.getAbsolutePath());
                } else {
                    try {
                        ClassNode cn = BytecodeViewer.blindlySearchForClassNode(target);
                        final ClassWriter cw = accept(cn);
                        String contents = Decompiler.FERNFLOWER_DECOMPILER.getDecompiler().decompileClassNode(cn, cw.toByteArray());
                        DiskWriter.replaceFile(output.getAbsolutePath(), contents, false);
                    } catch (Exception e) {
                        BytecodeViewer.handleException(e);
                    }
                }
            } else if (decompiler.equalsIgnoreCase("krakatau")) {
                System.out.println("Decompiling " + input.getAbsolutePath() + " with Krakatau");
                BytecodeViewer.openFiles(new File[]{input}, false);

                Thread.sleep(5 * 1000);

                if (target.equalsIgnoreCase("all")) {
                    Decompiler.KRAKATAU_DECOMPILER.getDecompiler().decompileToZip(tempZip.getAbsolutePath(), output.getAbsolutePath());
                } else {
                    try {
                        ClassNode cn = BytecodeViewer.blindlySearchForClassNode(target);
                        final ClassWriter cw = accept(cn);
                        String contents = Decompiler.KRAKATAU_DECOMPILER.getDecompiler().decompileClassNode(cn, cw.toByteArray());
                        DiskWriter.replaceFile(output.getAbsolutePath(), contents, false);
                    } catch (Exception e) {
                        BytecodeViewer.handleException(e);
                    }
                }
            } else if (decompiler.equalsIgnoreCase("krakatau-bytecode")) {
                System.out.println("Decompiling " + input.getAbsolutePath() + " with Krakatau-Bytecode");
                BytecodeViewer.openFiles(new File[]{input}, false);

                Thread.sleep(5 * 1000);

                if (target.equalsIgnoreCase("all")) {
                    System.out.println("Coming soon.");
                    //Decompiler.krakatauDA.decompileToZip(output.getAbsolutePath());
                } else {
                    try {
                        ClassNode cn = BytecodeViewer.blindlySearchForClassNode(target);
                        final ClassWriter cw = accept(cn);
                        String contents = Decompiler.KRAKATAU_DISASSEMBLER.getDecompiler().decompileClassNode(cn, cw.toByteArray());
                        DiskWriter.replaceFile(output.getAbsolutePath(), contents, false);
                    } catch (Exception e) {
                        BytecodeViewer.handleException(e);
                    }
                }
            } else if (decompiler.equalsIgnoreCase("jd-gui")) {
                System.out.println("Decompiling " + input.getAbsolutePath() + " with JD-GUI");
                BytecodeViewer.openFiles(new File[]{input}, false);

                Thread.sleep(5 * 1000);

                if (target.equalsIgnoreCase("all")) {
                    System.out.println("Coming soon.");
                    //Decompiler.jdgui.decompileToZip(output.getAbsolutePath());
                } else {
                    try {
                        ClassNode cn = BytecodeViewer.blindlySearchForClassNode(target);
                        final ClassWriter cw = accept(cn);
                        String contents = Decompiler.JD_DECOMPILER.getDecompiler().decompileClassNode(cn, cw.toByteArray());
                        DiskWriter.replaceFile(output.getAbsolutePath(), contents, false);
                    } catch (Exception e) {
                        BytecodeViewer.handleException(e);
                    }
                }
            } else if (decompiler.equalsIgnoreCase("smali")) {
                System.out.println("Decompiling " + input.getAbsolutePath() + " with Smali");
                BytecodeViewer.openFiles(new File[]{input}, false);

                Thread.sleep(5 * 1000);

                if (target.equalsIgnoreCase("all")) {
                    System.out.println("Coming soon.");
                    //Decompiler.smali.decompileToZip(output.getAbsolutePath());
                } else {
                    try {
                        ClassNode cn = BytecodeViewer.blindlySearchForClassNode(target);
                        final ClassWriter cw = accept(cn);
                        String contents = Decompiler.SMALI_DISASSEMBLER.getDecompiler().decompileClassNode(cn, cw.toByteArray());
                        DiskWriter.replaceFile(output.getAbsolutePath(), contents, false);
                    } catch (Exception e) {
                        BytecodeViewer.handleException(e);
                    }
                }
            } else if (decompiler.equalsIgnoreCase("jadx")) {
                System.out.println("Decompiling " + input.getAbsolutePath() + " with JADX");
                BytecodeViewer.openFiles(new File[]{input}, false);

                Thread.sleep(5 * 1000);

                if (target.equalsIgnoreCase("all")) {
                    System.out.println("Coming soon.");
                    //Decompiler.smali.decompileToZip(output.getAbsolutePath());
                } else {
                    try {
                        ClassNode cn = BytecodeViewer.blindlySearchForClassNode(target);
                        final ClassWriter cw = accept(cn);
                        String contents = Decompiler.JADX_DECOMPILER.getDecompiler().decompileClassNode(cn, cw.toByteArray());
                        DiskWriter.replaceFile(output.getAbsolutePath(), contents, false);
                    } catch (Exception e) {
                        BytecodeViewer.handleException(e);
                    }
                }
            }

            System.out.println("Finished.");
            System.out.println("Bytecode Viewer " + VERSION + " [CLI] - Created by @Konloch - https://bytecodeviewer.com");
            Configuration.canExit = true;
            System.exit(0);
        } catch (Exception e) {
            BytecodeViewer.handleException(e);
        }
    }

    public static ClassWriter accept(ClassNode cn) {
        ClassWriter cw = new ClassWriter(0);
        try {
            cn.accept(cw);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Thread.sleep(200);
                cn.accept(cw);
            } catch (InterruptedException ignored) {
            }
        }
        return cw;
    }
}
