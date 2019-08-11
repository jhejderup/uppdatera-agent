package com.github.jhejderup;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;


import java.util.HashMap;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;


public class MutateMethodExit extends ClassVisitor {

    private final String hotMethodName, hotClassName;
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

    public MutateMethodExit(ClassWriter cw, String methodName, String clazzName) {
        super(Opcodes.ASM5, cw);
        hotMethodName = methodName;
        hotClassName = clazzName;
    }


    // invoked for every method
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

        if (!name.equals(this.hotMethodName))
            return mv;



        return new MutateReturn(mv, access, name, desc);



    }

    private class MutateReturn extends AdviceAdapter {



        MutateReturn(final MethodVisitor mv, final int access, final String name, final String desc) {
            super(Opcodes.ASM5, mv, access, name, desc);
        }

        @Override
        public void visitJumpInsn(int opcode, Label label) {
            super.visitJumpInsn(opcodeMap.get(opcode),label);
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            super.visitMaxs(0, 0);
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            return super.visitAnnotation(desc, visible);
        }

        @Override
        public void visitParameter(String name, int access) {
            super.visitParameter(name, access);
        }

    }
}


