package the.bytecode.club.bytecodeviewer.plugin.preinstalled;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.api.BytecodeHook;
import the.bytecode.club.bytecodeviewer.api.Plugin;
import the.bytecode.club.bytecodeviewer.api.PluginConsole;
import the.bytecode.club.bytecodeviewer.gui.GraphicialReflectionKit;

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
 * EZ Injection - This plugin is designed to provide a graphical way for the
 * user to easily change the access modifiers of all fields/methods, insert
 * hooks into all functions, and invoke the main function. It also contains an
 * option to launch the graphical reflection kit, which is pretty much a GUI for
 * reflection.
 *
 * @author Konloch
 */

public class EZInjection extends Plugin {

    public static ArrayList<BytecodeHook> hookArray = new ArrayList<BytecodeHook>();
    private static String version = "1.0";
    private static PluginConsole gui = new PluginConsole("EZ Injection v" + version);
    private boolean accessModifiers, injectHooks, invokeMethod, useProxy,
            launchKit, console;
    public static boolean sandboxSystem, sandboxRuntime, printCmdL;
    private static boolean debugHooks, all = false;
    private String invokeMethodInformation, proxy;

    private static String[] debugClasses;

    public EZInjection(boolean accessModifiers, boolean injectHooks,
                       boolean debugHooks, boolean invokeMethod,
                       String invokeMethodInformation, boolean sandboxRuntime,
                       boolean sandboxSystem, String debugClasses, String proxy,
                       boolean useProxy, boolean launchKit, boolean console,
                       boolean printCmdL) {
        the.bytecode.club.bytecodeviewer.api.BytecodeViewer
                .createNewClassNodeLoaderInstance();
        this.accessModifiers = accessModifiers;
        this.injectHooks = injectHooks;
        EZInjection.debugHooks = debugHooks;
        this.invokeMethod = invokeMethod;
        this.invokeMethodInformation = invokeMethodInformation
                + "([Ljava/lang/String;)V";
        EZInjection.sandboxRuntime = sandboxRuntime;
        EZInjection.sandboxSystem = sandboxSystem;
        if (debugClasses.equals("*"))
            EZInjection.all = true;
        else
            EZInjection.debugClasses = debugClasses.split(",");
        this.proxy = proxy;
        this.useProxy = useProxy;
        this.launchKit = launchKit;
        this.console = console;
        EZInjection.printCmdL = printCmdL;
    }

    public static void setProxy(String host, String port) {
        System.setProperty("java.net.useSystemProxies", "true");
        System.setProperty("socksProxyHost", host);
        System.setProperty("socksProxyPort", port);
    }

    private static String lastMessage = "";

    public static void hook(String info) {
        for (BytecodeHook hook : hookArray)
            hook.callHook(info);

        if (debugHooks) {
            if (lastMessage.equals(info)) // just a small anti spam measurement
                return;

            lastMessage = info;

            boolean print = all;

            if (!all && debugClasses.length >= 1) {
                for (String s : debugClasses) {
                    if (info.split("\\.")[0].equals(s.replaceAll("\\.", "/")))
                        print = true;
                }
            }

            if (print)
                print("Method call: " + info);
        }
    }

    public static void print(String message) {
        if (printCmdL)
            System.out.println(message);

        if (gui.isVisible())
            gui.appendText(message);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void execute(ArrayList<ClassNode> classNodeList) {
        BytecodeViewer.viewer.setIcon(true);
        gui.setText("");

        if (console)
            gui.setVisible(true);

        if (accessModifiers)
            print("Setting all of the access modifiers to public/public static.");
        if (injectHooks)
            print("Injecting hook...");
        if (debugHooks)
            print("Hooks are debugging.");
        else if (injectHooks)
            print("Hooks are not debugging.");
        else
            print("Hooks are disabled completely.");
        if (useProxy)
            print("Forcing proxy as '" + proxy + "'.");
        if (launchKit)
            print("Launching the Graphicial Reflection Kit upon a succcessful invoke of the main method.");

        for (ClassNode classNode : classNodeList) {
            for (Object o : classNode.fields.toArray()) {
                FieldNode f = (FieldNode) o;

                if (accessModifiers) {
                    if (f.access == Opcodes.ACC_PRIVATE
                            || f.access == Opcodes.ACC_PROTECTED)
                        f.access = Opcodes.ACC_PUBLIC;

                    if (f.access == Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC
                            || f.access == Opcodes.ACC_PROTECTED
                            + Opcodes.ACC_STATIC)
                        f.access = Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC;

                    if (f.access == Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL
                            || f.access == Opcodes.ACC_PROTECTED
                            + Opcodes.ACC_FINAL)
                        f.access = Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL;

                    if (f.access == Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL
                            + Opcodes.ACC_STATIC
                            || f.access == Opcodes.ACC_PROTECTED
                            + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC)
                        f.access = Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL
                                + Opcodes.ACC_STATIC;
                }
            }
            for (Object o : classNode.methods.toArray()) {
                MethodNode m = (MethodNode) o;

                if (accessModifiers) {
                    if (m.access == Opcodes.ACC_PRIVATE
                            || m.access == Opcodes.ACC_PROTECTED)
                        m.access = Opcodes.ACC_PUBLIC;

                    if (m.access == Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC
                            || m.access == Opcodes.ACC_PROTECTED
                            + Opcodes.ACC_STATIC)
                        m.access = Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC;

                    if (m.access == Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL
                            || m.access == Opcodes.ACC_PROTECTED
                            + Opcodes.ACC_FINAL)
                        m.access = Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL;

                    if (m.access == Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL
                            + Opcodes.ACC_STATIC
                            || m.access == Opcodes.ACC_PROTECTED
                            + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC)
                        m.access = Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL
                                + Opcodes.ACC_STATIC;
                }

                if (injectHooks
                        && m.access != Opcodes.ACC_ABSTRACT
                        && m.access != Opcodes.ACC_PUBLIC
                        + Opcodes.ACC_ABSTRACT
                        && m.access != Opcodes.ACC_PRIVATE
                        + Opcodes.ACC_ABSTRACT
                        && m.access != Opcodes.ACC_PROTECTED
                        + Opcodes.ACC_ABSTRACT
                        && m.access != Opcodes.ACC_FINAL + Opcodes.ACC_ABSTRACT
                        && m.access != Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL
                        + Opcodes.ACC_ABSTRACT
                        && m.access != Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL
                        + Opcodes.ACC_ABSTRACT
                        && m.access != Opcodes.ACC_PROTECTED
                        + Opcodes.ACC_FINAL + Opcodes.ACC_ABSTRACT
                        && m.access != Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL
                        + Opcodes.ACC_STATIC + Opcodes.ACC_ABSTRACT
                        && m.access != Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL
                        + Opcodes.ACC_STATIC + Opcodes.ACC_ABSTRACT
                        && m.access != Opcodes.ACC_PROTECTED
                        + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC
                        + Opcodes.ACC_ABSTRACT) {
                    boolean inject = true;
                    if (m.instructions.size() >= 2
                            && m.instructions.get(1) instanceof MethodInsnNode) {
                        MethodInsnNode mn = (MethodInsnNode) m.instructions
                                .get(1);
                        if (mn.owner
                                .equals("the/bytecode/club/bytecodeviewer/plugins/EZInjection")) // already
                            // been
                            // injected
                            inject = false;
                    }
                    if (inject) {
                        // make this function grab parameters eventually
                        m.instructions
                                .insert(new MethodInsnNode(
                                        Opcodes.INVOKESTATIC,
                                        "the/bytecode/club/bytecodeviewer/plugins/EZInjection",
                                        "hook", "(Ljava/lang/String;)V"));
                        m.instructions.insert(new LdcInsnNode(classNode.name
                                + "." + m.name + m.desc));
                    }
                }
            }
        }

        if (useProxy) {
            try {
                String[] split = proxy.split(":");
                setProxy(split[0], split[1]);
            } catch (Exception e) {
                // ignore
            }
        }

        print("Done setting up.");

        setFinished();

        if (invokeMethod) {
            for (ClassNode cn : BytecodeViewer.getLoadedClasses())
                // load all the classnodes into the classloader
                the.bytecode.club.bytecodeviewer.api.BytecodeViewer
                        .getClassNodeLoader().addClass(cn);

            print("Invoking " + invokeMethodInformation + ":"
                    + BytecodeViewer.nl + BytecodeViewer.nl);

            for (ClassNode classNode : classNodeList) {
                for (Object o : classNode.methods.toArray()) {
                    MethodNode m = (MethodNode) o;
                    String methodInformation = classNode.name + "." + m.name
                            + m.desc;
                    if (invokeMethodInformation.equals(methodInformation)) {
                        for (Method m2 : the.bytecode.club.bytecodeviewer.api.BytecodeViewer
                                .getClassNodeLoader().nodeToClass(classNode)
                                .getMethods()) {
                            if (m2.getName().equals(m.name)) {
                                try {
                                    m2.invoke(classNode.getClass()
                                                    .newInstance(),
                                            (Object[]) new String[1]);
                                    if (launchKit)
                                        new GraphicialReflectionKit()
                                                .setVisible(true);
                                } catch (Exception e) {
                                    StringWriter sw = new StringWriter();
                                    e.printStackTrace(new PrintWriter(sw));
                                    e.printStackTrace();
                                    print(sw.toString());
                                }
                            }
                        }
                    }
                }
            }
        }

        BytecodeViewer.viewer.setIcon(false);
    }
}
