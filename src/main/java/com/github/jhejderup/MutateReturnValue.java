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
            Type[] args = Type.getArgumentTypes(this.methodDesc);
            for (int i = 0; i < args.length; i++) {
                if (args[i] == Type.BOOLEAN_TYPE) {
                    int off = (this.methodAccess | Opcodes.ACC_STATIC) == 0 ? 0 : 1;
                    int param = i + off;
                    visitVarInsn(ILOAD, param);
                    System.out.println("We found the bool parameter at register: " + param);
                    Label branch = new Label();
                    Label end = new Label();
                    visitJumpInsn(IFNE, branch);
                    visitInsn(ICONST_1);
                    visitJumpInsn(GOTO, end);
                    visitLabel(branch);
                    visitInsn(ICONST_0);
                    visitLabel(end);
                    visitVarInsn(ISTORE, param);
                }
            }
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            super.visitMaxs(0, 0);
        }

    }
}


