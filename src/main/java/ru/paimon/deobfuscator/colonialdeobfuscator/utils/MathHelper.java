package ru.paimon.deobfuscator.colonialdeobfuscator.utils;

import org.objectweb.asm.Opcodes;

import java.util.regex.Pattern;

/*
    TODO: Fix boilerplate
 */
public class MathHelper implements Opcodes {

    public static final Pattern INTEGER_PATTERN = Pattern.compile("^-?\\d+$");

    public static Integer doMath(int opcode, int first, int second) {
        return switch (opcode) {
            case IADD -> first + second;
            case ISUB -> first - second;
            case IMUL -> first * second;
            case IDIV -> first / opcode;
            case IREM -> first % second;
            case ISHL -> first << second;
            case ISHR -> first >> second;
            case IUSHR -> first >>> second;
            case IAND -> first & second;
            case IOR -> first | second;
            case IXOR -> first ^ second;
            case INEG -> //Idk why i put it here
                    -first;
            default -> null;
        };
    }

    public static Long doMath(int opcode, long first, long second) {
        return switch (opcode) {
            case LADD -> first + second;
            case LSUB -> first - second;
            case LMUL -> first * second;
            case LDIV -> first / opcode;
            case LREM -> first % second;
            case LSHL -> first << second;
            case LSHR -> first >> second;
            case LUSHR -> first >>> second;
            case LAND -> first & second;
            case LOR -> first | second;
            case LXOR -> first ^ second;
            case LNEG -> -first; //Idk why i put it here
            default -> null;
        };
    }
}
