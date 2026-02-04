package fr.kent1c38.yggdrasil.engine.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import net.minestom.server.command.builder.arguments.number.ArgumentFloat;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.entity.EntityFinder;

public class TPCommand extends Command {

    public TPCommand() {
        super("tp");

        ArgumentEntity targetArg = new ArgumentEntity("target").onlyPlayers(true).singleEntity(true);
        ArgumentFloat xArg = new ArgumentFloat("x");
        ArgumentFloat yArg = new ArgumentFloat("y");
        ArgumentFloat zArg = new ArgumentFloat("z");

        addSyntax((sender, context) -> {

            EntityFinder finder = context.get(targetArg);
            Player target = finder.findFirstPlayer(sender);
            float x = context.get(xArg);
            float y = context.get(yArg);
            float z = context.get(zArg);

            target.teleport(new Pos(x, y, z));

            sender.sendMessage(Component.text("Successfully teleported %s to pos: x=%f y=%f z=%f"
                    .formatted(LegacyComponentSerializer.legacySection().serialize(target.getName()),
                            x, y, z)));
        }, targetArg, xArg, yArg, zArg);
    }
}
