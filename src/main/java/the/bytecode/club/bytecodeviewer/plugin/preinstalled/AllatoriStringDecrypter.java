package the.bytecode.club.bytecodeviewer.plugin.preinstalled;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.objectweb.asm.tree.*;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Constants;
import the.bytecode.club.bytecodeviewer.api.ASMUtil;
import the.bytecode.club.bytecodeviewer.api.ExceptionUI;
import the.bytecode.club.bytecodeviewer.api.Plugin;
import the.bytecode.club.bytecodeviewer.api.PluginConsole;
import the.bytecode.club.bytecodeviewer.gui.components.MultipleChoiceDialogue;

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
	final PluginConsole frame = new PluginConsole("Allatori String Decrypter");
	final StringBuilder out = new StringBuilder();
	final String className;
	
	public AllatoriStringDecrypter(String className) {this.className = className;}
	
	@Override
	public void execute(ArrayList<ClassNode> classNodeList)
	{
		MultipleChoiceDialogue dialogue = new MultipleChoiceDialogue("Bytecode Viewer - WARNING",
				"WARNING: This will load the classes into the JVM and execute the allatori decrypter function"
						+ nl + "for each class. IF THE FILE YOU'RE LOADING IS MALICIOUS, DO NOT CONTINUE.",
				new String[]{"Continue", "Cancel"});
		
		if (dialogue.promptChoice() == 0)
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
				new ExceptionUI(e, "github.com/Szperak");
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
		{
			scanMethodNode(classNode, method);
		}
		
	}
	
	public int readUnsignedShort(byte[] b, final int index)
	{
		return ((b[index] & 0xFF) << 8) | (b[index + 1] & 0xFF);
	}
	
	private int getConstantPoolSize(String className)
	{
		byte[] fileContents = BytecodeViewer.getFileContents(className + ".class");
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
				LdcInsnNode ldci = (LdcInsnNode) i;
				if (ldci.cst instanceof String)
				{
					laststringldconstack = ldci;
				}
				continue;
			}
			else if (i instanceof MethodInsnNode)
			{
				MethodInsnNode methodi = (MethodInsnNode) i;
				
				
				if (laststringldconstack != null && methodi.getOpcode() == 0xb8)
				{ // Decryption is always a static call - 0xb8 - invokestatic
					String decrypterclassname = methodi.owner;
					String decrypterMethodName = methodi.name;
					
					if (decrypterclassname.contains("$"))
					{ // Decrypter is always a static method of other class's inner class
						byte[] decrypterFileContents = BytecodeViewer.getFileContents(decrypterclassname + ".class");
						
						// We have to create new node for editing
						// Also, one decrypter method could be used for multiple methods in code, what gives us only part of string decrypted
						ClassNode decrypterclassnode = ASMUtil.getClassNode(decrypterFileContents);
						
						if (decrypterclassnode != null)
						{
							MethodNode decryptermethodnode = ASMUtil.getMethodByName(decrypterclassnode, decrypterMethodName);
							
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
									System.out.println("Loading " + decrypterclassname);
									
									Class<?> decrypterClassList = the.bytecode.club.bytecodeviewer.api.BytecodeViewer
											.loadClassIntoClassLoader(decrypterclassnode);
									
									String decrypted = invokeDecrypter(decrypterClassList, decrypterMethodName, (String) laststringldconstack.cst);
									
									if (decrypted != null)
									{
										log("Succesfully invoked decrypter method: " + decrypted);
										laststringldconstack.cst = decrypted;
										iList.remove(methodi);
									}
								}
								catch (IndexOutOfBoundsException | ClassNotFoundException | IOException e)
								{
									e.printStackTrace();
									log("Could not load decrypter class: " + decrypterclassname);
								}
								
							}
							else
							{
								log("Could not find decrypter method (" + decrypterMethodName + ") of class " + decrypterclassname);
							}
						}
						else
						{
							log("Could not find decrypter ClassNode of class " + decrypterclassname);
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
		
		AbstractInsnNode insn = null, removeInsn = null;
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
		{
			return false;
		}
		
		while (insn != null)
		{
			if (insn instanceof MethodInsnNode)
			{
				MethodInsnNode methodi = ((MethodInsnNode) insn);
				
				if ("hashCode".equals(methodi.name)) // to this instruction
				{
					break;
				}
			}
			removeInsn = insn;
			insn = insn.getNext();
			iList.remove(removeInsn); // and remove it
		}
		if (insn == null) return false;
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
}
