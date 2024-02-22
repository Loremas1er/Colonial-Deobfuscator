package ru.paimon.deobfuscator.colonialdeobfuscator.transformers;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import ru.paimon.deobfuscator.colonialdeobfuscator.ClassModifier;
import ru.paimon.deobfuscator.colonialdeobfuscator.utils.ASMHelper;

import java.util.Arrays;

public class BooleanTransformer extends ASMHelper implements ClassModifier {
    @Override
    public void modify(ClassNode classNode) {
        classNode.methods.removeIf(methodNode -> methodNode.name.startsWith("ColonialObfuscator_"));
        classNode.methods.forEach(methodNode ->
                Arrays.stream(methodNode.instructions.toArray())
                        .forEach(insnNode -> {
                            if (isMethodStartWith(insnNode, classNode.name, "ColonialObfuscator_")
                                    && insnNode.getOpcode() == INVOKESTATIC && ((MethodInsnNode) insnNode).desc.equals("(I)I")) {
                                methodNode.instructions.remove(insnNode);
                            }
                        }));
    }
}
