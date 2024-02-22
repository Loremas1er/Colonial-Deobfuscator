package ru.paimon.deobfuscator.colonialdeobfuscator.transformers;

import org.objectweb.asm.tree.ClassNode;
import ru.paimon.deobfuscator.colonialdeobfuscator.ClassModifier;
import ru.paimon.deobfuscator.colonialdeobfuscator.utils.ASMHelper;

import java.util.Arrays;

public class FlowTransformer extends ASMHelper implements ClassModifier {
    @Override
    public void modify(ClassNode classNode) {
        classNode.methods.stream()
                .filter(methodNode -> !methodNode.name.startsWith("<"))
                .forEach(methodNode -> {
                    Arrays.stream(methodNode.instructions.toArray())
                            .filter(insnNode -> insnNode.getOpcode() == DUP || insnNode.getOpcode() == POP || insnNode.getOpcode() == SWAP || insnNode.getOpcode() == FSUB || insnNode.getOpcode() == ISUB || insnNode.getOpcode() == DSUB || insnNode.getOpcode() == ATHROW)
                            .forEach(insnNode -> {
                                while (insnNode.getPrevious() != null
                                        && insnNode.getPrevious().getPrevious() != null
                                        && insnNode.getOpcode() == DUP
                                        && insnNode.getPrevious().getOpcode() == INVOKEVIRTUAL
                                        && insnNode.getPrevious().getPrevious().getOpcode() == LDC) {

                                    methodNode.instructions.remove(insnNode.getPrevious().getPrevious());
                                    methodNode.instructions.remove(insnNode.getPrevious());

                                    if (insnNode.getNext().getOpcode() == POP2) {

                                        methodNode.instructions.remove(insnNode.getNext());

                                    } else if (insnNode.getNext().getOpcode() == POP) {

                                        methodNode.instructions.remove(insnNode.getNext());
                                        methodNode.instructions.remove(insnNode.getNext());

                                    }
                                    methodNode.instructions.remove(insnNode);
                                }
                            });
                });
    }
}
