package com.github.jhejderup;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;


public class MutateReturnValue extends ClassVisitor {

    private final String hotMethodName, hotClassName;


    public MutateReturnValue(ClassWriter cw, String methodName, String clazzName) {
        super(Opcodes.ASM5, cw);
        hotMethodName = methodName;
        hotClassName = clazzName;
    }

    // invoked for every method
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (!name.equals("boolz") && !name.equals("intz") && !name.equals("stringz"))
            return mv;

        return new MutateReturn(mv, access, name, desc);
    }

    private class MutateReturn extends AdviceAdapter {

        MutateReturn(final MethodVisitor mv, final int access, final String name, final String desc) {
            super(Opcodes.ASM5, mv, access, name, desc);
        }

        @Override
        protected void onMethodExit(int opcode) {

            //Negate boolean value
            if (opcode != ATHROW && Type.getReturnType(this.methodDesc) == Type.BOOLEAN_TYPE) {
                Label branch = new Label();
                Label rtn = new Label();
                visitJumpInsn(IFNE, branch);
                visitInsn(ICONST_1);
                visitJumpInsn(GOTO, rtn);
                visitLabel(branch);
                visitInsn(ICONST_0);
                visitLabel(rtn);
            }

            //Append random String
            if (opcode != ATHROW && (
                    Type.getReturnType(this.methodDesc).getSort() == Type.OBJECT &&
                            Type.getReturnType(this.methodDesc).getDescriptor().equals("Ljava/lang/String;"))) {

                visitInvokeDynamicInsn("makeConcatWithConstants",
                        "(Ljava/lang/String;)Ljava/lang/String;",
                        new Handle(Opcodes.H_INVOKESTATIC,
                                "java/lang/invoke/StringConcatFactory",
                                "makeConcatWithConstants",
                                "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite;", false)
                        , "\u0001YOO");
            }

            // ADD 1000 to a return variable
            if (opcode != ATHROW && Type.getReturnType(this.methodDesc) == Type.INT_TYPE) {
                System.out.println("Processsing: " + hotMethodName);
                visitIntInsn(SIPUSH, 1000);
                visitInsn(IADD);
            }


        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            super.visitMaxs(0, 0);
        }


    }
}


