package com.github.jhejderup;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;


public class MutateReturnValue extends ClassVisitor {

    private final String hotMethodName, hotClassName;
    private int ASTORE_STR_VAR;


    public MutateReturnValue(ClassWriter cw, String methodName, String clazzName) {
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
        protected void onMethodEnter() {
            Type str = Type.getType("Ljava/lang/String;");
            ASTORE_STR_VAR = newLocal(str);
            visitLdcInsn("\n\n\n");
            visitVarInsn(ASTORE,ASTORE_STR_VAR);
        }

        @Override
        protected void onMethodExit(int opcode) {
            Type[] args = Type.getArgumentTypes(this.methodDesc);
            if (args.length == 6 && opcode != ATHROW && (
                    Type.getReturnType(this.methodDesc).getSort() == Type.OBJECT &&
                            Type.getReturnType(this.methodDesc).getDescriptor().equals("Ljava/lang/String;"))) {
                visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                visitLdcInsn("\n\n\n");
                visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            }
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            super.visitMaxs(0, 0);
        }

    }
}


