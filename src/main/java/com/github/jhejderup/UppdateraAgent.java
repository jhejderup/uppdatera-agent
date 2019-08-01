package com.github.jhejderup;

import java.lang.instrument.Instrumentation;

public class UppdateraAgent {

    public static void premain(String agentOps, Instrumentation inst) {
        System.out.println("Starting the agent");
        Transformer transformer = new Transformer();
        inst.addTransformer(transformer);
    }


}
