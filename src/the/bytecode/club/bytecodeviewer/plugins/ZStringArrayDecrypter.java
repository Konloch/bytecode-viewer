package the.bytecode.club.bytecodeviewer.plugins;

import org.objectweb.asm.tree.ClassNode;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.api.Plugin;
import the.bytecode.club.bytecodeviewer.api.PluginConsole;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * Runs the classes then simply grabs the static String[] z
 * 
 * @author Konloch
 * @author Righteous
 * 
 */

public class ZStringArrayDecrypter extends Plugin {

    PluginConsole gui = new PluginConsole("ZStringArray Decrypter");
    StringBuilder out = new StringBuilder();

    @Override
    public void execute(ArrayList<ClassNode> classNodeList) {
		JOptionPane pane = new JOptionPane(
			"WARNING: This will load the classes into the JVM and execute the initialize function" + BytecodeViewer.nl+
			"for each class. IF THE FILE YOU'RE LOADING IS MALICIOUS, DO NOT CONTINUE."
		);
		Object[] options = new String[] { "Continue", "Cancel" };
		pane.setOptions(options);
		JDialog dialog = pane.createDialog(BytecodeViewer.viewer,
				"Bytecode Viewer - WARNING");
		dialog.setVisible(true);
		Object obj = pane.getValue();
		int result = -1;
		for (int k = 0; k < options.length; k++)
			if (options[k].equals(obj))
				result = k;
		
		if (result == 0) {
			boolean needsWarning = false;
            for (Class<?> debug : the.bytecode.club.bytecodeviewer.api.BytecodeViewer.loadClassesIntoClassLoader()) {
            	try {
	            	Field[] fields = debug.getDeclaredFields();
	                for ( Field field : fields ) {
	                    if ( field.getName().equals("z") ) {
	    	                out.append(debug.getName() + ":" + BytecodeViewer.nl);
	                        field.setAccessible(true);
	                        if(field.get(null) != null && field.get(null) instanceof String[] && Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
		                        String[] fieldVal = (String[]) field.get(null);
		                        for ( int i = 0; i < fieldVal.length; i++ ) {
		                            out.append("  z[" + i + "] = " + fieldVal[i] + BytecodeViewer.nl);
		                        }
	                        }
	                    }
	                }
            	} catch(NoClassDefFoundError | Exception e) {
            		System.err.println("Failed loading class " + debug.getName());
            		e.printStackTrace();
            		needsWarning = true;
            	}
            }
            
            if(needsWarning) {
            	BytecodeViewer.showMessage("Some classes failed to decrypt, if you'd like to decrypt all of them"+BytecodeViewer.nl+"makes sure you include ALL the libraries it requires.");
            }

	        gui.setText(out.toString());
	        gui.setVisible(true);
		}
    }

}
