package fr.kent1c38.yggdrasil.api.module;

public interface Module {
    void onEnable(ModuleContext ctx) throws Exception;

    void onDisable(ModuleContext ctx) throws Exception;

    default String getName() { return this.getClass().getSimpleName(); }
}
