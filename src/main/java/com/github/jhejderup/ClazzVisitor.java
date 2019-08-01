package com.github.jhejderup;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ClazzVisitor extends ClassVisitor {
    private String className;

    public ClazzVisitor(ClassVisitor cv, String pClassName) {
        super(Opcodes.ASM5, cv);
        className = pClassName;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {

        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals("boolz") || name.equals("stringz") || name.equals("intz")) {
            System.out.println("Visiting method: " + name);
            return new MethodReturnAdapter(mv, access, name, desc);
        } else
            return mv;

    }

}

