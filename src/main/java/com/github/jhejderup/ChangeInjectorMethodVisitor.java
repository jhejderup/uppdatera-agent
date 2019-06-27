package com.github.jhejderup;


import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;


public class ChangeInjectorMethodVisitor extends MethodNode {

    public ChangeInjectorMethodVisitor(int access, String name, String desc,
    String signature, String[] exceptions, MethodVisitor mv) {
        super(Opcodes.ASM5, access, name, desc, signature, exceptions);
        this.mv = mv;
    }

    @Override
    public void visitEnd() {
        var itr = instructions.iterator();


        while (itr.hasNext()) {
            // Checks whether the instruction is ALOAD 3
            AbstractInsnNode node = itr.next();
            if (node.getOpcode() != Opcodes.ALOAD
                    || ((VarInsnNode) node).var != 3)
                continue;



            // Checks whether the next instruction is INVOKEVIRTUAL
            if (node.getNext() == null
                    || node.getNext().getOpcode() != Opcodes.INVOKEVIRTUAL)
                continue;



            // Checks the invoked method name and signature
            MethodInsnNode next = (MethodInsnNode) node.getNext();
            if (!next.owner.equals("java/lang/String")
                    || !next.name.equals("length")
                    || !next.desc.equals("()I"))
                continue;


            // Checks whether the next of the next instruction is ISTORE 4
            AbstractInsnNode next2 =  next.getNext();
            if (next2.getOpcode() != Opcodes.ISTORE
                    || ((VarInsnNode) next2).var != 4)
                continue;

            System.out.println("we here 5");
            // Creates a list instructions to be inserted
            InsnList list = new InsnList();
            list.add(new InsnNode(Opcodes.ICONST_0));
            list.add(new VarInsnNode(Opcodes.ISTORE,4));

            // Inserts the list, updates maxStack to at least 2, and we are done
            instructions.insert(next2, list);
            maxStack = Math.max(2, maxStack);
            break;
        }
        accept(mv);

    }
}

