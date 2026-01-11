package fr.kent1c38.yggdrasil.engine.commands;

import fr.kent1c38.yggdrasil.engine.kernel.YggdrasilServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;

public class StopCommand extends Command {

    public StopCommand(YggdrasilServer server) {
        super("stop", "shutdown");
        setDefaultExecutor((commandSender, commandContext) -> {
            commandSender.sendMessage(Component.text("Stopping server...", NamedTextColor.RED));
            server.stop();
        });
    }
}
