package the.bytecode.club.bytecodeviewer.api;

/**
 * Whenever a function is executed, this class will be executed with the function
 * callHook(String);
 * 
 * @author Konloch
 *
 */

public abstract class BytecodeHook {
	
	/**
	 * Called whenever a function is called (And EZ-Injection has been injected).
	 * 
	 * @param information the full name of the class, method and method description.
	 */
	public abstract void callHook(String information);

}
