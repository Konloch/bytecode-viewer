package the.bytecode.club.bytecodeviewer.api;

import java.io.File;
import java.util.ArrayList;

import org.objectweb.asm.tree.ClassNode;

import the.bytecode.club.bytecodeviewer.plugins.EZInjection;

/**
 * The official API for BCV, this was mainly designed for plugin authors and
 * people utilizing EZ-Injection.
 * 
 * @author Konloch
 * 
 */

public class BytecodeViewer {

	/**
	 * This is used to define a global loader.
	 */
	private static ClassNodeLoader loader = new ClassNodeLoader();

	/**
	 * Grab the loader instance
	 * 
	 * @return
	 */
	public static ClassNodeLoader getClassNodeLoader() {
		return loader;
	}

	/**
	 * Creates a new instance of the ClassNode loader.
	 */
	public static void createNewClassNodeLoaderInstance() {
		loader.clear();
		loader = new ClassNodeLoader();
	}

	/**
	 * Used to start a plugin from file.
	 * 
	 * @param plugin
	 *            the file of the plugin
	 */
	public static void startPlugin(File plugin) {
		the.bytecode.club.bytecodeviewer.BytecodeViewer.startPlugin(plugin);
	}

	/**
	 * Used to load classes/jars into BCV.
	 * 
	 * @param files
	 *				an array of the files you want loaded.
	 * @param recentFiles
	 * 				if it should save to the recent files menu.
	 */
	public static void openFiles(File[] files, boolean recentFiles) {
		the.bytecode.club.bytecodeviewer.BytecodeViewer.openFiles(files, recentFiles);
	}

	/**
	 * Returns the currently opened class node, if nothing is opened it'll return null.
	 * @return The opened class node or a null if nothing is opened
	 */
	public static ClassNode getCurrentlyOpenedClassNode() {
		return the.bytecode.club.bytecodeviewer.BytecodeViewer.getCurrentlyOpenedClassNode();
	}
	
	/**
	 * Used to load a ClassNode.
	 * 
	 * @param name
	 *            the full name of the ClassNode
	 * @return the ClassNode
	 */
	public static ClassNode getClassNode(String name) {
		return the.bytecode.club.bytecodeviewer.BytecodeViewer
				.getClassNode(name);
	}

	/**
	 * Used to grab the loaded ClassNodes.
	 * 
	 * @return the loaded classes
	 */
	public static ArrayList<ClassNode> getLoadedClasses() {
		return the.bytecode.club.bytecodeviewer.BytecodeViewer
				.getLoadedClasses();
	}

	/**
	 * Used to insert a Bytecode Hook using EZ-Injection.
	 * 
	 * @param hook
	 */
	public static void insertHook(BytecodeHook hook) {
		EZInjection.hookArray.add(hook);
	}

	/**
	 * This will ask the user if they really want to reset the workspace, then
	 * it'll reset the work space.
	 * 
	 * @param ask
	 * 			if it should ask the user about resetting the workspace
	 */
	public static void resetWorkSpace(boolean ask) {
		the.bytecode.club.bytecodeviewer.BytecodeViewer.resetWorkSpace(ask);
	}

	/**
	 * If true, it will display the busy icon, if false it will remove it if
	 * it's displayed.
	 * 
	 * @param busy
	 *            if it should display the busy icon or not
	 */
	public static void setBusy(boolean busy) {
		the.bytecode.club.bytecodeviewer.BytecodeViewer.viewer.setIcon(busy);
	}

	/**
	 * Sends a small window popup with the defined message.
	 * 
	 * @param message
	 *            the message you want to display
	 */
	public static void showMessage(String message) {
		the.bytecode.club.bytecodeviewer.BytecodeViewer.showMessage(message);
	}
}
