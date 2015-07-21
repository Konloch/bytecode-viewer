package the.bytecode.club.bytecodeviewer;

import java.io.FileDescriptor;
import java.net.InetAddress;
import java.security.Permission;

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
			executedClass.equals("the.bytecode.club.bytecodeviewer.decompilers.KrakatauDisassambler") ||
			executedClass.equals("the.bytecode.club.bytecodeviewer.compilers.KrakatauAssembler") ||
			executedClass.equals("the.bytecode.club.bytecodeviewer.BytecodeViewer"))
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
			"pypy"
		};
		boolean allow = false;
		
		for(String s : whitelist) {
			if(cmd.contains(s))
				allow = true;
		}
		
		if(allow && !blocking) {
			System.out.println("Allowing exec:" + cmd);
		} else throw new SecurityException("BCV is awesome.");
    }
	@Override
    public void checkListen(int port) {
		throw new SecurityException("BCV is awesome.");
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
