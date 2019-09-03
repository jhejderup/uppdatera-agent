package com.github.jhejderup;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

public class DependencyMethodSet {

    static HashSet<String> methods = new HashSet<>();
    static FileWriter fw;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    if(methods.size() > 0) {
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
        });
    }

    public static void push(String fnname) {
        ///String callerName = Thread.currentThread().getStackTrace()[2].getClassName();
        //Read from a file the testclasses
        methods.add(fnname);

    }
}
