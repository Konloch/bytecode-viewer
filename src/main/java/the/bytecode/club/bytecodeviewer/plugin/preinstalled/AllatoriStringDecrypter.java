package the.bytecode.club.bytecodeviewer.plugin.preinstalled;

import java.awt.Dimension;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Constants;
import the.bytecode.club.bytecodeviewer.api.ASMUtil;
import the.bytecode.club.bytecodeviewer.api.BCV;
import the.bytecode.club.bytecodeviewer.api.Plugin;
import the.bytecode.club.bytecodeviewer.api.PluginConsole;
import the.bytecode.club.bytecodeviewer.gui.components.MultipleChoiceDialog;
import the.bytecode.club.bytecodeviewer.plugin.PluginManager;
import the.bytecode.club.bytecodeviewer.resources.IconResources;

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
 * An Allatori String Decrypter, targets an unknown (old) version.
 *
 * @author Konloch
 * @author Szperak
 * @since 08/15/2015
 */

public class AllatoriStringDecrypter extends Plugin
{
	final StringBuilder out = new StringBuilder();
	final String className;
	
	public AllatoriStringDecrypter(String className) {this.className = className;}
	
	@Override
	public void execute(List<ClassNode> classNodeList)
	{
		PluginConsole frame = new PluginConsole("Allatori String Decrypter");
		
		MultipleChoiceDialog dialog = new MultipleChoiceDialog("Bytecode Viewer - WARNING",
				"WARNING: This will load the classes into the JVM and execute the allatori decrypter function"
						+ nl + "for each class. IF THE FILE YOU'RE LOADING IS MALICIOUS, DO NOT CONTINUE.",
				new String[]{"Continue", "Cancel"});
		
		if (dialog.promptChoice() == 0)
		{
			try
			{
				if (!className.equals("*"))
				{
					for (ClassNode classNode : classNodeList)
					{
						if (classNode.name.equals(className))
							scanClassNode(classNode);
					}
				}
				else
				{
					for (ClassNode classNode : classNodeList)
					{
						scanClassNode(classNode);
					}
				}
			}
			catch (Exception e)
			{
				BytecodeViewer.handleException(e, "github.com/Szperak");
			}
			finally
			{
				frame.appendText(out.toString());
				frame.setVisible(true);
			}
		}
	}
	
	private void log(String msg)
	{
		out.append(msg);
		out.append(Constants.nl);
	}
	
	public void scanClassNode(ClassNode classNode) throws Exception
	{
		for (MethodNode method : classNode.methods)
			scanMethodNode(classNode, method);
	}
	
	public int readUnsignedShort(byte[] b, final int index)
	{
		return ((b[index] & 0xFF) << 8) | (b[index + 1] & 0xFF);
	}
	
	private int getConstantPoolSize(String className)
	{
		byte[] fileContents = activeContainer.getFileContents(className + ".class");
		return readUnsignedShort(fileContents, 8);
	}
	
	public void scanMethodNode(ClassNode classNode, MethodNode methodNode) throws Exception
	{
		InsnList iList = methodNode.instructions;
		
		log("Scanning method " + methodNode.name + " of " + classNode.name);
		
		LdcInsnNode laststringldconstack = null;
		for (AbstractInsnNode i : iList.toArray())
		{
			if (i instanceof LdcInsnNode)
			{
				LdcInsnNode ldcI = (LdcInsnNode) i;
				if (ldcI.cst instanceof String)
					laststringldconstack = ldcI;
				continue;
			}
			else if (i instanceof MethodInsnNode)
			{
				MethodInsnNode methodI = (MethodInsnNode) i;
				
				// Decryption is always a static call - 0xb8 - invokestatic
				if (laststringldconstack != null && methodI.getOpcode() == 0xb8)
				{
					String decrypterClassName = methodI.owner;
					String decrypterMethodName = methodI.name;
					
					// Decrypter is always a static method of other class's inner class
					if (decrypterClassName.contains("$"))
					{
						byte[] decrypterFileContents = activeContainer.getFileContents(decrypterClassName + ".class");
						
						// We have to create new node for editing
						// Also, one decrypter method could be used for multiple methods in code, what gives us only part of string decrypted
						ClassNode decrypterClassNode = ASMUtil.bytesToNode(decrypterFileContents);
						MethodNode decryptermethodnode = ASMUtil.getMethodByName(decrypterClassNode, decrypterMethodName);
						
						if (decryptermethodnode != null)
						{
							String keyString = (getConstantPoolSize(classNode.name) +
									classNode.name +
									methodNode.name +
									getConstantPoolSize(classNode.name)
							);
							
							int newHashCode = keyString.hashCode();
							
							scanDecrypter(decryptermethodnode, newHashCode);
							
							try
							{
								System.out.println("Loading " + decrypterClassName);
								
								Class<?> decrypterClassList = BCV
										.loadClassIntoClassLoader(decrypterClassNode);
								
								String decrypted = invokeDecrypter(decrypterClassList, decrypterMethodName, (String) laststringldconstack.cst);
								
								if (decrypted != null)
								{
									log("Succesfully invoked decrypter method: " + decrypted);
									laststringldconstack.cst = decrypted;
									iList.remove(methodI);
								}
							}
							catch (IndexOutOfBoundsException | ClassNotFoundException | IOException e)
							{
								e.printStackTrace();
								log("Could not load decrypter class: " + decrypterClassName);
							}
							
						}
						else
						{
							log("Could not find decrypter method (" + decrypterMethodName + ") of class " + decrypterClassName);
						}
					}
				}
				
			}
			else if (i instanceof InvokeDynamicInsnNode)
			{
				InvokeDynamicInsnNode methodi = (InvokeDynamicInsnNode) i;
				if (methodi.getOpcode() == 0xba)
				{
					// TODO: Safe-reflection deobfuscator here
					// Allatori replaces invokeinterface and invokestatic with invokedynamic
					
					//log(methodi.bsm.getOwner()+" dot "+methodi.bsm.getName());
					//iList.set(methodi, new MethodInsnNode(0xb8, methodi.bsm.getOwner(), methodi.bsm.getName(), methodi.bsm.getDesc(), false));
					
				}
			}
			
			laststringldconstack = null;
		}
	}
	
	private boolean scanDecrypter(MethodNode decryptermethodnode, int newHashCode)
	{
		InsnList iList = decryptermethodnode.instructions;
		
		AbstractInsnNode insn = null, removeInsn;
		for (AbstractInsnNode i : iList.toArray())
		{
			if (i instanceof MethodInsnNode)
			{
				MethodInsnNode methodi = ((MethodInsnNode) i);
				if ("currentThread".equals(methodi.name)) // find code form this instruction
				{
					insn = i;
					break;
				}
				
			}
			
		}
		
		if (insn == null)
			return false;
		
		while (insn != null)
		{
			if (insn instanceof MethodInsnNode)
			{
				MethodInsnNode methodi = ((MethodInsnNode) insn);
				
				if ("hashCode".equals(methodi.name)) // to this instruction
					break;
			}
			removeInsn = insn;
			insn = insn.getNext();
			iList.remove(removeInsn); // and remove it
		}
		
		if (insn == null)
			return false;
		
		iList.set(insn, new LdcInsnNode(newHashCode)); // then replace it with pre-computed key LDC
		return true;
	}
	
	private String invokeDecrypter(Class<?> decrypterclass, String name, String arg) throws Exception
	{
		try
		{
			Method decrypterMethod = decrypterclass.getDeclaredMethod(name, String.class);
			decrypterMethod.setAccessible(true);
			return (String) decrypterMethod.invoke(null, arg);
		}
		catch (Exception e)
		{
			log("Could not invoke decrypter method: " + name + " of class " + decrypterclass.getName());
			throw e;
		}
	}
	
	public static class AllatoriStringDecrypterOptions extends Plugin
	{
		@Override
		public void execute(List<ClassNode> classNodeList)
		{
			new AllatoriStringDecrypterOptionsFrame().setVisible(true);
		}
	}
	
	public static class AllatoriStringDecrypterOptionsFrame extends JFrame
	{
		private final JTextField textField;
		
		public AllatoriStringDecrypterOptionsFrame()
		{
			this.setIconImages(IconResources.iconList);
			setSize(new Dimension(250, 120));
			setResizable(false);
			setTitle("Allatori String Decrypter");
			getContentPane().setLayout(null);
			
			JButton btnNewButton = new JButton("Decrypt");
			btnNewButton.setBounds(6, 56, 232, 23);
			getContentPane().add(btnNewButton);
			
			JLabel lblNewLabel = new JLabel("Class:");
			lblNewLabel.setBounds(6, 20, 67, 14);
			getContentPane().add(lblNewLabel);
			
			textField = new JTextField();
			textField.setToolTipText("* will search all classes");
			textField.setText("*");
			textField.setBounds(80, 17, 158, 20);
			getContentPane().add(textField);
			textField.setColumns(10);
			
			btnNewButton.addActionListener(arg0 ->
			{
				PluginManager.runPlugin(new the.bytecode.club.bytecodeviewer.plugin.preinstalled.AllatoriStringDecrypter(textField.getText()));
				dispose();
			});
			
			this.setLocationRelativeTo(null);
		}
		
		private static final long serialVersionUID = -2662514582647810868L;
	}
}
