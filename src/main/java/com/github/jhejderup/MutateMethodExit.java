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

        Type[] args = Type.getArgumentTypes(desc);

        if(args.length == 2
                && args[0].getDescriptor().equals("Ljava/lang/String;")
                && args[1].getDescriptor().equals("Ljava/lang/String;")) {
            return new MutateReturn(mv, access, name, desc);
        } else {
            return mv;
        }


    }

    private class MutateReturn extends AdviceAdapter {

        MutateReturn(final MethodVisitor mv, final int access, final String name, final String desc) {
            super(Opcodes.ASM5, mv, access, name, desc);
        }

        @Override
        protected void onMethodExit(int opcode) {
            super.onMethodExit(opcode);
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


