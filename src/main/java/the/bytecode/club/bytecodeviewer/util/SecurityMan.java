package the.bytecode.club.bytecodeviewer.util;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetAddress;
import java.security.Permission;
import java.util.concurrent.atomic.AtomicInteger;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.Constants;
import the.bytecode.club.bytecodeviewer.compilers.impl.JavaCompiler;
import the.bytecode.club.bytecodeviewer.compilers.impl.KrakatauAssembler;
import the.bytecode.club.bytecodeviewer.decompilers.impl.CFRDecompiler;
import the.bytecode.club.bytecodeviewer.decompilers.impl.FernFlowerDecompiler;
import the.bytecode.club.bytecodeviewer.decompilers.impl.JDGUIDecompiler;
import the.bytecode.club.bytecodeviewer.decompilers.impl.KrakatauDecompiler;
import the.bytecode.club.bytecodeviewer.decompilers.impl.KrakatauDisassembler;
import the.bytecode.club.bytecodeviewer.decompilers.impl.ProcyonDecompiler;
import the.bytecode.club.bytecodeviewer.resources.ExternalResources;

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
 * An awesome security manager.
 *
 * @author Konloch
 */

public class SecurityMan extends SecurityManager
{
    private static final boolean disableExecSandbox = true;
    private static final boolean disableDiskWriteSandbox = true;
    
    private final AtomicInteger silentExec = new AtomicInteger(1);
    private boolean printing = false;
    private boolean printingPackage = false;
    
    public void silenceExec(boolean b) {
        silentExec.addAndGet(b ? 1 : -1);
    }
    
    public void setPrinting(boolean printing)
    {
        this.printing = printing;
    }
    
    public void setPrintingPackage(boolean printingPackage)
    {
        this.printingPackage = printingPackage;
    }
    
    /**
     * Attempts to secure untrusted code
     *
     * When paired with checkWrite it should prevent most escapes
     * JNI is still possible so make sure to block checkLink as well //TODO for BCV
     *
     * Rewritten on 07/19/2021
     *
     * @author Konloch
     */
    @Override
    public void checkExec(String cmd)
    {
        //This was disabled on 02-13-2022, at some point in the future I will fix the compatibility issues and re-enable it.
        if(disableExecSandbox)
            return;
        
        //incoming command must contain the following or it will be automatically denied
        String[] execWhitelist =
        {
                "attrib",
                "python",
                "pypy",
                "java",
                "brut_util",
        };
        
        //the goal is to make this true
        boolean allow = false;
        //while keeping this false
        boolean blocked = false;

        //normalize all command paths
        final String normalizedPath;
        try
        {
            normalizedPath = new File(cmd.toLowerCase()).getCanonicalPath();
        }
        catch (IOException e)
        {
            throw new SecurityException(e);
        }
    
        //don't trust .jar file extensions being executed
        if(normalizedPath.endsWith(".jar"))
            blocked = true;
        
        //don't trust .js file extensions being executed
        else if(normalizedPath.endsWith(".js"))
            blocked = true;
        
        //block anything executing in system temp
        else if(normalizedPath.startsWith(Constants.systemTempDirectory.toLowerCase()))
            blocked = true;
        
        //can only write into BCV dir, so anything executing from here has probably been dropped
        try
        {
            if(normalizedPath.startsWith(Constants.BCVDir.getCanonicalPath().toLowerCase()))
                blocked = true;
        }
        catch (IOException e)
        {
            throw new SecurityException(e);
        }
    
        //filter exec whitelist
        for (String whiteList : execWhitelist)
        {
            if (normalizedPath.contains(whiteList))
            {
                allow = true;
                break;
            }
        }
        
        //filter class whitelist
        boolean validClassCall = false;
        //JDK-8
        if(canClassExecute(Thread.currentThread().getStackTrace()[3].getClassName()))
            validClassCall = true;
        //JDK-15
        else if(canClassExecute(Thread.currentThread().getStackTrace()[4].getClassName()))
            validClassCall = true;
        //JDK-8
        else if(canClassExecute(Thread.currentThread().getStackTrace()[6].getClassName()))
            validClassCall = true;
        //JDK-15
        else if(canClassExecute(Thread.currentThread().getStackTrace()[7].getClassName()))
            validClassCall = true;
        else
        {
            int index = 0;
            for (StackTraceElement stackTraceElements : Thread.currentThread().getStackTrace())
            {
                System.out.println(index++ + ":" + stackTraceElements.getClassName());
            }
        }
        
        //log exec if allowed
        if (allow && validClassCall && !blocked)
        {
            if(silentExec.get() >= 1)
                System.err.println("Allowing exec: " + cmd);
        } //throw exception stopping execution
        else throw new SecurityException("BCV is awesome! Blocking exec: " + cmd);
    }
    
    /**
     * Class Whitelist goes here
     */
    private boolean canClassExecute(String fullyQualifiedClassName)
    {
        return  fullyQualifiedClassName.equals(KrakatauDecompiler.class.getCanonicalName()) ||
                fullyQualifiedClassName.equals(KrakatauDisassembler.class.getCanonicalName()) ||
                fullyQualifiedClassName.equals(CFRDecompiler.class.getCanonicalName()) ||
                fullyQualifiedClassName.equals(ProcyonDecompiler.class.getCanonicalName()) ||
                fullyQualifiedClassName.equals(FernFlowerDecompiler.class.getCanonicalName()) ||
                fullyQualifiedClassName.equals(JDGUIDecompiler.class.getCanonicalName()) ||
                fullyQualifiedClassName.equals(KrakatauAssembler.class.getCanonicalName()) ||
                fullyQualifiedClassName.equals(ExternalResources.class.getCanonicalName()) ||
                fullyQualifiedClassName.equals(Enjarify.class.getCanonicalName()) ||
                fullyQualifiedClassName.equals(APKTool.class.getCanonicalName()) ||
                fullyQualifiedClassName.equals(BytecodeViewer.class.getCanonicalName()) ||
                fullyQualifiedClassName.equals(Constants.class.getCanonicalName()) ||
                fullyQualifiedClassName.equals(JavaCompiler.class.getCanonicalName());
    }

    @Override
    public void checkListen(int port) {
        throw new SecurityException("BCV is awesome, blocking port " + port + " from listening");
    }

    @Override
    public void checkPermission(Permission perm) { //expand eventually
    }

    @Override
    public void checkPermission(Permission perm, Object context) {//expand eventually
    }

    @Override
    public void checkAccess(Thread t) {
    }

    @Override
    public void checkAccept(String host, int port) {
    }

    @Override
    public void checkAccess(ThreadGroup g) {
    }
    
    @SuppressWarnings("deprecation")
    public void checkAwtEventQueueAccess() {
    }

    @Override
    public void checkConnect(String host, int port) {
        if(printing)
            System.out.println("Connecting to: " + host + ":" + port);
    }

    @Override
    public void checkConnect(String host, int port, Object context) {
    }

    @Override
    public void checkCreateClassLoader() {
    }

    @Override
    public void checkDelete(String file) {
        if(printing)
            System.out.println("Deleting: " + file);
    }

    @Override
    public void checkExit(int status) {
        if (!Configuration.canExit) {
            throw new SecurityException("BCV is awesome, blocking System.exit(" + status + ");");
        }
    }

    @Override
    public void checkLink(String lib) {
        if(printing)
            System.out.println("Linking: " + lib);
    }
    
    @SuppressWarnings("deprecation")
    public void checkMemberAccess(Class<?> clazz, int which) {
    }

    @Override
    public void checkMulticast(InetAddress maddr) {
    }
    
    @SuppressWarnings("deprecation")
    public void checkMulticast(InetAddress maddr, byte ttl) {
    }

    public void checkPackageAccess(String pkg) {
        if(printingPackage)
            System.out.println("Accessing: " + pkg);
    }

    @Override
    public void checkPackageDefinition(String pkg) {
    }

    @Override
    public void checkPrintJobAccess() {
    }

    @Override
    public void checkPropertiesAccess() {
    }

    @Override
    public void checkPropertyAccess(String key) {
    }

    @Override
    public void checkRead(FileDescriptor fd) {
    }

    @Override
    public void checkRead(String file) {
        if(printing)
            System.out.println("Reading: " + file);
    }

    @Override
    public void checkRead(String file, Object context) {
    }

    @Override
    public void checkSecurityAccess(String target) {
    }

    @Override
    public void checkSetFactory() {
    }
    
    @SuppressWarnings("deprecation")
    public void checkSystemClipboardAccess() {
    }

    @Override
    public void checkWrite(FileDescriptor fd) {
    }

    @Override
    public void checkWrite(String file) {
        if(printing)
            System.out.println("Writing: " + file);
    
        //This was disabled on 02-13-2022, at some point in the future I will fix the compatibility issues and re-enable it.
        if(disableDiskWriteSandbox)
            return;
        
        try
        {
            //can only export as the following extensions
            if(file.endsWith(".zip") || file.endsWith(".jar") || file.endsWith(".apk")
                    || file.endsWith(".dex") || file.endsWith(".class") || file.endsWith("js")
                    || file.endsWith(".java") || file.endsWith(".gy") || file.endsWith(".bcv")
                    || file.endsWith(".json") || file.endsWith(".txt") || file.endsWith(".log"))
                return;
            
            //can only write into BCV dir
            if(file.startsWith(Constants.BCVDir.getCanonicalPath()))
                return;
            
            //can only write into system temp
            if(file.startsWith(Constants.systemTempDirectory))
                return;
        }
        catch (IOException e)
        {
            throw new SecurityException(e);
        }

        throw new SecurityException("BCV is awesome, blocking write(" + file + ");");
    }
}
