package fr.kent1c38.yggdrasil.engine.kernel;

import fr.kent1c38.yggdrasil.api.module.ModuleContext;
import fr.kent1c38.yggdrasil.api.module.Module;

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
    private final YggdrasilServer server;

    public ModuleLoader(File modulesDirectory, ModuleContext ctx, YggdrasilServer server) {
        this.modulesDirectory = modulesDirectory;
        this.ctx = ctx;
        this.server = server;
    }

    public List<LoadedModule> loadAll() {
        List<LoadedModule> loadedModules = new ArrayList<>();
        if (!modulesDirectory.exists()) modulesDirectory.mkdirs();
        File[] files = modulesDirectory.listFiles((_, name) -> name.endsWith(".jar"));
        if (files == null) return loadedModules;
        for (File jar : files) {
            try {
                LoadedModule lm = loadJar(jar);
                if (lm != null) loadedModules.add(lm);
            } catch (Exception e) {
                ctx.getLogger().warn("Error while loading module {}: {}", jar.getName(), e.getMessage());
            }
        }
        return loadedModules;
    }

    public LoadedModule loadJar(File jarFile) throws Exception {
        try (JarFile jf = new JarFile(jarFile)) {
            JarEntry entry = jf.getJarEntry("plugin.properties");
            if (entry == null) {
                ctx.getLogger().warn("No plugin.properties found in {}, skipping.", jarFile.getName());
                return null;
            }
            try (InputStream is = jf.getInputStream(entry)) {
                Properties p = new Properties();
                p.load(is);
                String mainClass = p.getProperty("main");
                if (mainClass == null) {
                    ctx.getLogger().warn("plugin.properties does not contain a 'main-class' property in {}, skipping.", jarFile.getName());
                    return null;
                }

                URL url = jarFile.toURI().toURL();
                URLClassLoader cl = new URLClassLoader(new URL[]{url}, this.getClass().getClassLoader());
                Class<?> clazz = Class.forName(mainClass, true, cl);
                Object inst = clazz.getDeclaredConstructor().newInstance();
                if (!(inst instanceof fr.kent1c38.yggdrasil.api.module.Module module)) {
                    ctx.getLogger().warn("Class {} does not extends from Module class, skipping...", mainClass);
                    return null;
                }
                module.onEnable(ctx);
                ctx.getLogger().info("Successfully loaded module: {}", jarFile.getName());
                return new LoadedModule(module, cl, jarFile.getName());
            }
        }
    }

    public void unloadModule(LoadedModule loadedModule) {
        try {
            loadedModule.module.onDisable(ctx);
        } catch (Exception e) {
            ctx.getLogger().warn("Error while disabling module {}: {}", loadedModule.jarName, e.getMessage());
        }

        try {
            if (loadedModule.classLoader instanceof URLClassLoader cl) {
                cl.close();
            }
        } catch (Exception e) {
            ctx.getLogger().warn("Error while closing classloader for {}", loadedModule.jarName);
        }
    }

    public void unloadAll() {
        for (LoadedModule lm : server.getModules()) {
            unloadModule(lm);
        }
    }

    public record LoadedModule(Module module, ClassLoader classLoader, String jarName) {}

}
