package com.github.jhejderup;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

import java.util.HashMap;
import java.util.Map;

public class NegateCondition extends MethodNode {

    public static final String NAME = "NegateCondition";
    private static Map<Integer, Integer> opcodeMap = new HashMap<>();

    static {
        opcodeMap.put(Opcodes.IF_ACMPEQ, Opcodes.IF_ACMPNE);
        opcodeMap.put(Opcodes.IF_ACMPNE, Opcodes.IF_ACMPEQ);
        opcodeMap.put(Opcodes.IF_ICMPEQ, Opcodes.IF_ICMPNE);
        opcodeMap.put(Opcodes.IF_ICMPGE, Opcodes.IF_ICMPLT);
        opcodeMap.put(Opcodes.IF_ICMPGT, Opcodes.IF_ICMPLE);
        opcodeMap.put(Opcodes.IF_ICMPLE, Opcodes.IF_ICMPGT);
        opcodeMap.put(Opcodes.IF_ICMPLT, Opcodes.IF_ICMPGE);
        opcodeMap.put(Opcodes.IF_ICMPNE, Opcodes.IF_ICMPEQ);
        opcodeMap.put(Opcodes.IFEQ, Opcodes.IFNE);
        opcodeMap.put(Opcodes.IFGE, Opcodes.IFLT);
        opcodeMap.put(Opcodes.IFGT, Opcodes.IFLE);
        opcodeMap.put(Opcodes.IFLE, Opcodes.IFGT);
        opcodeMap.put(Opcodes.IFLT, Opcodes.IFGE);
        opcodeMap.put(Opcodes.IFNE, Opcodes.IFEQ);
        opcodeMap.put(Opcodes.IFNONNULL, Opcodes.IFNULL);
        opcodeMap.put(Opcodes.IFNULL, Opcodes.IFNONNULL);
    }

    public Boolean alternate;


    public NegateCondition(int access, String name, String desc,
                           String signature, String[] exceptions, MethodVisitor mv) {
        super(Opcodes.ASM5, access, name, desc, signature, exceptions);
        this.mv = mv;
        alternate = true;


    }

    private static int flip(int opcode) {
        return opcodeMap.get(opcode);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        if (alternate) {
            System.out.println("Old code:" + opcode + ", new: " + flip(opcode));
            super.visitJumpInsn(Opcodes.IFNULL, label);
            alternate = false;
        } else {
            System.out.println("Old code:" + opcode + ", new: " + flip(opcode));
            super.visitJumpInsn(Opcodes.IFNULL, label);
        }
        accept(mv);

    }
}
