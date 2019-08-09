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
        protected void onMethodEnter() {
            System.out.println(this.methodDesc);
            boolean no_mod = true;
            Type[] args = Type.getArgumentTypes(this.methodDesc);
            for (int i = 0; i < args.length ; i++) {
                if(
                        args[i].getSort() == Type.OBJECT &&
                        args[i].getDescriptor().equals("Ljava/lang/Iterable;")){


                    Type file = Type.getType("Ljava/io/File;");
                    int id = newLocal(file);
                    Label label0 = new Label();
                    Label label1 = new Label();
                    Label label2 = new Label();
                    Label label4 = new Label();
                    visitTryCatchBlock(label0, label1, label2, "java/lang/Exception");
                    visitLabel(label0);
                    visitTypeInsn(NEW, "java/io/File");
                    visitInsn(DUP);
                    visitLdcInsn("hacked123-iter.txt");
                    visitMethodInsn(INVOKESPECIAL, "java/io/File", "<init>", "(Ljava/lang/String;)V", false);
                    visitVarInsn(ASTORE, id);
                    visitVarInsn(ALOAD, id);
                    visitMethodInsn(INVOKEVIRTUAL, "java/io/File", "createNewFile", "()Z", false);
                    visitInsn(POP);
                    visitLabel(label1);
                    visitJumpInsn(GOTO, label4);
                    visitLabel(label2);
                    visitVarInsn(ASTORE, id);
                    visitLabel(label4);
                    no_mod=false;
                    break;
                }

                if(
                        args[i].getSort() == Type.ARRAY &&
                        args[i].getDescriptor().equals("[Ljava/lang/Object;")){
                    Type file = Type.getType("Ljava/io/File;");
                    int id = newLocal(file);
                    Label label0 = new Label();
                    Label label1 = new Label();
                    Label label2 = new Label();
                    Label label4 = new Label();
                    visitTryCatchBlock(label0, label1, label2, "java/lang/Exception");
                    visitLabel(label0);
                    visitTypeInsn(NEW, "java/io/File");
                    visitInsn(DUP);
                    visitLdcInsn("hacked123-elem.txt");
                    visitMethodInsn(INVOKESPECIAL, "java/io/File", "<init>", "(Ljava/lang/String;)V", false);
                    visitVarInsn(ASTORE, id);
                    visitVarInsn(ALOAD, id);
                    visitMethodInsn(INVOKEVIRTUAL, "java/io/File", "createNewFile", "()Z", false);
                    visitInsn(POP);
                    visitLabel(label1);
                    visitJumpInsn(GOTO, label4);
                    visitLabel(label2);
                    visitVarInsn(ASTORE, id);
                    visitLabel(label4);
                    no_mod=false;
                    break;
                }
            }
            if(no_mod)
                super.onMethodEnter();
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            super.visitMaxs(0, 0);
        }

    }
}


