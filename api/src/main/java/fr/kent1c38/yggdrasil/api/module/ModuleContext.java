package fr.kent1c38.yggdrasil.api.module;

import fr.kent1c38.yggdrasil.api.server.ServerProperties;

import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;

public interface ModuleContext {
    void info(String fmt, Object... args);
    void warn(String fmt, Object... args);

    <T> void registerEvent(Class<T> eventClass, Consumer<T> listener);

    ScheduledFuture<?> schedule(Runnable task, long delay, long repeat);

    ServerProperties getProperties();
}
