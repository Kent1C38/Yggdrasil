package fr.kent1c38.aurastomcore.kernel;

public interface Module {
    void onEnable(ModuleContext ctx) throws Exception;

    void onDisable() throws Exception;

    default String getName() { return this.getClass().getSimpleName(); }
}
