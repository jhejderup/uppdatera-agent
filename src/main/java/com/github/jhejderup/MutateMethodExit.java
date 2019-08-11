package com.github.jhejderup;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;


import java.util.HashMap;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;


public class MutateMethodExit extends ClassVisitor {

    private final String hotMethodName, hotClassName;
    private static Map<Integer, Integer> opcodeMap = new HashMap<Integer, Integer>();

    static {
        opcodeMap.put(IF_ACMPEQ, IF_ACMPNE);
        opcodeMap.put(IF_ACMPNE, IF_ACMPEQ);
        opcodeMap.put(IF_ICMPEQ, IF_ICMPNE);
        opcodeMap.put(IF_ICMPGE, IF_ICMPLT);
        opcodeMap.put(IF_ICMPGT, IF_ICMPLE);
        opcodeMap.put(IF_ICMPLE, IF_ICMPGT);
        opcodeMap.put(IF_ICMPLT, IF_ICMPGE);
        opcodeMap.put(IF_ICMPNE, IF_ICMPEQ);
        opcodeMap.put(IFEQ, IFNE);
        opcodeMap.put(IFGE, IFLT);
        opcodeMap.put(IFGT, IFLE);
        opcodeMap.put(IFLE, IFGT);
        opcodeMap.put(IFLT, IFGE);
        opcodeMap.put(IFNE, IFEQ);
        opcodeMap.put(IFNONNULL, IFNULL);
        opcodeMap.put(IFNULL, IFNONNULL);
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
            super.visitJumpInsn(opcodeMap.get(opcode), label);
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


