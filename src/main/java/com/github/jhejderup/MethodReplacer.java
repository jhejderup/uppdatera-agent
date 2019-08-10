package com.github.jhejderup;

import com.sun.org.apache.bcel.internal.generic.IRETURN;
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
        Type[] args =  Type.getArgumentTypes(desc);



        if(!name.contains(hotMethodName) &&
                !(args.length == 1) &&
                !(args[0].getDescriptor().equals("Ljava/io/InputStream;")))
            return mv;

        return new Replacer(mv);
    }

    private class Replacer extends MethodVisitor {
        private final MethodVisitor mv;

        Replacer(MethodVisitor mv) {
            super(Opcodes.ASM5);
            this.mv = mv;
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            mv.visitMaxs(0, 0);
        }

        @Override
        public void visitCode() {
            mv.visitCode();
            mv.visitInsn(Opcodes.RETURN);
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

