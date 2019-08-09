package com.github.jhejderup;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;


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

        if(name.equals("fromJson"))
            return new MutateReturnFromJSON(mv, access, name, desc);

        if (name.equals("toJson"))
            return new MutateReturnToJSON(mv, access, name, desc);

        return mv;


    }

    private class MutateReturnToJSON extends AdviceAdapter {

        MutateReturnToJSON(final MethodVisitor mv, final int access, final String name, final String desc) {
            super(Opcodes.ASM5, mv, access, name, desc);
        }


        @Override
        protected void onMethodExit(int opcode) {
            Type[] args = Type.getArgumentTypes(this.methodDesc);

            if(args.length < 2){
                if(args[0].getSort() == Type.OBJECT && args[0].getDescriptor().equals("java/lang/Object;")){

                    if (opcode != ATHROW && (
                            Type.getReturnType(this.methodDesc).getSort() == Type.OBJECT &&
                                    Type.getReturnType(this.methodDesc).getDescriptor().equals("Ljava/lang/String;"))) {
                        visitLdcInsn("asdasdas");
                        visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "concat", "(Ljava/lang/String;)Ljava/lang/String;", false);
                    }

                } else {
                    super.onMethodExit(opcode);
                }

            } else {
                super.onMethodExit(opcode);
            }
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            super.visitMaxs(0, 0);
        }

    }

    private class MutateReturnFromJSON extends AdviceAdapter {

        MutateReturnFromJSON(final MethodVisitor mv, final int access, final String name, final String desc) {
            super(Opcodes.ASM5, mv, access, name, desc);
        }

        @Override
        protected void onMethodEnter() {
            Type[] args = Type.getArgumentTypes(this.methodDesc);
            if(args.length == 2 &&
                    args[0].getDescriptor().equals("Ljava/lang/String;") &&
                    args[1].getDescriptor().equals("Ljava/lang/Class;")){
                visitVarInsn(ALOAD, 1);
                visitLdcInsn("asdasdas");
                visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "concat", "(Ljava/lang/String;)Ljava/lang/String;", false);
                visitVarInsn(ASTORE, 1);
            } else {
                super.onMethodEnter();
            }
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            super.visitMaxs(0, 0);
        }

    }
}


