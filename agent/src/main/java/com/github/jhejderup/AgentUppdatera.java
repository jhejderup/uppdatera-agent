package com.github.jhejderup;

import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

public class AgentUppdatera {

  public static void premain(String agentOps, Instrumentation inst) {

    try {

      String dataPath = System.getProperty("user.dir")
          .replace("/root", "/data");

      Set<String> packages = Files
          .lines(Paths.get(dataPath, "callgraph", "cha.txt"))
          .collect(Collectors.toSet());

      if (packages.size() > 0) {
        DependencyTransformer transformer = new DependencyTransformer(packages);
        inst.addTransformer(transformer, false);
      }
    } catch (Exception e) {
      System.out.println("Error!");
      e.printStackTrace();
    }
  }

}
