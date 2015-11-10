package the.bytecode.club.bytecodeviewer;

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
 *
 */

public class SecurityMan extends SecurityManager {

	public void setBlocking() {
		blocking = true;
	}
	
	public void stopBlocking() { //slightly safer security system than just a public static boolean being toggled
		String executedClass = Thread.currentThread().getStackTrace()[2].getClassName();
		if(	executedClass.equals("the.bytecode.club.bytecodeviewer.decompilers.KrakatauDecompiler") ||
			executedClass.equals("the.bytecode.club.bytecodeviewer.decompilers.KrakatauDisassembler") ||
			executedClass.equals("the.bytecode.club.bytecodeviewer.decompilers.CFRDecompiler") ||
			executedClass.equals("the.bytecode.club.bytecodeviewer.decompilers.ProcyonDecompiler") ||
			executedClass.equals("the.bytecode.club.bytecodeviewer.decompilers.FernFlowerDecompiler") ||
			executedClass.equals("the.bytecode.club.bytecodeviewer.decompilers.JDGUIDecompiler") ||
			executedClass.equals("the.bytecode.club.bytecodeviewer.compilers.KrakatauAssembler") ||
			executedClass.equals("the.bytecode.club.bytecodeviewer.Enjarify") ||
			executedClass.equals("the.bytecode.club.bytecodeviewer.BytecodeViewer") ||
			executedClass.equals("the.bytecode.club.bytecodeviewer.compilers.JavaCompiler"))
		{
			blocking = false;
		} else for(StackTraceElement stackTraceElements : Thread.currentThread().getStackTrace()) {
			System.out.println(stackTraceElements.getClassName());
		}
	}
	
	private boolean blocking = true; //might be insecure due to assholes targeting BCV, however that's highly unlikely.
	
	@Override
    public void checkExec(String cmd) {
		String[] whitelist = {
			"attrib",
			"python",
			"pypy",
			"java"
		};
		boolean allow = false;
		
		for(String s : whitelist) {
			if(cmd.contains(s))
				allow = true;
		}
		
		if(allow && !blocking) {
			System.out.println("Allowing exec:" + cmd);
		} else throw new SecurityException("BCV is awesome, blocking " + cmd);
    }
	@Override
    public void checkListen(int port) {
		throw new SecurityException("BCV is awesome, blocking port "+port+" from listening");
    }
	@Override
    public void checkPermission(Permission perm) { //expand eventually
    }
	@Override
    public void checkPermission(Permission perm, Object context) {//expand eventually
    }
	@Override public void checkAccess(Thread t) {}
	@Override public void checkAccept(String host, int port) {}
	@Override public void checkAccess(ThreadGroup g) {}
	@Override public void checkAwtEventQueueAccess() {}
	@Override public void checkConnect(String host, int port) {}
	@Override public void checkConnect(String host, int port, Object context) {}
	@Override public void checkCreateClassLoader() {}
	@Override public void checkDelete(String file) {}
	@Override public void checkExit(int status) {}
	@Override public void checkLink(String lib) {}
	@Override public void checkMemberAccess(Class<?> clazz, int which) {}
	@Override public void checkMulticast(InetAddress maddr) {}
	@Override public void checkMulticast(InetAddress maddr, byte ttl) {}
	@Override public void checkPackageAccess(String pkg) {}
	@Override public void checkPackageDefinition(String pkg) {}
	@Override public void checkPrintJobAccess() {}
	@Override public void checkPropertiesAccess() {}
	@Override public void checkPropertyAccess(String key) {}
	@Override public void checkRead(FileDescriptor fd) {}
	@Override public void checkRead(String file) {}
	@Override public void checkRead(String file, Object context) {}
	@Override public void checkSecurityAccess(String target) {}
	@Override public void checkSetFactory() {}
	@Override public void checkSystemClipboardAccess() {}
	@Override public void checkWrite(FileDescriptor fd) {}
	@Override public void checkWrite(String file) {}

}
