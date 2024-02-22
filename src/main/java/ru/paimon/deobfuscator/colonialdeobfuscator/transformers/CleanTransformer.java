package ru.paimon.deobfuscator.colonialdeobfuscator.transformers;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import ru.paimon.deobfuscator.colonialdeobfuscator.ClassModifier;
import ru.paimon.deobfuscator.colonialdeobfuscator.utils.ASMHelper;

import java.util.Arrays;

public class CleanTransformer extends ASMHelper implements ClassModifier {
    @Override
    public void modify(ClassNode classNode) {
        classNode.methods.forEach(this::transformNormally);
    }
    private void transformNormally(MethodNode methodNode) {
        boolean modified;
        do {
            modified = false;
            for (AbstractInsnNode node : methodNode.instructions.toArray()) {
                switch (node.getOpcode()) {
                    case POP -> {
                        int type = check(node.getPrevious());
                        if (type == 1) {
                            methodNode.instructions.remove(node.getPrevious());
                            methodNode.instructions.remove(node);
                            modified = true;
                        }
                        break;
                    }
                    case POP2 -> {
                        int type = check(node.getPrevious());
                        if (type == 1 && check(node.getPrevious().getPrevious()) == 1) {
                            methodNode.instructions.remove(node.getPrevious().getPrevious());
                            methodNode.instructions.remove(node.getPrevious());
                            methodNode.instructions.remove(node);
                            modified = true;
                        } else if (type == 2) {
                            methodNode.instructions.remove(node.getPrevious());
                            methodNode.instructions.remove(node);
                            modified = true;
                        }
                        break;
                    }
                    case DUP -> {
                        int type = check(node.getPrevious());
                        if (type == 1) {
                            methodNode.instructions.insert(node.getPrevious(), node.getPrevious().clone(null));
                            methodNode.instructions.remove(node);
                            modified = true;
                        }
                        break;
                    }
                    case DUP_X1, DUP2_X1, DUP2_X2, DUP_X2 -> {
                        break;
                    }
                    case DUP2 -> {
                        int type = check(node.getPrevious());
                        if (type == 2) {
                            methodNode.instructions.insert(node.getPrevious(), node.getPrevious().clone(null));
                            methodNode.instructions.remove(node);
                            modified = true;
                        }
                        break;
                    }
                    case SWAP -> {
                        int firstType = check(node.getPrevious().getPrevious());
                        int secondType = check(node.getPrevious());

                        if (secondType == 1 && firstType == 1) {
                            AbstractInsnNode cloned = node.getPrevious().getPrevious();

                            methodNode.instructions.remove(node.getPrevious().getPrevious());
                            methodNode.instructions.set(node, cloned.clone(null));
                            modified = true;
                        }
                        break;
                    }
                }
            }
        } while (modified);
    }

    private int check(AbstractInsnNode node) {
        if (isLong(node) || isDouble(node)) {
            return 2;
        } else if (isInteger(node) || isFloat(node) || node instanceof LdcInsnNode) {
            return 1;
        }

        return 0;
    }
}
