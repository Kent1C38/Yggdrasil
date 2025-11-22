package fr.kent1c38.aurastomcore.kernel;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ModuleLoader {
    private final File modulesDirectory;
    private final ModuleContext ctx;

    public ModuleLoader(File modulesDirectory, ModuleContext ctx) {
        this.modulesDirectory = modulesDirectory;
        this.ctx = ctx;
    }

    public List<LoadedModule> loadAll() throws Exception {
        List<LoadedModule> loadedModules = new ArrayList<>();
        if (!modulesDirectory.exists()) modulesDirectory.mkdirs();
        File[] files = modulesDirectory.listFiles((dir, name) -> name.endsWith(".jar"));
        if (files == null) return loadedModules;
        for (File jar : files) {
            try {
                LoadedModule lm = loadJar(jar);
                if (lm != null) loadedModules.add(lm);
            } catch (Exception e) {
                ctx.warn("Error while loading module %s: %s", jar.getName(), e.getMessage());
            }
        }
        return loadedModules;
    }

    public LoadedModule loadJar(File jarFile) throws Exception {
        try (JarFile jf = new JarFile(jarFile)) {
            JarEntry entry = jf.getJarEntry("plugin.properties");
            if (entry == null) {
                ctx.warn("No plugin.properties found in %s, skipping.", jarFile.getName());
                return null;
            }
            try (InputStream is = jf.getInputStream(entry)) {
                Properties p = new Properties();
                p.load(is);
                String mainClass = p.getProperty("main");
                if (mainClass == null) {
                    ctx.warn("plugin.properties does not contain a 'main-class' property in %s, skipping.", jarFile.getName());
                    return null;
                }

                URL url = jarFile.toURI().toURL();
                URLClassLoader cl = new URLClassLoader(new URL[]{url}, this.getClass().getClassLoader());
                Class<?> clazz = Class.forName(mainClass, true, cl);
                Object inst = clazz.getDeclaredConstructor().newInstance();
                if (!(inst instanceof Module module)) {
                    ctx.warn("Class %s does not extends from Module class, skipping...", mainClass);
                    return null;
                }
                module.onEnable(ctx);
                ctx.info("Successfully loaded module: %s", jarFile.getName());
                return new LoadedModule(module, cl, jarFile.getName());
            }
        }
    }

    public static class LoadedModule {
        public final Module module;
        public final ClassLoader classLoader;
        public final String jarName;

        public LoadedModule(Module module, ClassLoader classLoader, String jarName) {
            this.module = module;
            this.classLoader = classLoader;
            this.jarName = jarName;
        }
    }

}
