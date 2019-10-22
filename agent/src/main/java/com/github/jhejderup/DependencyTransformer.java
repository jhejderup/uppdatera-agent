package com.github.jhejderup;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Set;

public class DependencyTransformer implements ClassFileTransformer {

  private final Set<String> dependencies;

  public DependencyTransformer(Set<String> dependencies) {
    this.dependencies = dependencies;

  }

  @Override
  public byte[] transform(ClassLoader loader, String className,
      Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
      byte[] classfileBuffer) {

    if (this.dependencies.contains(className)) {
      ClassReader reader = new ClassReader(classfileBuffer);
      ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);
      InstrumentMethodBodies visitor = new InstrumentMethodBodies(writer,
          className);
      reader.accept(visitor, ClassReader.EXPAND_FRAMES);
      return writer.toByteArray();
    }
    return null;
  }

}

