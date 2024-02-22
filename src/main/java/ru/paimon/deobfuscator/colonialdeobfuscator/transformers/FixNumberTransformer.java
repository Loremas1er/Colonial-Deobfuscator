package ru.paimon.deobfuscator.colonialdeobfuscator.transformers;

import org.objectweb.asm.tree.*;
import ru.paimon.deobfuscator.colonialdeobfuscator.ClassModifier;
import ru.paimon.deobfuscator.colonialdeobfuscator.utils.ASMHelper;

import java.util.Arrays;
import java.util.HashMap;

public class FixNumberTransformer extends ASMHelper implements ClassModifier {
    @Override
    public void modify(ClassNode classNode) {
        HashMap<Integer,Integer> vars = new HashMap<>();
        classNode.methods.forEach(methodNode ->
                Arrays.stream(methodNode.instructions.toArray())
                        .forEach(insnNode -> {
                            if (isInteger(insnNode) && insnNode.getNext().getOpcode() == IRETURN) {
                                if (getInteger(insnNode) % 2 == -1 || getInteger(insnNode) % 2 == 0) {
                                    methodNode.instructions.set(insnNode, new InsnNode(ICONST_1));
                                } else if (getInteger(insnNode) % 2 == 1) {
                                    methodNode.instructions.set(insnNode, new InsnNode(ICONST_0));
                                }
                            } else if (isInteger(insnNode) && insnNode.getNext().getOpcode() == INVOKEVIRTUAL) {
                                if (((MethodInsnNode) insnNode.getNext()).desc.contains("Z")) {
                                    if (getInteger(insnNode) % 2 == -1 || getInteger(insnNode) % 2 == 0) {
                                        methodNode.instructions.set(insnNode, new InsnNode(ICONST_1));
                                    } else if (getInteger(insnNode) % 2 == 1) {
                                        methodNode.instructions.set(insnNode, new InsnNode(ICONST_0));
                                    }
                                }
                            }/* else if (insnNode.getPrevious() != null && insnNode.getPrevious().getOpcode() == ALOAD && isInteger(insnNode) && insnNode.getNext().getOpcode() == AALOAD) {
                                if (vars.get(((VarInsnNode) insnNode.getPrevious()).var) != null) {
                                    int finalDec = getInteger(insnNode) ^ vars.get(((VarInsnNode) insnNode.getPrevious()).var);
                                    int opcode = 9999;
                                    switch (finalDec) {
                                        case 1:
                                            opcode = ICONST_0;
                                        case 2:
                                            opcode = ICONST_1;
                                        case 3:
                                            opcode = ICONST_2;
                                        case 4:
                                            opcode = ICONST_3;
                                        case 5:
                                            opcode = ICONST_4;
                                        case 6:
                                            opcode = ICONST_5;
                                    }
                                    if (opcode != 9999) {
                                        methodNode.instructions.set(insnNode, new InsnNode(opcode));
                                    } else {
                                        methodNode.instructions.set(insnNode, new IntInsnNode(BIPUSH, finalDec));
                                    }
                                } else {
                                    vars.put(((VarInsnNode) insnNode.getPrevious()).var, getInteger(insnNode));
                                    methodNode.instructions.set(insnNode, new InsnNode(ICONST_0));
                                }
                            }*/
                        }));
    }
}
