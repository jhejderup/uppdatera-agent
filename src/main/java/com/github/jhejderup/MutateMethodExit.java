package com.github.jhejderup;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;


import java.util.HashMap;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;


public class MutateMethodExit extends ClassVisitor {

    private final String hotMethodName, hotClassName;

    public MutateMethodExit(ClassWriter cw, String methodName, String clazzName) {
        super(Opcodes.ASM5, cw);
        hotMethodName = methodName;
        hotClassName = clazzName;
    }


    // invoked for every method
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

        if (!name.equals("isBlank")
                && !name.equals("isEmpty")
                && !name.equals("isNotBlank")
                && !name.equals("isNotEmpty"))
            return mv;


        return new MutateReturn(mv, access, name, desc);


    }

    private class MutateReturn extends AdviceAdapter {

        MutateReturn(final MethodVisitor mv, final int access, final String name, final String desc) {
            super(Opcodes.ASM5, mv, access, name, desc);
        }


        @Override
        protected void onMethodExit(int opcode) {
            if (opcode != ATHROW && Type.getReturnType(this.methodDesc) == Type.BOOLEAN_TYPE) {
                Label branch = new Label();
                Label rtn = new Label();
                visitJumpInsn(IFNE, branch);
                visitInsn(ICONST_1);
                visitJumpInsn(GOTO, rtn);
                visitLabel(branch);
                visitInsn(ICONST_0);
                visitLabel(rtn);
            }
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            super.visitMaxs(0, 0);
        }

    }
}


