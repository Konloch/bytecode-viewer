package the.bytecode.club.bytecodeviewer.plugin.preinstalled;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.MethodNode;
import the.bytecode.club.bytecodeviewer.api.Plugin;
import the.bytecode.club.bytecodeviewer.api.PluginConsole;

public class StackFramesRemover extends Plugin
{
    @Override
    public void execute(List<ClassNode> classNodeList)
    {
        AtomicInteger counter = new AtomicInteger();
        PluginConsole frame = new PluginConsole("StackFrames Remover");
        for (ClassNode cn : classNodeList)
        {
            for (MethodNode mn : cn.methods)
            {
                for (AbstractInsnNode insn : mn.instructions.toArray())
                {
                    if (insn instanceof FrameNode)
                    {
                        mn.instructions.remove(insn);
                        counter.incrementAndGet();
                    }
                }
            }
        }

        frame.appendText(String.format("Removed %s stackframes.", counter));
        frame.setVisible(true);
    }
}
