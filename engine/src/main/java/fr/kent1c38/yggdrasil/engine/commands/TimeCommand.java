package fr.kent1c38.yggdrasil.engine.commands;

import fr.kent1c38.yggdrasil.engine.kernel.YggdrasilServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.number.ArgumentLong;

public class TimeCommand extends Command {

    public enum TimeOperations {
        SET,
        ADD,
        QUERY,
    }

    public enum TimeNames {
        DAY,
        NIGHT,
        NOON,
        MIDNIGHT,
    }

    public TimeCommand(YggdrasilServer server) {
        super("time");

        ArgumentEnum<TimeOperations> operationArg = new ArgumentEnum<>("operation", TimeOperations.class);
        operationArg.setFormat(ArgumentEnum.Format.LOWER_CASED);
        ArgumentEnum<TimeNames> timeNamesArg = new ArgumentEnum<>("timeName", TimeNames.class);
        timeNamesArg.setFormat(ArgumentEnum.Format.LOWER_CASED);
        ArgumentLong valueArg = new ArgumentLong("value");

        addSyntax((sender, context) -> {
            TimeOperations op = context.get(operationArg);
            switch (op) {
                case ADD, SET -> sender.sendMessage(Component.text("Invalid command", NamedTextColor.RED)
                        .appendNewline()
                        .append(Component.text("Usage: /time %s <value>".formatted(op.name().toLowerCase()), NamedTextColor.AQUA)));
                case QUERY -> sender.sendMessage(Component.text("Current time: %d".formatted(server.getInstance().getTime())));
            }
        }, operationArg);

        addSyntax((sender, context) -> {
            TimeOperations op = context.get(operationArg);
            long value = context.get(valueArg);

            switch (op) {
                case ADD -> {
                    server.getInstance().setTime(server.getInstance().getTime() + value);
                    sender.sendMessage("Time set to: %d".formatted(server.getInstance().getTime()));
                }
                case SET -> {
                    server.getInstance().setTime(value);
                    sender.sendMessage("Time set to: %d".formatted(server.getInstance().getTime()));
                }
                case QUERY -> sender.sendMessage("'/time query' doesn't take other arguments!");
            }
        }, operationArg, valueArg);

        addSyntax((sender, context) -> {
            TimeOperations op = context.get(operationArg);
            TimeNames name = context.get(timeNamesArg);
            switch (op) {
                case SET -> {
                    switch (name) {
                        case DAY -> server.getInstance().setTime(1000);
                        case NIGHT -> server.getInstance().setTime(13000);
                        case NOON -> server.getInstance().setTime(6000);
                        case MIDNIGHT -> server.getInstance().setTime(18000);
                    }
                    sender.sendMessage("Time set to: %d".formatted(server.getInstance().getTime()));
                }
                case QUERY, ADD -> sender.sendMessage("Invalid arguments");
            }
        }, operationArg, timeNamesArg);

        setDefaultExecutor((sender, _) -> sender.sendMessage(Component.text("Invalid arguments")));
    }
}
