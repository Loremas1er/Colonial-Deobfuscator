package ru.paimon.deobfuscator.colonialdeobfuscator.transformers;

import org.objectweb.asm.tree.ClassNode;
import ru.paimon.deobfuscator.colonialdeobfuscator.ClassModifier;
import ru.paimon.deobfuscator.colonialdeobfuscator.utils.ASMHelper;

public class AccessTransformer extends ASMHelper implements ClassModifier {
    @Override
    public void modify(ClassNode classNode) {
        if (isAccess(classNode.access, ACC_SYNTHETIC)) {
            classNode.access &= ~ACC_SYNTHETIC;
        }

        classNode.fields.stream()
                .filter(node -> isAccess(node.access, ACC_SYNTHETIC))
                .forEach(node -> node.access &= ~ACC_SYNTHETIC);

        classNode.methods.forEach(methodNode -> {
            if (isAccess(methodNode.access, ACC_SYNTHETIC)) {
                methodNode.access &= ~ACC_SYNTHETIC;
            }

            if (isAccess(methodNode.access, ACC_BRIDGE)) {
                methodNode.access &= ~ACC_BRIDGE;
            }
        });
    }
}
