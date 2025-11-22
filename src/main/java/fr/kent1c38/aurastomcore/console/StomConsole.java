package fr.kent1c38.aurastomcore.console;

import fr.kent1c38.aurastomcore.AuraStomCore;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.CommandResult;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

public class StomConsole {

    private Thread inputThread;
    private Terminal terminal;
    private LineReader reader;
    private volatile boolean running = false;

    public void start() throws IOException {
        if (running) return;
        running = true;

        terminal = TerminalBuilder.builder()
                .system(true)
                .jna(true)
                .build();

        reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .build();

        inputThread = new Thread(this::readLoop, "Console-Input");
        inputThread.setDaemon(true);
        inputThread.start();
    }

    private void readLoop() {
        var sender = MinecraftServer.getCommandManager().getConsoleSender();
         while (running) {
             try {
                 String line = reader.readLine("");
                 if (!line.isBlank()) {
                     CommandResult result = MinecraftServer.getCommandManager().execute(sender, line);
                     switch (result.getType()) {
                         case SUCCESS -> AuraStomCore.getServer().info("Executing command /" + line);
                         case UNKNOWN -> AuraStomCore.getServer().info("Unknown command: " + line);
                         case INVALID_SYNTAX -> AuraStomCore.getServer().info("Syntax error for command: " + line);
                         default -> AuraStomCore.getServer().info("Entry: " + line);
                     }
                 }
             } catch (UserInterruptException | EndOfFileException e) {
                 running = false;
             } catch (Exception e) {
                 AuraStomCore.getServer().severe(e.getMessage());
             }
         }
    }

    public void stop() {
        running = false;
        try {
            terminal.close();
        } catch (IOException e) {
            inputThread.interrupt();
        }
    }
}
