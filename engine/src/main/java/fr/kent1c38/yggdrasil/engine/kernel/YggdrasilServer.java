package fr.kent1c38.yggdrasil.engine.kernel;

import fr.kent1c38.yggdrasil.api.module.ModuleContext;
import fr.kent1c38.yggdrasil.engine.commands.StopCommand;
import fr.kent1c38.yggdrasil.engine.console.Console;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.anvil.AnvilLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class YggdrasilServer {
    private final Logger logger = Logger.getLogger("AuraStomKernel");
    private final Console console = new Console(this);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
	private final ModuleLoader loader;
    private final List<ModuleLoader.LoadedModule> modules = new ArrayList<>();

    public YggdrasilServer(File modulesDir) {
		SimpleModuleContext ctx = new SimpleModuleContext();
		this.loader = new ModuleLoader(modulesDir, ctx, this);
    }

    public void start(int port) throws Exception {
        MinecraftServer server = MinecraftServer.init();

        console.start();

        info("Starting kernel...");

        modules.addAll(loader.loadAll());
        info("%d loaded modules.", modules.size());

        MinecraftServer.getCommandManager().register(new StopCommand(this));

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();

        instanceContainer.setChunkLoader(new AnvilLoader("worlds/world"));

        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Pos(0, 0, 0));
        });

        server.start("localhost", port);
    }

    public void stop() {
        console.stop();
        MinecraftServer.getSchedulerManager().scheduleNextTick(() -> {
            loader.unloadAll();
            MinecraftServer.stopCleanly();
            System.exit(0);
        });
    }

    public void info(String fmt, Object... args) {logger.log(Level.INFO, String.format(fmt, args));}
    public void warn(String fmt, Object... args) {logger.log(Level.WARNING, String.format(fmt, args));}
    public void severe(String fmt, Object... args) {logger.log(Level.SEVERE, String.format(fmt, args));}

    private class SimpleModuleContext implements ModuleContext {

        @Override
        public void info(String fmt, Object... args) {
            YggdrasilServer.this.info(fmt, args);
        }

        @Override
        public void warn(String fmt, Object... args) {
            YggdrasilServer.this.warn(fmt, args);
        }

        @Override
        public <T> void registerEvent(Class<T> eventClass, Consumer<T> listener) {

        }

        @Override
        public ScheduledFuture<?> schedule(Runnable task, long delay, long repeat) {
            long delayMs = ticksToMs(delay);
            long periodMs = repeat <= 0 ? 0 : ticksToMs(repeat);
            if (periodMs <= 0) return scheduler.schedule(task, delayMs, TimeUnit.MILLISECONDS);
            else return scheduler.scheduleAtFixedRate(task, delayMs, periodMs, TimeUnit.MILLISECONDS);
        }

        @Override
        public String getProperty(String key) {
            return switch (key) {
                case "server.port" -> "25575";
                case "server.name" -> "MINESTOM";
                case "main-class" -> "main";
                default -> null;
            };
        }
    }

    public List<ModuleLoader.LoadedModule> getModules() {
        return modules;
    }

    private long ticksToMs(long ticks) { return Math.max(0, ticks) * 50L; }
}
