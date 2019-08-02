package com.github.jhejderup;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;

import java.io.*;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class Transformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer)
            throws IllegalClassFormatException {

        System.out.println(className);

        if (className.contains("org/apache/commons/lang3/StringUtils")) {
            System.out.println(className);
            ClassReader reader = new ClassReader(classfileBuffer);
            ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES);
           // MethodReplacer visitor = new MethodReplacer(writer,"stringz", className);

            MutateReturnValue visitor = new MutateReturnValue(writer,"intz", className);
            //ConsumeIteratorParameter visitor = new ConsumeIteratorParameter(writer,"intz", className);
            reader.accept(visitor, ClassReader.SKIP_FRAMES);
            return writer.toByteArray();
        }
        return null;
    }


}

