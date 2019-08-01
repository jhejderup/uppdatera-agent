package com.github.jhejderup;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class Transformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer)
            throws IllegalClassFormatException {

        if (className.contains("DependencyClass")) {
            System.out.println(className);
            ClassReader reader = new ClassReader(classfileBuffer);
            ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES);
            //ClassVisitor visitor = new ClazzVisitor(writer, className);
            MethodReplacer visitor = new MethodReplacer(writer,"stringz", className);
            reader.accept(visitor, 0);
            return writer.toByteArray();
        }
        return null;
    }


}

