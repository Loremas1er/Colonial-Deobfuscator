package ru.paimon.deobfuscator.colonialdeobfuscator;

import org.objectweb.asm.tree.ClassNode;

import java.util.Random;

public interface ClassModifier {
    Random r = new Random();

    void modify(ClassNode classNode);
}
