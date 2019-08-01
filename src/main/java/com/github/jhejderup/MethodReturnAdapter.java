package com.github.jhejderup;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;

public class MethodReturnAdapter extends AdviceAdapter {

    public MethodReturnAdapter(MethodVisitor mv, int access, String name, String desc) {
        super(Opcodes.ASM5, mv, access, name, desc);
    }

    @Override
    protected void onMethodExit(int opcode) {
        //Negate boolean value
        if (opcode != ATHROW && Type.getReturnType(this.methodDesc) == Type.BOOLEAN_TYPE) {
            Label branch = new Label();
            Label rtn = new Label();
            mv.visitJumpInsn(IFNE, branch);
            mv.visitInsn(ICONST_1);
            mv.visitJumpInsn(GOTO, rtn);
            mv.visitLabel(branch);
            mv.visitInsn(ICONST_0);
            mv.visitLabel(rtn);
            mv.visitInsn(opcode);
        }

        //Append String value with a random String
        if (opcode != ATHROW && (
                Type.getReturnType(this.methodDesc).getSort() == Type.OBJECT &&
                        Type.getReturnType(this.methodDesc).getDescriptor().equals("Ljava/lang/String;"))) {

            mv.visitInvokeDynamicInsn("makeConcatWithConstants",
                    "(Ljava/lang/String;)Ljava/lang/String;",
                    new Handle(Opcodes.H_INVOKESTATIC,
                            "java/lang/invoke/StringConcatFactory",
                            "makeConcatWithConstants",
                            "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite;", false)
                    , "\u0001YOO");
        }

        // ADD 1000 to a return variable
        if (opcode != ATHROW && Type.getReturnType(this.methodDesc) == Type.INT_TYPE) {
            mv.visitIntInsn(SIPUSH, 1000);
            mv.visitInsn(IADD);
        }
    }
}
