package the.bytecode.club.bytecodeviewer.plugin.preinstalled;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.api.BCV;
import the.bytecode.club.bytecodeviewer.api.BytecodeHook;
import the.bytecode.club.bytecodeviewer.api.Plugin;
import the.bytecode.club.bytecodeviewer.api.PluginConsole;
import the.bytecode.club.bytecodeviewer.gui.plugins.GraphicalReflectionKit;

import static the.bytecode.club.bytecodeviewer.Constants.nl;

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

public class EZInjection extends Plugin
{
    public static List<BytecodeHook> hookArray = new ArrayList<>();
    private static final String version = "1.0";
    private final boolean accessModifiers;
    private final boolean injectHooks;
    private final boolean invokeMethod;
    private final boolean useProxy;
    private final boolean launchKit;
    private final boolean console;
    public static boolean sandboxSystem, sandboxRuntime, printCmdL;
    private static boolean debugHooks, all = false;
    private final String invokeMethodInformation;
    private final String proxy;

    private static String[] debugClasses;

    public EZInjection(boolean accessModifiers, boolean injectHooks,
                       boolean debugHooks, boolean invokeMethod,
                       String invokeMethodInformation, boolean sandboxRuntime,
                       boolean sandboxSystem, String debugClasses, String proxy,
                       boolean useProxy, boolean launchKit, boolean console,
                       boolean printCmdL)
    {
        BCV.createNewClassNodeLoaderInstance();
        this.accessModifiers = accessModifiers;
        this.injectHooks = injectHooks;
        EZInjection.debugHooks = debugHooks;
        this.invokeMethod = invokeMethod;
        this.invokeMethodInformation = invokeMethodInformation + "([Ljava/lang/String;)V";
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

    public static void setProxy(String host, String port)
    {
        System.setProperty("java.net.useSystemProxies", "true");
        System.setProperty("socksProxyHost", host);
        System.setProperty("socksProxyPort", port);
    }

    private static String lastMessage = "";

    public static void hook(String info)
    {
        for (BytecodeHook hook : hookArray)
            hook.callHook(info);

        if (debugHooks)
        {
            if (lastMessage.equals(info)) // just a small anti spam measurement
                return;

            lastMessage = info;
            boolean print = all;

            if (!all && debugClasses.length >= 1)
            {
                for (String s : debugClasses)
                {
                    if (info.split("\\.")[0].equals(s.replaceAll("\\.", "/")))
                    {
                        print = true;
                        break;
                    }
                }
            }

            if (print)
                print("Method call: " + info);
        }
    }

    public static void print(String message)
    {
        System.out.println(message);
    }

    @Override
    public void execute(List<ClassNode> classNodeList)
    {
        if(console)
            new PluginConsole("EZ Injection v" + version);

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
    
        //force everything to be public
        for (ClassNode classNode : classNodeList)
        {
            for (Object o : classNode.fields.toArray())
            {
                FieldNode f = (FieldNode) o;

                if (accessModifiers)
                {
                    if (f.access == Opcodes.ACC_PRIVATE
                            || f.access == Opcodes.ACC_PROTECTED)
                        f.access = Opcodes.ACC_PUBLIC;

                    if (f.access == Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC
                            || f.access == Opcodes.ACC_PROTECTED + Opcodes.ACC_STATIC)
                        f.access = Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC;

                    if (f.access == Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL
                            || f.access == Opcodes.ACC_PROTECTED + Opcodes.ACC_FINAL)
                        f.access = Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL;

                    if (f.access == Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC
                            || f.access == Opcodes.ACC_PROTECTED + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC)
                        f.access = Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC;
                }
            }
            
            for (Object o : classNode.methods.toArray())
            {
                MethodNode m = (MethodNode) o;
                if (accessModifiers)
                {
                    if (m.access == Opcodes.ACC_PRIVATE
                            || m.access == Opcodes.ACC_PROTECTED)
                        m.access = Opcodes.ACC_PUBLIC;

                    if (m.access == Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC
                            || m.access == Opcodes.ACC_PROTECTED + Opcodes.ACC_STATIC)
                        m.access = Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC;

                    if (m.access == Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL
                            || m.access == Opcodes.ACC_PROTECTED + Opcodes.ACC_FINAL)
                        m.access = Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL;

                    if (m.access == Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC
                            || m.access == Opcodes.ACC_PROTECTED + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC)
                        m.access = Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC;
                }

                if (injectHooks
                        && m.access != Opcodes.ACC_ABSTRACT
                        && m.access != Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT
                        && m.access != Opcodes.ACC_PRIVATE + Opcodes.ACC_ABSTRACT
                        && m.access != Opcodes.ACC_PROTECTED + Opcodes.ACC_ABSTRACT
                        && m.access != Opcodes.ACC_FINAL + Opcodes.ACC_ABSTRACT
                        && m.access != Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_ABSTRACT
                        && m.access != Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL + Opcodes.ACC_ABSTRACT
                        && m.access != Opcodes.ACC_PROTECTED + Opcodes.ACC_FINAL + Opcodes.ACC_ABSTRACT
                        && m.access != Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC + Opcodes.ACC_ABSTRACT
                        && m.access != Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC + Opcodes.ACC_ABSTRACT
                        && m.access != Opcodes.ACC_PROTECTED + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC + Opcodes.ACC_ABSTRACT)
                {
                    boolean inject = true;
                    if (m.instructions.size() >= 2
                            && m.instructions.get(1) instanceof MethodInsnNode)
                    {
                        MethodInsnNode mn = (MethodInsnNode) m.instructions.get(1);
    
                        // already been injected
                        if (mn.owner.equals(EZInjection.class.getName().replace(".", "/")))
                            inject = false;
                    }
                    
                    if (inject)
                    {
                        // make this function grab parameters eventually
                        m.instructions
                                .insert(new MethodInsnNode(
                                        Opcodes.INVOKESTATIC,
                                        EZInjection.class.getName().replace(".", "/"),
                                        "hook", "(Ljava/lang/String;)V"));
                        m.instructions.insert(new LdcInsnNode(classNode.name
                                + "." + m.name + m.desc));
                    }
                }
            }
        }

        if (useProxy)
        {
            try
            {
                String[] split = proxy.split(":");
                setProxy(split[0], split[1]);
            } catch (Exception e) {
                // ignore
            }
        }

        print("Done setting up.");

        setFinished();

        if (invokeMethod)
        {
            //start print debugging
            BytecodeViewer.sm.setPrinting(true);
            
            // load all the classnodes into the classloader
            for (ClassNode cn : BytecodeViewer.getLoadedClasses())
                BCV.getClassNodeLoader().addClass(cn);

            print("Attempting to find " + invokeMethodInformation + ":" + nl + nl);

            for (ClassNode classNode : classNodeList)
            {
                for (Object o : classNode.methods.toArray())
                {
                    MethodNode m = (MethodNode) o;
                    String methodInformation = classNode.name + "." + m.name + m.desc;
                    
                    if (invokeMethodInformation.equals(methodInformation))
                    {
                        for (Method m2 : BCV
                                .getClassNodeLoader().nodeToClass(classNode)
                                .getMethods())
                        {
                            if (m2.getName().equals(m.name))
                            {
                                print("Invoking " + invokeMethodInformation + ":" + nl + nl);
                                
                                GraphicalReflectionKit kit = launchKit ? new GraphicalReflectionKit() : null;
                                try
                                {
                                    if(kit != null)
                                        kit.setVisible(true);
                                    
                                    m2.invoke(classNode.getClass().getDeclaredConstructor().newInstance(), (Object[]) new String[1]);
                                    
                                    print("Finished running " + invokeMethodInformation);
                                }
                                catch (Exception e)
                                {
                                    StringWriter sw = new StringWriter();
                                    e.printStackTrace(new PrintWriter(sw));
                                    e.printStackTrace();
                                    print(sw.toString());
                                }
                                finally
                                {
                                    //disable print debugging
                                    BytecodeViewer.sm.setPrinting(false);
                                    
                                    if(kit != null)
                                        kit.setVisible(false);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
