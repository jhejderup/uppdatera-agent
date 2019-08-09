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

        if (!name.equals(this.hotMethodName))
            return mv;

        return new MutateReturn(mv, access, name, desc);

    }

    private class MutateReturn extends AdviceAdapter {

        MutateReturn(final MethodVisitor mv, final int access, final String name, final String desc) {
            super(Opcodes.ASM5, mv, access, name, desc);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            if (opcode == INVOKESTATIC
                    && owner.equals("java/util/Collections")
                    && name.equals("reverse")
                    && descriptor.equals("(Ljava/util/List;)V")
                    && isInterface == false){
                visitMethodInsn(INVOKESTATIC, "java/util/Collections", "shuffle", "(Ljava/util/List;)V", false);
            } else {
                super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            }

        }

        @Override
        protected void onMethodEnter() {
            super.onMethodEnter();
        }

        @Override
        protected void onMethodExit(int opcode) {
           super.onMethodExit(opcode);
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            super.visitMaxs(0, 0);
        }

    }
}


