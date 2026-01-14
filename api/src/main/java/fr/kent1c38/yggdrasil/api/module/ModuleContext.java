package fr.kent1c38.yggdrasil.api.module;

import fr.kent1c38.yggdrasil.api.server.ServerProperties;
import net.minestom.server.command.builder.Command;
import net.minestom.server.event.Event;

import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;

public interface ModuleContext {
    void info(String fmt, Object... args);
    void warn(String fmt, Object... args);
    void severe(String fmt, Object... args);

    <T extends Event> void registerEvent(Class<T> eventClass, Consumer<T> listener);

    ScheduledFuture<?> schedule(Runnable task, long delay, long repeat);

    ServerProperties getProperties();

    void registerCommand(Command command);
}
