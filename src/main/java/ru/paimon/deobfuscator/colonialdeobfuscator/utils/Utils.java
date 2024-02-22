package ru.paimon.deobfuscator.colonialdeobfuscator.utils;

import org.apache.commons.io.IOUtils;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;
import sun.misc.Unsafe;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Utils {
    private static final Printer printer = new Textifier();
    private static final TraceMethodVisitor methodPrinter;
    private static Unsafe unsafe;

    public static boolean isInstruction(AbstractInsnNode node) {
        return !(node instanceof LineNumberNode) && !(node instanceof FrameNode) && !(node instanceof LabelNode);
    }

    public static boolean notAbstractOrNative(MethodNode methodNode) {
        return !Modifier.isNative(methodNode.access) && !Modifier.isAbstract(methodNode.access);
    }

    public static AbstractInsnNode getNextFollowGoto(AbstractInsnNode node) {
        Object next;
        for(next = node.getNext(); next instanceof LabelNode || next instanceof LineNumberNode || next instanceof FrameNode; next = ((AbstractInsnNode)next).getNext()) {
        }

        if (((AbstractInsnNode)next).getOpcode() == 167) {
            JumpInsnNode cast = (JumpInsnNode)next;

            for(next = cast.label; !isInstruction((AbstractInsnNode)next); next = ((AbstractInsnNode)next).getNext()) {
            }
        }

        return (AbstractInsnNode)next;
    }

    public static AbstractInsnNode getNext(AbstractInsnNode node, int amount) {
        for(int i = 0; i < amount; ++i) {
            node = getNext(node);
        }

        return node;
    }

    public static AbstractInsnNode getNext(AbstractInsnNode node) {
        AbstractInsnNode next;
        for(next = node.getNext(); !isInstruction(next); next = next.getNext()) {
        }

        return next;
    }

    public static AbstractInsnNode getPrevious(AbstractInsnNode node, int amount) {
        for(int i = 0; i < amount; ++i) {
            node = getPrevious(node);
        }

        return node;
    }

    public static AbstractInsnNode getPrevious(AbstractInsnNode node) {
        AbstractInsnNode prev;
        for(prev = node.getPrevious(); !isInstruction(prev); prev = prev.getPrevious()) {
        }

        return prev;
    }

    public static int iconstToInt(int opcode) {
        int operand = Integer.MIN_VALUE;
        switch(opcode) {
            case 2:
                operand = -1;
                break;
            case 3:
                operand = 0;
                break;
            case 4:
                operand = 1;
                break;
            case 5:
                operand = 2;
                break;
            case 6:
                operand = 3;
                break;
            case 7:
                operand = 4;
                break;
            case 8:
                operand = 5;
        }

        return operand;
    }

    public static MethodNode getMethodNode(ClassNode start, String methodName, String methodDesc, Map<String, ClassNode> dictionary) {
        MethodNode targetMethod = null;
        LinkedList<ClassNode> haystack = new LinkedList();
        haystack.add(start);

        while(targetMethod == null && !haystack.isEmpty()) {
            ClassNode needle = (ClassNode)haystack.poll();
            targetMethod = (MethodNode)needle.methods.stream().filter((imn) -> {
                return imn.name.equals(methodName) && imn.desc.equals(methodDesc);
            }).findFirst().orElse((MethodNode) null);
            if (targetMethod == null && !needle.name.equals("java/lang/Object")) {
                Iterator var7 = needle.interfaces.iterator();

                while(var7.hasNext()) {
                    String intf = (String)var7.next();
                    ClassNode intfNode = (ClassNode)dictionary.get(intf);
                    if (intfNode == null) {
                        throw new IllegalArgumentException("Class not found: " + intf);
                    }

                    haystack.add(intfNode);
                }

                String superName = needle.superName;
                needle = (ClassNode)dictionary.get(needle.superName);
                if (needle == null) {
                    throw new IllegalArgumentException("Class not found: " + superName);
                }

                haystack.add(needle);
            }
        }

        return targetMethod;
    }

    public static long copy(InputStream from, OutputStream to) throws IOException {
        byte[] buf = new byte[4096];
        long total = 0L;

        while(true) {
            int r = from.read(buf);
            if (r == -1) {
                return total;
            }

            to.write(buf, 0, r);
            total += (long)r;
        }
    }

    public static String descFromTypes(Type[] types) {
        StringBuilder descBuilder = new StringBuilder("(");
        Type[] var2 = types;
        int var3 = types.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Type type = var2[var4];
            descBuilder.append(type.getDescriptor());
        }

        descBuilder.append(")");
        return descBuilder.toString();
    }

    public static void sneakyThrow(Throwable t) {
        sneakyThrow0(t);
    }

    private static <T extends Throwable> void sneakyThrow0(Throwable t) throws T {
        try {
            throw t;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static String prettyprint(AbstractInsnNode insnNode) {
        if (insnNode == null) {
            return "null";
        } else {
            insnNode.accept(methodPrinter);
            StringWriter sw = new StringWriter();
            printer.print(new PrintWriter(sw));
            printer.getText().clear();
            return sw.toString().trim();
        }
    }

    public static boolean isTerminating(AbstractInsnNode next) {
        switch(next.getOpcode()) {
            case 167:
            case 170:
            case 171:
            case 172:
            case 173:
            case 174:
            case 175:
            case 176:
            case 177:
            case 191:
                return true;
            case 168:
            case 169:
            case 178:
            case 179:
            case 180:
            case 181:
            case 182:
            case 183:
            case 184:
            case 185:
            case 186:
            case 187:
            case 188:
            case 189:
            case 190:
            default:
                return false;
        }
    }

    public static boolean willPushToStack(int opcode) {
        switch(opcode) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 11:
            case 12:
            case 13:
            case 16:
            case 17:
            case 18:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 178:
                return true;
            default:
                return false;
        }
    }

    public static Unsafe getUnsafe() {
        try {
            initializeUnsafe();
        } catch (Exception var1) {
            var1.printStackTrace();
        }

        return unsafe;
    }

    public static <T> Object allocateInstance(Class<T> t) {
        try {
            return getUnsafe().allocateInstance(t);
        } catch (InstantiationException var2) {
            var2.printStackTrace();
            return null;
        }
    }

    private static void initializeUnsafe() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (unsafe == null) {
            Constructor<Unsafe> ctor = Unsafe.class.getDeclaredConstructor();
            ctor.setAccessible(true);
            unsafe = (Unsafe)ctor.newInstance();
        }

    }

    public static boolean isNumberV2(int type,char var4) {
        byte var2 = -1;
        while(type > 0) {
            //
        }

        switch(var2) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                return true;
            default:
                return false;
        }
    }

    public static boolean isNumber(String type) {
        byte var2 = -1;
        switch(type.hashCode()) {
            case 66:
                if (type.equals("B")) {
                    var2 = 2;
                }
            case 67:
            case 69:
            case 71:
            case 72:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
            case 81:
            case 82:
            default:
                break;
            case 68:
                if (type.equals("D")) {
                    var2 = 4;
                }
                break;
            case 70:
                if (type.equals("F")) {
                    var2 = 5;
                }
                break;
            case 73:
                if (type.equals("I")) {
                    var2 = 0;
                }
                break;
            case 74:
                if (type.equals("J")) {
                    var2 = 3;
                }
                break;
            case 83:
                if (type.equals("S")) {
                    var2 = 1;
                }
        }

        switch(var2) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                return true;
            default:
                return false;
        }
    }

    public static boolean canReturnDigit(String type) {
        byte var2 = -1;
        switch(type.hashCode()) {
            case 66:
                if (type.equals("B")) {
                    var2 = 2;
                }
                break;
            case 67:
                if (type.equals("C")) {
                    var2 = 5;
                }
                break;
            case 73:
                if (type.equals("I")) {
                    var2 = 0;
                }
                break;
            case 74:
                if (type.equals("J")) {
                    var2 = 3;
                }
                break;
            case 83:
                if (type.equals("S")) {
                    var2 = 1;
                }
                break;
            case 90:
                if (type.equals("Z")) {
                    var2 = 4;
                }
        }

        switch(var2) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                return true;
            default:
                return false;
        }
    }

    public static boolean isFloat(String type) {
        byte var2 = -1;
        switch(type.hashCode()) {
            case 68:
                if (type.equals("D")) {
                    var2 = 1;
                }
                break;
            case 70:
                if (type.equals("F")) {
                    var2 = 0;
                }
        }

        switch(var2) {
            case 0:
            case 1:
                return true;
            default:
                return false;
        }
    }

    public static InsnList copyInsnList(InsnList original) {
        InsnList newInsnList = new InsnList();

        for(AbstractInsnNode insn = original.getFirst(); insn != null; insn = insn.getNext()) {
            newInsnList.add(insn);
        }

        return newInsnList;
    }

    public static InsnList cloneInsnList(InsnList original) {
        InsnList newInsnList = new InsnList();
        Map<LabelNode, LabelNode> labels = new HashMap();

        AbstractInsnNode insn;
        for(insn = original.getFirst(); insn != null; insn = insn.getNext()) {
            if (insn instanceof LabelNode) {
                labels.put((LabelNode)insn, new LabelNode());
            }
        }

        for(insn = original.getFirst(); insn != null; insn = insn.getNext()) {
            newInsnList.add(insn.clone(labels));
        }

        return newInsnList;
    }

    public static AbstractInsnNode getIntInsn(int number) {
        if (number >= -1 && number <= 5) {
            return new InsnNode(number + 3);
        } else if (number >= -128 && number <= 127) {
            return new IntInsnNode(16, number);
        } else {
            return (AbstractInsnNode)(number >= -32768 && number <= 32767 ? new IntInsnNode(17, number) : new LdcInsnNode(number));
        }
    }

    public static AbstractInsnNode getLongInsn(long number) {
        return (AbstractInsnNode)(number >= 0L && number <= 1L ? new InsnNode((int)(number + 9L)) : new LdcInsnNode(number));
    }

    public static AbstractInsnNode getFloatInsn(float number) {
        return (AbstractInsnNode)(number >= 0.0F && number <= 2.0F ? new InsnNode((int)(number + 11.0F)) : new LdcInsnNode(number));
    }

    public static AbstractInsnNode getDoubleInsn(double number) {
        return (AbstractInsnNode)(number >= 0.0D && number <= 1.0D ? new InsnNode((int)(number + 14.0D)) : new LdcInsnNode(number));
    }

    public static void printClass(ClassNode classNode) {
        System.out.println(classNode.name + '\n');
        classNode.methods.forEach((methodNode) -> {
            System.out.println(methodNode.name + " " + methodNode.desc);

            for(int i = 0; i < methodNode.instructions.size(); ++i) {
                System.out.printf("%s:   %s \n", i, prettyprint(methodNode.instructions.get(i)));
            }

        });
    }

    public static boolean isInteger(AbstractInsnNode ain) {
        if (ain == null) {
            return false;
        } else if ((ain.getOpcode() < 2 || ain.getOpcode() > 8) && ain.getOpcode() != 17 && ain.getOpcode() != 16) {
            if (ain instanceof LdcInsnNode) {
                LdcInsnNode ldc = (LdcInsnNode)ain;
                if (ldc.cst instanceof Integer) {
                    return true;
                }
            }

            return false;
        } else {
            return true;
        }
    }

    public static int getIntValue(AbstractInsnNode node) {
        if (node.getOpcode() >= 2 && node.getOpcode() <= 8) {
            return node.getOpcode() - 3;
        } else if (node.getOpcode() != 17 && node.getOpcode() != 16) {
            if (node instanceof LdcInsnNode) {
                LdcInsnNode ldc = (LdcInsnNode)node;
                if (ldc.cst instanceof Integer) {
                    return (Integer)ldc.cst;
                }
            }

            return 0;
        } else {
            return ((IntInsnNode)node).operand;
        }
    }

    public static boolean isLong(AbstractInsnNode ain) {
        if (ain == null) {
            return false;
        } else if (ain.getOpcode() != 9 && ain.getOpcode() != 10) {
            if (ain instanceof LdcInsnNode) {
                LdcInsnNode ldc = (LdcInsnNode)ain;
                if (ldc.cst instanceof Long) {
                    return true;
                }
            }

            return false;
        } else {
            return true;
        }
    }

    public static long getLongValue(AbstractInsnNode node) {
        if (node.getOpcode() >= 9 && node.getOpcode() <= 10) {
            return (long)(node.getOpcode() - 9);
        } else {
            if (node instanceof LdcInsnNode) {
                LdcInsnNode ldc = (LdcInsnNode)node;
                if (ldc.cst instanceof Long) {
                    return (Long)ldc.cst;
                }
            }

            return 0L;
        }
    }

    public static List<byte[]> loadBytes(File input) {
        List<byte[]> result = new ArrayList();
        Throwable var3;
        if (input.getName().endsWith(".jar")) {
            try {
                ZipFile zipIn = new ZipFile(input);
                var3 = null;

                try {
                    Enumeration e = zipIn.entries();

                    while(e.hasMoreElements()) {
                        ZipEntry next = (ZipEntry)e.nextElement();
                        if (next.getName().endsWith(".class")) {
                            try {
                                InputStream in = zipIn.getInputStream(next);
                                Throwable var7 = null;

                                try {
                                    result.add(IOUtils.toByteArray(in));
                                } catch (Throwable var60) {
                                    var7 = var60;
                                    throw var60;
                                } finally {
                                    if (in != null) {
                                        if (var7 != null) {
                                            try {
                                                in.close();
                                            } catch (Throwable var59) {
                                                var7.addSuppressed(var59);
                                            }
                                        } else {
                                            in.close();
                                        }
                                    }

                                }
                            } catch (IllegalArgumentException var65) {
                                System.out.println("Could not parse " + next.getName() + " (is it a class?)");
                            }
                        }
                    }
                } catch (Throwable var66) {
                    var3 = var66;
                    throw var66;
                } finally {
                    if (zipIn != null) {
                        if (var3 != null) {
                            try {
                                zipIn.close();
                            } catch (Throwable var58) {
                                var3.addSuppressed(var58);
                            }
                        } else {
                            zipIn.close();
                        }
                    }

                }
            } catch (IOException var68) {
                var68.printStackTrace(System.out);
            }
        } else if (input.getName().endsWith(".class")) {
            try {
                InputStream in = new FileInputStream(input);
                var3 = null;

                try {
                    result.add(IOUtils.toByteArray(in));
                } catch (Throwable var61) {
                    var3 = var61;
                    throw var61;
                } finally {
                    if (in != null) {
                        if (var3 != null) {
                            try {
                                in.close();
                            } catch (Throwable var57) {
                                var3.addSuppressed(var57);
                            }
                        } else {
                            in.close();
                        }
                    }

                }
            } catch (Throwable var63) {
                System.out.println("Could not parse " + input.getName() + " (is it a class?)");
            }
        }

        return result;
    }

    public static Map<LabelNode, LabelNode> generateCloneMap(InsnList list) {
        Map<LabelNode, LabelNode> result = new HashMap();
        list.iterator().forEachRemaining((insn) -> {
            if (insn instanceof LabelNode) {
                result.put((LabelNode)insn, new LabelNode());
            }

        });
        return result;
    }

    public static int getPullValue(AbstractInsnNode ain, boolean includeShifts) {
        int var5;
        switch(ain.getOpcode()) {
            case 46:
            case 47:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
                return 2;
            case 54:
            case 56:
            case 58:
                return 1;
            case 55:
            case 57:
                return 2;
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 132:
            case 145:
            case 146:
            case 147:
            case 167:
            case 168:
            case 169:
            case 177:
            case 178:
            case 187:
            case 196:
            default:
                break;
            case 79:
            case 81:
            case 83:
            case 84:
            case 85:
            case 86:
                return 3;
            case 80:
            case 82:
                return 4;
            case 87:
                return 1;
            case 88:
                return 2;
            case 89:
                if (includeShifts) {
                    return 1;
                }
                break;
            case 90:
                if (includeShifts) {
                    return 1;
                }
                break;
            case 91:
                if (includeShifts) {
                    return 1;
                }
                break;
            case 92:
                if (includeShifts) {
                    return 2;
                }
                break;
            case 93:
                if (includeShifts) {
                    return 2;
                }
                break;
            case 94:
                if (includeShifts) {
                    return 2;
                }
                break;
            case 95:
                if (includeShifts) {
                    return 2;
                }
                break;
            case 96:
            case 100:
            case 104:
            case 108:
            case 112:
            case 120:
            case 122:
            case 124:
            case 126:
            case 128:
            case 130:
                return 2;
            case 97:
            case 101:
            case 105:
            case 109:
            case 113:
            case 127:
            case 129:
            case 131:
            case 148:
                return 4;
            case 98:
            case 102:
            case 106:
            case 110:
            case 114:
            case 149:
            case 150:
                return 2;
            case 99:
            case 103:
            case 107:
            case 111:
            case 115:
            case 151:
            case 152:
                return 4;
            case 116:
            case 118:
                return 1;
            case 117:
            case 119:
                return 2;
            case 121:
            case 123:
            case 125:
                return 3;
            case 133:
            case 134:
            case 135:
                return 1;
            case 136:
            case 137:
            case 138:
                return 2;
            case 139:
            case 140:
            case 141:
                return 1;
            case 142:
            case 143:
            case 144:
                return 2;
            case 153:
            case 154:
            case 155:
            case 156:
            case 157:
            case 158:
                return 1;
            case 159:
            case 160:
            case 161:
            case 162:
            case 163:
            case 164:
            case 165:
            case 166:
                return 2;
            case 170:
            case 171:
                return 1;
            case 172:
            case 174:
            case 176:
                return 1;
            case 173:
            case 175:
                return 2;
            case 179:
                if (Type.getType(((FieldInsnNode)ain).desc).getSort() != 7 && Type.getType(((FieldInsnNode)ain).desc).getSort() != 8) {
                    return 1;
                }

                return 2;
            case 180:
                return 1;
            case 181:
                if (Type.getType(((FieldInsnNode)ain).desc).getSort() != 7 && Type.getType(((FieldInsnNode)ain).desc).getSort() != 8) {
                    return 2;
                }

                return 3;
            case 182:
            case 183:
            case 184:
            case 185:
                int args = 0;
                if (ain.getOpcode() != 184) {
                    ++args;
                }

                Type[] var8 = Type.getArgumentTypes(((MethodInsnNode)ain).desc);
                int var9 = var8.length;

                for(var5 = 0; var5 < var9; ++var5) {
                    Type t = var8[var5];
                    if (t.getSort() != 7 && t.getSort() != 8) {
                        ++args;
                    } else {
                        args += 2;
                    }
                }

                return args;
            case 186:
                int args1 = 0;
                Type[] var4 = Type.getArgumentTypes(((InvokeDynamicInsnNode)ain).desc);
                var5 = var4.length;

                for(int var6 = 0; var6 < var5; ++var6) {
                    Type t = var4[var6];
                    if (t.getSort() != 7 && t.getSort() != 8) {
                        ++args1;
                    } else {
                        args1 += 2;
                    }
                }

                return args1;
            case 188:
            case 189:
            case 190:
            case 191:
            case 192:
            case 193:
            case 194:
            case 195:
            case 198:
            case 199:
                return 1;
            case 197:
                return ((MultiANewArrayInsnNode)ain).dims;
        }

        return 0;
    }

    static {
        methodPrinter = new TraceMethodVisitor(printer);
    }
}
