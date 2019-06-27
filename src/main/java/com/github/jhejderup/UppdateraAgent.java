package com.github.jhejderup;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class UppdateraAgent {

    public static void premain(String agentOps, Instrumentation inst) {
        System.out.println("Starting the agent");
        Transformer transformer = new Transformer();
        inst.addTransformer(transformer);
    }


}
