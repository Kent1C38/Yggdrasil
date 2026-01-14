package fr.kent1c38.yggdrasil.engine.commands;

import fr.kent1c38.yggdrasil.engine.kernel.YggdrasilServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;

public class ListCommand extends Command {
	public ListCommand(YggdrasilServer server) {
		super("list");

		setDefaultExecutor((sender, _) -> {
			int playerNumber = server.getOnlinePlayers().size();
			Component message = Component.text(String.format("Currently online players: %d", playerNumber));
			if (playerNumber > 0) message = message.appendNewline();
			for (Player p : server.getOnlinePlayers()) {
				message = message.append(Component.text(String.format("%s, ", LegacyComponentSerializer.legacySection().serialize(p.getName()))));
			}
			sender.sendMessage(message);
		});
	}
}
