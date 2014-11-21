package the.bytecode.club.bytecodeviewer;

import java.io.IOException;

import the.bytecode.club.bytecodeviewer.plugins.EZInjection;

public class RuntimeOverride {
    private static RuntimeOverride currentRuntime = new RuntimeOverride();

    public static RuntimeOverride getRuntime() {
        return currentRuntime;
    }

    public void exit(int status) {
        if(EZInjection.sandboxSystem) {
        	EZInjection.exitR(status);
        } else {
        	Runtime.getRuntime().exit(status);
        }
    }

    public void addShutdownHook(Thread hook) {
    	Runtime.getRuntime().addShutdownHook(hook);
    }

    public boolean removeShutdownHook(Thread hook) {
    	return Runtime.getRuntime().removeShutdownHook(hook);
    }

    public void halt(int status) {
    	Runtime.getRuntime().halt(status);
    }
    
    public Process exec(String command) throws IOException {
        if(EZInjection.sandboxSystem) {
        	EZInjection.announceSystem(command);
        	return null;
        } else {
        	return Runtime.getRuntime().exec(command);
        }
    }

    /*public Process exec(String command, String[] envp) throws IOException {
        return exec(command, envp, null);
    }
    
    public Process exec(String command, String[] envp, File dir)
        throws IOException {
        if (command.length() == 0)
            throw new IllegalArgumentException("Empty command");

        StringTokenizer st = new StringTokenizer(command);
        String[] cmdarray = new String[st.countTokens()];
        for (int i = 0; st.hasMoreTokens(); i++)
            cmdarray[i] = st.nextToken();
        return exec(cmdarray, envp, dir);
    }
    
    public Process exec(String cmdarray[]) throws IOException {
        return exec(cmdarray, null, null);
    }
    
    public Process exec(String[] cmdarray, String[] envp) throws IOException {
        return exec(cmdarray, envp, null);
    }
    
    public Process exec(String[] cmdarray, String[] envp, File dir)
        throws IOException {
        return new ProcessBuilder(cmdarray)
            .environment(envp)
            .directory(dir)
            .start();
    }
    
    public native int availableProcessors();
    
    public native long freeMemory();

    public native long totalMemory();

    public native long maxMemory();

    public void gc() {
    	Runtime.getRuntime().gc();
    }

    public void runFinalization() {
    	Runtime.getRuntime().runFinalization();
    }
    
    public native void traceInstructions(boolean on);
    
    public native void traceMethodCalls(boolean on);

    public void load(String filename) {
        load0(Reflection.getCallerClass(), filename);
    }

    synchronized void load0(Class fromClass, String filename) {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkLink(filename);
        }
        if (!(new File(filename).isAbsolute())) {
            throw new UnsatisfiedLinkError(
                "Expecting an absolute path of the library: " + filename);
        }
        ClassLoader.loadLibrary(fromClass, filename, true);
    }

    public void loadLibrary(String libname) {
        loadLibrary0(Reflection.getCallerClass(), libname);
    }

    synchronized void loadLibrary0(Class fromClass, String libname) {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkLink(libname);
        }
        if (libname.indexOf((int)File.separatorChar) != -1) {
            throw new UnsatisfiedLinkError(
    "Directory separator should not appear in library name: " + libname);
        }
        ClassLoader.loadLibrary(fromClass, libname, false);
    }

    public InputStream getLocalizedInputStream(InputStream in) {
        return in;
    }
    
    public OutputStream getLocalizedOutputStream(OutputStream out) {
        return out;
    }*/
}