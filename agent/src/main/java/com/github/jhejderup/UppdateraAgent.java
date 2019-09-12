package com.github.jhejderup;

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.instrument.Instrumentation;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class UppdateraAgent {


    public static void save(Set<String> obj, String path) throws Exception {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(
                    new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8));
            for (String s : obj) {
                pw.println(s);
            }
            pw.flush();
        } finally {
            pw.close();
        }
    }

    public static void premain(String agentOps, Instrumentation inst) {

        try {

            HashSet<String> testClasses = Files.walk(Paths.get("target/test-classes"))
                    .filter(p -> p.toString().endsWith(".class"))
                    .map(p -> p.toString().replace(".class", "").replace("target/test-classes/", ""))
                    .collect(HashSet::new, (map, v) -> map.add(v), HashSet::addAll);

            save(testClasses, "tclass.uppdatera");

        } catch (Exception e) {
            System.out.println("No test files found");
        }

        try {
            File[] depz = Maven.configureResolver()
                    .loadPomFromFile("pom.xml")
                    .importDependencies(
                            ScopeType.COMPILE,
                            ScopeType.IMPORT,
                            ScopeType.PROVIDED
                    ).resolve()
                    .withTransitivity()
                    .asFile();

            HashSet packages = new HashSet<String>();

            for (int i = 0; i < depz.length; i++) {
                try (ZipFile archive = new ZipFile(depz[i])) {
                    List<? extends ZipEntry> entries = archive.stream()
                            .sorted(Comparator.comparing(ZipEntry::getName))
                            .collect(Collectors.toList());
                    for (ZipEntry entry : entries) {
                        try {
                            if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                                Path path = Paths.get(entry.getName());
                                String dir = path.getParent().toString();
                                packages.add(dir);
                            }
                        } catch (Exception e) {
                            continue;
                        }
                    }
                } catch (Exception e) {
                    continue;
                }
            }

            if (packages.size() > 0) {
                save(packages, "dclass.uppdatera");
                DependencyTransformer transformer = new DependencyTransformer(packages);
                inst.addTransformer(transformer, false);
            }
        } catch (Exception e) {
            System.out.println("Error!");
            e.printStackTrace();
        }
    }


}
