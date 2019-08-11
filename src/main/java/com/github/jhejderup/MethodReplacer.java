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

        if(!name.contains(hotMethodName))
            return mv;

        if(args.length == 3
                && args[0].getDescriptor().equals("Ljava/lang/String;")
                && args[1].getDescriptor().equals("Ljava/lang/String;")
                && args[2].getDescriptor().equals("Ljava/lang/String;")){
            return new Replacer(mv);
        } else {
            return mv;
        }

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
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitVarInsn(Opcodes.ALOAD, 2);
            mv.visitInsn(Opcodes.ICONST_1);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "org/apache/commons/lang3/StringUtils", "replace", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/String;", false);
            mv.visitInsn(Opcodes.ARETURN);
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

