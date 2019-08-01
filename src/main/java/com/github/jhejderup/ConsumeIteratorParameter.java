package com.github.jhejderup;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;

public class ConsumeIteratorParameter extends ClassVisitor {

    private final String hotMethodName, hotClassName;

    public ConsumeIteratorParameter(ClassWriter cw, String methodName, String clazzName) {
        super(Opcodes.ASM5, cw);
        hotMethodName = methodName;
        hotClassName = clazzName;
    }

    // invoked for every method
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (!name.equals("interzz"))
            return mv;

        return new ConsumeIterable(mv, access, name, desc);
    }

    private class ConsumeIterable extends AdviceAdapter {

        ConsumeIterable(final MethodVisitor mv, final int access, final String name, final String desc) {
            super(Opcodes.ASM5, mv, access, name, desc);
        }

        @Override
        protected void onMethodEnter() {
            Type[] args = Type.getArgumentTypes(this.methodDesc);
            for (int i = 0; i < args.length; i++) {
                if (args[i].getSort() == Type.OBJECT && args[i].getDescriptor().equals("Ljava/lang/Iterable;")) {
                    Label EMPTY = new Label();
                    Type type = Type.getType("Ljava/util/Iterator;");
                    int id = newLocal(type);
                    System.out.println("Inside:" + args[i].getDescriptor());
                    int off = (this.methodAccess | Opcodes.ACC_STATIC) == 0 ? 0 : 1;
                    int param = i + off;
                    System.out.println("Parameter: " + param);
                    int opcode = Type.getArgumentTypes(this.methodDesc)[i].getOpcode(Opcodes.IALOAD);
                    System.out.println("Opcode: " + opcode);
                    System.out.println("Type: " + type.getClassName());
                    System.out.println("ID: " + id);
                    visitVarInsn(opcode, param);
                    visitMethodInsn(INVOKEINTERFACE, "java/lang/Iterable", "iterator", "()Ljava/util/Iterator;", true);
                    visitVarInsn(AASTORE, id);
                    visitVarInsn(AALOAD, id);
                    visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);
                    visitJumpInsn(IFEQ, EMPTY);
                    visitVarInsn(AALOAD, id);
                    visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
                    visitInsn(POP);
                    visitLabel(EMPTY);
                } else {
                    System.out.println("Outside: " + args[i].getDescriptor());
                }
            }
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            super.visitMaxs(0, 0);
        }


    }


}
