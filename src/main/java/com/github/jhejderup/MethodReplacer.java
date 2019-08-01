package com.github.jhejderup;

import org.objectweb.asm.*;
import org.objectweb.asm.Type;

//https://stackoverflow.com/questions/45180625/how-to-remove-method-body-at-runtime-with-asm-5-2
public class MethodReplacer extends ClassVisitor {

    private final String hotMethodName, hotClassName;

    public MethodReplacer(ClassWriter cw, String methodName, String clazzName) {
        super(Opcodes.ASM5, cw);
        hotMethodName = methodName;
        hotClassName = clazzName;
    }

    // invoked for every method
    @Override
    public MethodVisitor visitMethod(
            int access, String name, String desc, String signature, String[] exceptions) {

        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if(!name.contains(hotMethodName))
            // reproduce the methods we're not interested in, unchanged
            return mv;

        // alter the behavior for the specific method
        return new ReplaceWithNULL(
                mv,
                (Type.getArgumentsAndReturnSizes(desc)>>2)-1);
    }

    private class ReplaceWithNULL extends MethodVisitor {
        private final MethodVisitor mv;
        private final int newMaxLocals;

        ReplaceWithNULL(MethodVisitor mv, int newMaxL) {
            super(Opcodes.ASM5);
            this.mv = mv;
            newMaxLocals = newMaxL;
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            mv.visitMaxs(0, newMaxLocals);
        }

        @Override
        public void visitCode() {
            mv.visitCode();
            mv.visitInsn(Opcodes.ACONST_NULL);
            mv.visitInsn(Opcodes.ARETURN);// our new code
        }

        @Override
        public void visitEnd() {
            mv.visitEnd();
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            return mv.visitAnnotation(desc, visible);
        }

        @Override
        public void visitParameter(String name, int access) {
            mv.visitParameter(name, access);
        }
    }
}

