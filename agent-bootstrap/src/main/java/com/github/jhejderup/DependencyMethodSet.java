package com.github.jhejderup;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DependencyMethodSet {

    static HashSet<String> methods = new HashSet<>();
    static FileWriter fw;
    static Set<String> testClasses = null;
    static Set<String> depClasses = null;

    static {

        try {
            depClasses = Files.lines(Paths.get("dclass.uppdatera"))
                    .collect(HashSet::new, (map, v) -> map.add(v), HashSet::addAll);

        } catch (Exception e) {
            System.out.println("DepClasses: could not find!");
        }

        try {
            testClasses = Files.lines(Paths.get("tclass.uppdatera"))
                    .collect(HashSet::new, (map, v) -> map.add(v), HashSet::addAll);
        } catch (Exception e) {
            System.out.println("TestClasses: could not find!");
        }

    }


    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                synchronized (methods) {
                    try {
                        if (methods.size() > 0) {
                            File log = new File("functions.txt");
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

        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        boolean isTestClass = false;

        for (int i = 1; i < stackTraceElements.length; i++) {

            int ahead = i + 1;

            if (ahead == stackTraceElements.length)
                break;
            ///     stack[0]            stack[1]        stack[2]
            //InstrumenterClass <- DependencyClass <- CallerClass
            String[] segments = stackTraceElements[i].getClassName().replace(".", "/").split("/");
            String pkgName = String.join("/", Arrays.copyOf(segments, segments.length - 1));
            String classCurr = stackTraceElements[ahead].getClassName().replace(".", "/");

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
