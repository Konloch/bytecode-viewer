package the.bytecode.club.bytecodeviewer.decompilers.bytecode;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;

import org.apache.commons.lang3.StringEscapeUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.searching.commons.InstructionSearcher;

/**
 * A Bytecode decompiler
 * 
 * @author Konloch
 * @author WaterWolf
 *
 */

public class BytecodeDecompiler {
    
    public static String[] opcodeStrings;
    public static String[] typeStrings;
    
    static {
        opcodeStrings = new String[256];
        for (final Field f : Opcodes.class.getFields()) {
            try {
                final Object oo = f.get(null);
                if (oo instanceof Integer) {
                    final int oi = ((Integer)oo);
                    if (oi < 256 && oi >= 0) {
                        opcodeStrings[oi] = f.getName().toLowerCase();
                    }
                }
            } catch (final IllegalArgumentException e) {
                e.printStackTrace();
            } catch (final IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        typeStrings = new String[100];
        for (final Field f : AbstractInsnNode.class.getFields()) {
            if (!(f.getName().endsWith("_INSN"))) {
                continue;
            }
            try {
                final Object oo = f.get(null);
                if (oo instanceof Integer) {
                    final int oi = ((Integer)oo);
                    if (oi < 256 && oi >= 0) {
                        typeStrings[oi] = f.getName().toLowerCase();
                    }
                }
            } catch (final IllegalArgumentException e) {
                e.printStackTrace();
            } catch (final IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
	public String decompileClassNode(final ClassNode cn) {
        final StringBuilder classBuilder = new StringBuilder();
        final ClassStringBuilder cb = new ClassStringBuilder(classBuilder);
        
        final String cnm = cn.name;
        String package_ = null;
        String class_ = null;
        if (cnm.contains("/")) {
            package_ = cnm.substring(0, cnm.lastIndexOf("/"));
            class_ = cnm.substring(cnm.lastIndexOf("/")+1);
        }
        else {
            class_ = cnm;
        }
        
        if (package_ != null) {
            cb.appendnl("package " + package_ + ";", 2);
        }
        
        cb.append(Modifier.toString(cn.access) + " class " + class_ + " ");
        
        if (cn.superName != null) {
            cb.append("extends " + cn.superName + " ");
        }
        if (cn.interfaces.size() > 0) {
            cb.append("implements ");
			final Iterator<String> sit = cn.interfaces.iterator();
            while (sit.hasNext()) {
                final String s = sit.next();
                cb.append(s);
                if (sit.hasNext()) {
                    cb.append(", ");
                } else {
                    cb.append(" ");
                }
            }
        }
        
        cb.appendnl("{");
        cb.increase();
        cb.appendnl();
        
        final Iterator<FieldNode> fni = cn.fields.iterator();
        
        while (fni.hasNext()) {
            final FieldNode fn = fni.next();
            
            cb.appendnl(Modifier.toString(fn.access) + " " + Type.getType(fn.desc).getClassName() + " " + fn.name + ";");
            
        }
        
        cb.appendnl();
        
        final Iterator<MethodNode> mni = cn.methods.iterator();
        while (mni.hasNext()) {
            final MethodNode mn = mni.next();
            final String mnm = mn.name;
            if (!mnm.equals("<clinit>")) {
                cb.append(Modifier.toString(mn.access) + " ");
            }
            
            if (mnm.equals("<init>")) {
                cb.append(class_);
            }
            else if (mnm.equals("<clinit>")) {
                cb.append("static {");
                if (BytecodeViewer.viewer.debugHelpers.isSelected())
                	cb.appendnl(" // <clinit>");
                else
                	cb.appendnl();
            }
            else {
                cb.append(Type.getReturnType(mn.desc).getClassName() + " ");
                cb.append(mnm);
            }
            
            TypeAndName[] args = new TypeAndName[0];
            
            if (!mnm.equals("<clinit>")) {
                cb.append("(");
                
                // TODO desc
                final Type[] argTypes = Type.getArgumentTypes(mn.desc);
                args = new TypeAndName[argTypes.length];
                
                for (int i = 0;i < argTypes.length; i++) {
                    final Type type = argTypes[i];
                    
                    final TypeAndName tan = new TypeAndName();
                    final String argName = "arg" + i;
                    
                    tan.name = argName;
                    tan.type = type;
                    
                    args[i] = tan;
                    
                    cb.append(type.getClassName() + " " + argName + (i < argTypes.length-1 ? ", " : ""));
                }
                
                cb.appendnl(") {");
            }
            
            cb.increase();
            
            try {
				decompileMethod(cb, args, mn, cn);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
            
            cb.decrease();
            cb.appendnl("}");
            cb.appendnl();
        }
        
        cb.decrease();
        cb.appendnl("}");
        
        
        return classBuilder.toString();
    }
    
    public void decompileMethod(final ClassStringBuilder builder, final TypeAndName[] args, final MethodNode mn, final ClassNode parent) throws UnsupportedEncodingException {
        final InstructionSearcher is = new InstructionSearcher(mn);
        //AbstractInsnNode next = is.getCurrent();
        
        for(Object e : mn.tryCatchBlocks.toArray()) {
        	TryCatchBlockNode t = (TryCatchBlockNode)e;
        	String type = t.type;
        	LabelNode start = t.start;
        	LabelNode end = t.end;
        	LabelNode handler = t.handler;
        	builder.appendnl("trycatch block L" + start.hashCode() + " to L" + end.hashCode() + " handled by L" + handler.hashCode() + " exception type: " + type);
        }
        
        int index = 0;
        for(AbstractInsnNode next : mn.instructions.toArray()) {
            
            if (next.getOpcode() == -1) {
            	
            	if(next instanceof LabelNode) {
            		LabelNode l = (LabelNode)next;
            		builder.appendnl(index++ + ". L" +l.hashCode());
            	} else {
            		builder.appendnl(index++ + ". nop //actually an unimplement opcode, please contact Konloch"); //lets just set it as nop for now.
            	}
            	//next = is.getNext();
                continue;
            }
            
            builder.append(index++ + ". " + opcodeStrings[next.getOpcode()] + " ");
            
            if (next instanceof FieldInsnNode) {
                final FieldInsnNode fin = (FieldInsnNode) next;
                builder.append(fin.owner + " " + fin.name + " " + fin.desc);
            }
            else if (next instanceof MethodInsnNode) {
                final MethodInsnNode min = (MethodInsnNode) next;
                builder.append(min.owner + " " + min.name + " " + min.desc);
            }
            else if (next instanceof VarInsnNode) {
                final VarInsnNode vin = (VarInsnNode) next;
                builder.append(vin.var);
                if (BytecodeViewer.viewer.debugHelpers.isSelected()) {
                    if (vin.var == 0 && !Modifier.isStatic(mn.access)) {
                        builder.append(" // reference to self");
                    }
                    else {
                        final int refIndex = vin.var - (Modifier.isStatic(mn.access) ? 0 : 1);
                        if (refIndex >= 0 && refIndex < args.length-1) {
                            builder.append(" // reference to " + args[refIndex].name);
                        }
                    }
                }
            }
            else if (next instanceof IntInsnNode) {
                final IntInsnNode iin = (IntInsnNode) next;
                builder.append(iin.operand);
            }
            else if (next instanceof JumpInsnNode) {
                final JumpInsnNode jin = (JumpInsnNode) next;
                builder.append(is.computePosition(jin.label));
                switch (next.getOpcode()) {
                case Opcodes.IF_ICMPLT:
                    builder.append(" // if val1 less than val2 jump");
                    break;
                }
            }
            else if (next instanceof LdcInsnNode) {
                final LdcInsnNode lin = (LdcInsnNode) next;
                if(lin.cst instanceof String) {
                	String s = ((String)lin.cst).replaceAll("\\n", "\\\\n").replaceAll("\\r", "\\\\r").replaceAll("\\\"", "\\\\\"");
                	if(BytecodeViewer.viewer.chckbxmntmNewCheckItem.isSelected())
                        builder.append("\"" + StringEscapeUtils.escapeJava(s) + "\"");
                	else
                		builder.append("\"" + s + "\"");
                } else {
                	String s = lin.cst.toString().replaceAll("\\n", "\\\\n").replaceAll("\\r", "\\\\r").replaceAll("\\\"", "\\\\\"");
                	if(BytecodeViewer.viewer.chckbxmntmNewCheckItem.isSelected())
                		builder.append("\"" + StringEscapeUtils.escapeJava(s) + "\"");
                	else
                		builder.append("\"" + s + "\"");
                }
            }
            else if (next instanceof IincInsnNode) {
                final IincInsnNode iin = (IincInsnNode) next;
                builder.append("var " + iin.var + " by " + iin.incr);
            }
            else if (next instanceof TypeInsnNode) {
                final TypeInsnNode tin = (TypeInsnNode) next;
                builder.append(tin.desc);
            }
            else {
                /*
                switch (next.getOpcode()) {
                case Opcodes.IF_ICMPLT:
                    buffer.append(" // ");
                    break;
                }
                */
            }
            
            if (BytecodeViewer.viewer.debugInstructions.isSelected()) {
                builder.append(" // " + typeStrings[next.getType()] + " ");
            }
            
            if (BytecodeViewer.viewer.debugHelpers.isSelected() &&
            	next instanceof JumpInsnNode)
            {
            	final JumpInsnNode jin = (JumpInsnNode) next;
            	builder.append(" // line " + is.computePosition(jin.label) + " is " + printInstruction(is.computePosition(jin.label), mn, is).trim());
            }
            
            builder.appendnl();
        }
    }

    public static String printInstruction(int line, MethodNode mn, InstructionSearcher is) {
    	for(int i = 0; i < mn.instructions.size(); i++) {
    		AbstractInsnNode next = mn.instructions.get(i);
    		if(line == i)
    			if(next.getOpcode() != -1) {
    				return beatifyAbstractInsnNode(next, is);
    			}
    	}
        return "Unable to find, please contact konloch.";
    }
    
    public static String beatifyAbstractInsnNode(AbstractInsnNode next, InstructionSearcher is) {
		String insn = "";
		
		if(next.getOpcode() != -1)
			insn =opcodeStrings[next.getOpcode()] + " ";
		else if(next instanceof LabelNode) {
            	LabelNode l = (LabelNode)next;
            	insn = "L" +l.hashCode();
		}
		
        if (next instanceof FieldInsnNode) {
            final FieldInsnNode fin = (FieldInsnNode) next;
            insn += fin.owner + " " + fin.name + " " + fin.desc;
        }
        else if (next instanceof MethodInsnNode) {
            final MethodInsnNode min = (MethodInsnNode) next;
            insn += min.owner + " " + min.name + " " + min.desc;
        }
        else if (next instanceof VarInsnNode) {
            final VarInsnNode vin = (VarInsnNode) next;
            insn += vin.var;
        }
        else if (next instanceof IntInsnNode) {
            final IntInsnNode iin = (IntInsnNode) next;
            insn += iin.operand;
        }
        else if (next instanceof JumpInsnNode) {
            final JumpInsnNode jin = (JumpInsnNode) next;
            insn += is.computePosition(jin.label);
        }
        else if (next instanceof LdcInsnNode) {
            final LdcInsnNode lin = (LdcInsnNode) next;
            if(lin.cst instanceof String)
                insn += "\"" + ((String) lin.cst).replaceAll("\\n", "\\\\n").replaceAll("\\r", "\\\\r") + "\"";
            else
                insn += "\"" + lin.cst + "\"";
        }
        else if (next instanceof IincInsnNode) {
            final IincInsnNode iin = (IincInsnNode) next;
            insn += "var " + iin.var + " by " + iin.incr;
        }
        else if (next instanceof TypeInsnNode) {
            final TypeInsnNode tin = (TypeInsnNode) next;
            insn += tin.desc;
        }
        else {
        	
        }
		
		return insn;
    }

}
