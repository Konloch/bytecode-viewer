package the.bytecode.club.bytecodeviewer.util;

import the.bytecode.club.bytecodeviewer.Configuration;

import java.io.FileDescriptor;
import java.net.InetAddress;
import java.security.Permission;

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
    private int blocking = 1; //TODO replace with a more secure system
    private int silentExec = 1;
    private boolean printing = false;
    private boolean printingPackage = false;
    
    public void silenceExec(boolean b) {
        silentExec += (b ? 1 : -1);
    }
    
    public void resumeBlocking() {
        blocking++;
    }
    
    public void pauseBlocking() { //slightly safer security system than just a public static boolean being toggled
        String executedClass = Thread.currentThread().getStackTrace()[2].getClassName();
        if (executedClass.equals("the.bytecode.club.bytecodeviewer.decompilers.impl.KrakatauDecompiler") ||
                executedClass.equals("the.bytecode.club.bytecodeviewer.decompilers.impl.KrakatauDisassembler") ||
                executedClass.equals("the.bytecode.club.bytecodeviewer.decompilers.impl.CFRDecompiler") ||
                executedClass.equals("the.bytecode.club.bytecodeviewer.decompilers.impl.ProcyonDecompiler") ||
                executedClass.equals("the.bytecode.club.bytecodeviewer.decompilers.impl.FernFlowerDecompiler") ||
                executedClass.equals("the.bytecode.club.bytecodeviewer.decompilers.impl.JDGUIDecompiler") ||
                executedClass.equals("the.bytecode.club.bytecodeviewer.compilers.impl.KrakatauAssembler") ||
                executedClass.equals("the.bytecode.club.bytecodeviewer.resources.ExternalResources") ||
                executedClass.equals("the.bytecode.club.bytecodeviewer.util.Enjarify") ||
                executedClass.equals("the.bytecode.club.bytecodeviewer.util.APKTool") ||
                executedClass.equals("the.bytecode.club.bytecodeviewer.BytecodeViewer") ||
                executedClass.equals("the.bytecode.club.bytecodeviewer.Constants") ||
                executedClass.equals("the.bytecode.club.bytecodeviewer.compilers.impl.JavaCompiler")) {
            blocking--;
        } else for (StackTraceElement stackTraceElements : Thread.currentThread().getStackTrace()) {
            System.out.println(stackTraceElements.getClassName());
        }
    }
    
    public void setPrinting(boolean printing)
    {
        this.printing = printing;
    }
    
    public void setPrintingPackage(boolean printingPackage)
    {
        this.printingPackage = printingPackage;
    }
    
    @Override
    public void checkExec(String cmd) {
        String[] whitelist = {
                "attrib",
                "python",
                "pypy",
                "java",
                "brut_util",
        };
        boolean allow = false;

        for (String s : whitelist)
            if (cmd.contains(s)) {
                allow = true;
                break;
            }
        
        if (allow && blocking <= 0)
        {
            if(silentExec >= 1)
                System.out.println("Allowing exec: " + cmd);
        }
        else throw new SecurityException("BCV is awesome, blocking(" + blocking + ") exec " + cmd);
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
    
    @SuppressWarnings("deprecation")
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
    }
}
