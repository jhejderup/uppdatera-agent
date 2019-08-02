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

                    // Iterable parameter
                    int off = (this.methodAccess | Opcodes.ACC_STATIC) == 0 ? 0 : 1;
                    int param = i + off;
                    System.out.println("Parameter: " + param);
                    //New var to store iterator
                    Type it_type = Type.getType("Ljava/util/Iterator;");
                    int it_id = newLocal(it_type);
                    System.out.println("ITR_ID: " + it_id );

                    // New var to store array
                    Type arr_type = Type.getType("Ljava/util/Collection;");
                    int arr_id = newLocal(arr_type);
                    System.out.println("ARR_ID: " + arr_id );

                    //New generic placeholder var
                    int gen_id = newLocal(Type.getType("Ljava/lang/Object;"));
                    System.out.println("LOOP_ID: " + gen_id);

                    // Create new array and store it
                    visitTypeInsn(NEW, "java/util/ArrayList");
                    visitInsn(DUP);
                    visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
                    visitVarInsn(ASTORE, arr_id);

                    // Load iterator parameter and store it
                    visitVarInsn(ALOAD, param);
                    visitMethodInsn(INVOKEINTERFACE, "java/lang/Iterable", "iterator", "()Ljava/util/Iterator;", true);
                    visitVarInsn(ASTORE, it_id);

                    //Load iterator and check hasNext
                    Label doLoop = new Label();
                    visitLabel(doLoop);
                    visitVarInsn(ALOAD, it_id);
                    visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);
                    Label doneLoop = new Label();
                    //loop and add elements to a new list
                    visitJumpInsn(IFEQ, doneLoop);
                    visitVarInsn(ALOAD, it_id);
                    visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
                //    visitTypeInsn(CHECKCAST, "java/lang/String");
                    visitVarInsn(ASTORE, gen_id);
                    visitVarInsn(ALOAD, arr_id);
                    visitVarInsn(ALOAD, gen_id);
                    visitMethodInsn(INVOKEINTERFACE, "java/util/Collection", "add", "(Ljava/lang/Object;)Z", true);
                    visitInsn(POP);
                    visitJumpInsn(GOTO, doLoop);
                    //done with adding all elements
                    //duplicate array with the same elements
                    visitLabel(doneLoop);
                    visitVarInsn(ALOAD, arr_id);
                    visitVarInsn(ALOAD, arr_id);
                    visitMethodInsn(INVOKEINTERFACE, "java/util/Collection", "addAll", "(Ljava/util/Collection;)Z", true);
                    visitInsn(POP);
                    //store the array in place of the iterator
                    visitVarInsn(ALOAD, arr_id);
                    visitVarInsn(ASTORE, param);
                } else {
                    System.out.println("Outside: " + args[i].getDescriptor() + "Sort: " + args[i].getSort());
                }
            }
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            super.visitMaxs(0, 0);
        }


    }


}
