package com.github.jhejderup;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ChangeInjectorClassVisitor extends ClassVisitor {
    private String className;

    public ChangeInjectorClassVisitor(ClassVisitor cv, String pClassName) {
        super(Opcodes.ASM5, cv);
        className = pClassName;
    }



    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {

        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if(name.equals("parseTweet")){
            System.out.println("Visiting method: " + name);
            return new NegateCondition(access,name, desc, signature, exceptions,mv);
           // return new ChangeInjectorMethodVisitor(access,name, desc, signature, exceptions,mv);
        } else
            return mv;

    }

}

