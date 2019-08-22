package com.github.jhejderup;

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.JarURLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class UppdateraAgent {

    public static void premain(String agentOps, Instrumentation inst) {

        try {
            File file = new File(agentOps);
            JarFile jfagent = new JarFile(file);
            inst.appendToBootstrapClassLoaderSearch(jfagent);

            File[] depz = Maven.configureResolver()
                    .workOffline()
                    .loadPomFromFile("pom.xml")
                    .importDependencies(
                            ScopeType.COMPILE,
                            ScopeType.IMPORT,
                            ScopeType.PROVIDED
                    ).resolve()
                    .withTransitivity()
                    .asFile();

            for( File dep: depz){
                JarFile jf = new JarFile(dep);
                inst.appendToBootstrapClassLoaderSearch(jf);
            }

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
                Transformer transformer = new Transformer(packages);
                inst.addTransformer(transformer);
            }
        } catch (Exception e) {
            System.out.println("Error!");
            e.printStackTrace();
        }
    }


}
