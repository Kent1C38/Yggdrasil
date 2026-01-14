package fr.kent1c38.yggdrasil.engine.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.entity.EntityFinder;

public class GamemodeCommand extends Command {

	public GamemodeCommand() {
		super("gamemode", "gm");

		ArgumentWord modeArg = new ArgumentWord("mode");
		modeArg.setSuggestionCallback((_, _, suggestion) -> {
			suggestion.addEntry(new SuggestionEntry("survival"));
			suggestion.addEntry(new SuggestionEntry("creative"));
			suggestion.addEntry(new SuggestionEntry("adventure"));
			suggestion.addEntry(new SuggestionEntry("spectator"));
		});

		ArgumentEntity targetArg = new ArgumentEntity("target")
				.onlyPlayers(true)
				.singleEntity(true);

		// /gm <mode>
		addSyntax((sender, ctx) -> {
			Player player = (Player) sender;
			GameMode mode = parseGameMode(sender, ctx.get(modeArg));
			if (mode == null) return;
			player.setGameMode(mode);
		}, modeArg);

		// /gm <mode> <target>
		addSyntax((sender, ctx) -> {
			GameMode mode = parseGameMode(sender, ctx.get(modeArg));
			if (mode == null) return;
			EntityFinder finder = ctx.get(targetArg);
			Player target = finder.findFirstPlayer(sender);
			target.setGameMode(mode);
		}, modeArg, targetArg);
	}

	private GameMode parseGameMode(CommandSender sender, String input) {
		GameMode mode = switch (input.toLowerCase()) {
			case "survival", "s", "0" -> GameMode.SURVIVAL;
			case "creative", "c", "1" -> GameMode.CREATIVE;
			case "adventure", "a", "2" -> GameMode.ADVENTURE;
			case "spectator", "sp", "3" -> GameMode.SPECTATOR;
			default -> null;
		};

		if (mode == null) {
			sender.sendMessage(Component.text("Invalid Gamemode: " + input, NamedTextColor.RED));
		}
		return mode;
	}
}