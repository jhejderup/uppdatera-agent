package com.github.jhejderup;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;

import java.util.HashMap;
import java.util.Map;


public class RecordMethodInvocation extends ClassVisitor {

    private static Map<Integer, Integer> opcodeMap = new HashMap<>();
    private final String hotClassName;

    public RecordMethodInvocation(ClassWriter cw, String clazzName) {
        super(Opcodes.ASM5, cw);
        hotClassName = clazzName;
    }


    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

        return new InstrumentMethod(mv, access, name, desc);


    }

    private class InstrumentMethod extends AdviceAdapter {

        private final String hotMethodName;


        InstrumentMethod(final MethodVisitor mv, final int access, final String name, final String desc) {
            super(Opcodes.ASM5, mv, access, name, desc);
            this.hotMethodName = name;
        }

        @Override
        protected void onMethodEnter() {

            int bytes = newLocal(Type.getType("[B"));
            int path = newLocal(Type.getType("Ljava/nio/file/Path;"));
            int obj = newLocal(Type.getType("Ljava/lang/Object;"));
            int option = newLocal(Type.getType("Ljava/nio/file/StandardOpenOption;"));
            Label label0 = new Label();
            Label label1 = new Label();
            Label label2 = new Label();
            visitTryCatchBlock(label0, label1, label2, "java/io/IOException");
            visitLabel(label0);
            visitLdcInsn("dyncalls.txt");
            visitInsn(ICONST_0);
            visitTypeInsn(ANEWARRAY, "java/lang/String");
            visitMethodInsn(INVOKESTATIC, "java/nio/file/Paths", "get", "(Ljava/lang/String;[Ljava/lang/String;)Ljava/nio/file/Path;", false);
            visitVarInsn(ASTORE, path);
            Label label3 = new Label();
            visitLabel(label3);
            visitLdcInsn("L" + hotClassName + "/" + hotMethodName + this.methodDesc + "\n");
            visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "getBytes", "()[B", false);
            visitVarInsn(ASTORE, bytes);
            Label label4 = new Label();
            visitLabel(label4);
            visitVarInsn(ALOAD, path);
            visitInsn(ICONST_0);
            visitTypeInsn(ANEWARRAY, "java/nio/file/LinkOption");
            visitMethodInsn(INVOKESTATIC, "java/nio/file/Files", "exists", "(Ljava/nio/file/Path;[Ljava/nio/file/LinkOption;)Z", false);
            Label label5 = new Label();
            visitJumpInsn(IFEQ, label5);
            visitFieldInsn(GETSTATIC, "java/nio/file/StandardOpenOption", "APPEND", "Ljava/nio/file/StandardOpenOption;");
            Label label6 = new Label();
            visitJumpInsn(GOTO, label6);
            visitLabel(label5);
            visitFieldInsn(GETSTATIC, "java/nio/file/StandardOpenOption", "CREATE", "Ljava/nio/file/StandardOpenOption;");
            visitLabel(label6);
            visitVarInsn(ASTORE, option);
            Label label7 = new Label();
            visitLabel(label7);
            visitVarInsn(ALOAD, path);
            visitVarInsn(ALOAD, bytes);
            visitInsn(ICONST_1);
            visitTypeInsn(ANEWARRAY, "java/nio/file/OpenOption");
            visitInsn(DUP);
            visitInsn(ICONST_0);
            visitVarInsn(ALOAD, option);
            visitInsn(AASTORE);
            visitMethodInsn(INVOKESTATIC, "java/nio/file/Files", "write", "(Ljava/nio/file/Path;[B[Ljava/nio/file/OpenOption;)Ljava/nio/file/Path;", false);
            visitInsn(POP);
            visitLabel(label1);
            visitLineNumber(95, label1);
            Label label8 = new Label();
            visitJumpInsn(GOTO, label8);
            visitLabel(label2);
            visitVarInsn(ASTORE, obj);
            Label label9 = new Label();
            visitLabel(label9);
            visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            visitVarInsn(ALOAD, obj);
            visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V", false);
            visitLabel(label8);
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            super.visitMaxs(0, 0);
        }
    }
}


