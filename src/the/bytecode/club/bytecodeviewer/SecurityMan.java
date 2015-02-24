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

	public boolean blocking = true;
	@Override
    public void checkExec(String cmd) {
		if(blocking)
			throw new SecurityException("BCV is awesome.");
    }
	@Override
    public void checkListen(int port) {
		if(blocking)
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
