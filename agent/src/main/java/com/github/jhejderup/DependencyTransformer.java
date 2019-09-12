package com.github.jhejderup;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.HashSet;

public class DependencyTransformer implements ClassFileTransformer {

    private final HashSet<String> dependencies;

    public DependencyTransformer(HashSet<String> dependencies) {
        this.dependencies = dependencies;

    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {

        String[] segments = className.split("/");
        String pkgName = String.join("/", Arrays.copyOf(segments, segments.length - 1));

        if (this.dependencies.contains(pkgName)) {
            ClassReader reader = new ClassReader(classfileBuffer);
            ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);
            InstrumentMethodBodies visitor = new InstrumentMethodBodies(writer, className);
            reader.accept(visitor, ClassReader.EXPAND_FRAMES);
            return writer.toByteArray();
        }
        return null;
    }

}

