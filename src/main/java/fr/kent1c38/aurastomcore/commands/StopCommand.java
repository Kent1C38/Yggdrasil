package fr.kent1c38.aurastomcore.commands;

import fr.kent1c38.aurastomcore.AuraStomCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;

public class StopCommand extends Command {

    public StopCommand() {
        super("stop", "shutdown");
        setDefaultExecutor((commandSender, commandContext) -> {
            commandSender.sendMessage(Component.text("Stopping server...", NamedTextColor.RED));
            AuraStomCore.getServer().stop();
        });
    }
}
