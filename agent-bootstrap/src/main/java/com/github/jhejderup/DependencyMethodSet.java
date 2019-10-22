package com.github.jhejderup;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

public class DependencyMethodSet {

  static HashSet<String> methods = new HashSet<>();
  static FileWriter      fw;

  static {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        synchronized (methods) {
          try {
            if (methods.size() > 0) {
              String dataPath = System.getProperty("user.dir")
                  .replace("/root", "/data");
              File log = new File(dataPath + "/dyn-cg.txt");
              fw = new FileWriter(log);
              for (String method : methods) {
                fw.write(method);
              }
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
    synchronized (methods) {
      methods.add(fnname);
    }
  }
}
