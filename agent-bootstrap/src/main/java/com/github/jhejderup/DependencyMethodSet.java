package com.github.jhejderup;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DependencyMethodSet {

  static HashSet<String> methods     = new HashSet<>();
  static Set<String>     testClasses = new HashSet<>();
  static Set<String>     depClasses  = new HashSet<>();
  static FileWriter      fw;

  static {
    try {
      testClasses = Files.walk(Paths.get("target/test-classes"))
          .filter(p -> p.toString().endsWith(".class")).map(
              p -> p.toString().replace(".class", "")
                  .replace("target/test-classes/", ""))
          .collect(HashSet::new, (map, v) -> map.add(v), HashSet::addAll);

      String dataPath = System.getProperty("user.dir")
          .replace("/root", "/data");

      depClasses = Files.lines(Paths.get(dataPath, "callgraph", "cha.txt"))
          .collect(Collectors.toSet());

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  static {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        synchronized (methods) {
          try {
            if (methods.size() > 0) {
              String dataPath = System.getProperty("user.dir")
                  .replace("/root", "/data");
              File log = new File(dataPath + "/callgraph/gen-dyn-cg.txt");
              fw = new FileWriter(log);
              for (String method : methods) {
                fw.write(method);
              }
              fw.close();
            } else {
              String dataPath = System.getProperty("user.dir")
                  .replace("/root", "/data");
              File log = new File(dataPath + "/callgraph/gen-dyn-cg.txt");
              fw = new FileWriter(log);
              fw.write("");
              fw.close();
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    });
  }

  public static void push(String fnname) {
    StackTraceElement[] stackTraceElements = Thread.currentThread()
        .getStackTrace();
    boolean isTestClass = false;

    for (int i = 1; i < stackTraceElements.length; i++) {

      int ahead = i + 1;

      if (ahead == stackTraceElements.length)
        break;
      ///     stack[i]            stack[i+1]        stack[i+2]
      //InstrumenterClass <- DependencyClass <- CallerClass
      String[] segments = stackTraceElements[i].getClassName().replace(".", "/")
          .split("/");
      String pkgName = String
          .join("/", Arrays.copyOf(segments, segments.length - 1));
      String classCurr = stackTraceElements[ahead].getClassName()
          .replace(".", "/");

      if (depClasses.contains(pkgName) && testClasses.contains(classCurr)) {
        isTestClass = true;
        break;
      }
    }

    if (!isTestClass) {
      synchronized (methods) {
        methods.add(fnname);
      }
    }
  }
}
