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
        if (!name.equals("fromJson") && !name.equals("toJson"))
            return mv;

        return new MutateReturn(mv, access, name, desc);
    }

    private class MutateReturn extends AdviceAdapter {

        MutateReturn(final MethodVisitor mv, final int access, final String name, final String desc) {
            super(Opcodes.ASM5, mv, access, name, desc);
        }


        @Override
        public void visitJumpInsn(int opcode, Label label) {
            if(opcode == IFNULL) {
                super.visitJumpInsn(IFNONNULL, label);
            } else if(opcode == IFNONNULL){
                super.visitJumpInsn(IFNULL, label);
            } else {
                super.visitJumpInsn(opcode, label);
            }
        }

        @Override
        protected void onMethodExit(int opcode) {
                super.onMethodExit(opcode);
        }

        @Override
        protected void onMethodEnter() {
            super.onMethodEnter();
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            super.visitMaxs(0, 0);
        }

    }
}


