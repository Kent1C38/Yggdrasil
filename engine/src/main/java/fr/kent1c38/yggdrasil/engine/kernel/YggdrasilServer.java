package fr.kent1c38.yggdrasil.engine.kernel;

import fr.kent1c38.yggdrasil.api.module.ModuleContext;
import fr.kent1c38.yggdrasil.api.server.ServerProperties;
import fr.kent1c38.yggdrasil.engine.commands.*;
import fr.kent1c38.yggdrasil.engine.console.Console;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerCommandEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class YggdrasilServer {
    private final Logger LOGGER = LoggerFactory.getLogger("Yggdrasil");

    private final ServerProperties serverProperties;
    private final Console console = new Console(this);

    private final ModuleLoader loader;
    private final List<ModuleLoader.LoadedModule> modules = new ArrayList<>();

    private InstanceContainer instance;

    private Task heartbeat;

    private GlobalEventHandler globalEventHandler;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4, r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    private final HashSet<Player> onlinePlayers = new HashSet<>();

    public YggdrasilServer() throws IOException {
		SimpleModuleContext ctx = new SimpleModuleContext();
        this.serverProperties = new ServerProperties();
		this.loader = new ModuleLoader(new File("modules"), ctx, this);
    }

    public void start() throws Exception {
        //Minecraft Server Initialization
        MinecraftServer server = MinecraftServer.init();

        //Start Console Thread
        console.start();

        //Load Modules
        modules.addAll(loader.loadAll());
        LOGGER.info("{} loaded modules.", modules.size());

        //Commands
        registerCommand(new StopCommand(this));
        registerCommand(new GamemodeCommand());
        registerCommand(new ListCommand(this));
        registerCommand(new TimeCommand(this));
        registerCommand(new TPCommand());

        //Instance Init
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        instance = instanceManager.createInstanceContainer();

        //World Init
        instance.setChunkLoader(new AnvilLoader("worlds/world"));

        //Listeners
        globalEventHandler = MinecraftServer.getGlobalEventHandler();

        //Player Pre Login
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();
            onlinePlayers.add(player);
            event.setSpawningInstance(instance);
            player.setRespawnPoint(new Pos(0, 0, 0));
        });

        //Player Disconnect
        globalEventHandler.addListener(PlayerDisconnectEvent.class, event -> {
            final Player player = event.getPlayer();
            onlinePlayers.remove(player);
        });

        //Player Command Event
        globalEventHandler.addListener(PlayerCommandEvent.class, event -> {
            final Player player = event.getPlayer();
            LOGGER.info("Player {} executed the command: {}", LegacyComponentSerializer.legacySection().serialize(player.getName()), event.getCommand());
        });

        //Open Server
        server.start("localhost", serverProperties.getServerPort());

        heartbeat = MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            if (!console.isRunning())
                stop();
        }, TaskSchedule.duration(500, ChronoUnit.MILLIS), TaskSchedule.duration(500, ChronoUnit.MILLIS));
        MinecraftServer.getSchedulerManager().buildShutdownTask(MinecraftServer::stopCleanly);
    }

    public void stopServer() {
        loader.unloadAll();
        scheduler.shutdown();
        heartbeat.cancel();

        MinecraftServer.stopCleanly();
    }

    public void stop() {
        console.stop();
        stopServer();
    }

    private class SimpleModuleContext implements ModuleContext {


        @Override
        public Logger getLogger() {
            return LOGGER;
        }

        @Override
        public <T extends Event> void registerEvent(Class<T> eventClass, Consumer<T> listener) {
            globalEventHandler.addListener(eventClass, listener);
        }

        @Override
        public ScheduledFuture<?> schedule(Runnable task, long delay, long repeat) {
            long delayMs = ticksToMs(delay);
            long periodMs = repeat <= 0 ? 0 : ticksToMs(repeat);
            if (periodMs <= 0) return scheduler.schedule(task, delayMs, TimeUnit.MILLISECONDS);
            else return scheduler.scheduleAtFixedRate(task, delayMs, periodMs, TimeUnit.MILLISECONDS);
        }

        @Override
        public ServerProperties getProperties() {
            return serverProperties;
        }

        @Override
        public void registerCommand(Command command) {
            YggdrasilServer.this.registerCommand(command);
        }

    }

    public InstanceContainer getInstance() {
        return instance;
    }

    public List<ModuleLoader.LoadedModule> getModules() {
        return modules;
    }

    private long ticksToMs(long ticks) { return Math.max(0, ticks) * 50L; }

    private void registerCommand(Command command) {
        MinecraftServer.getCommandManager().register(command);
    }

    public HashSet<Player> getOnlinePlayers() {
        return onlinePlayers;
    }

    public Logger getLogger() {
        return LOGGER;
    }
}
