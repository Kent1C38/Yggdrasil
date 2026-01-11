package fr.kent1c38.yggdrasil.engine.console;

import fr.kent1c38.yggdrasil.engine.kernel.YggdrasilServer;
import net.minestom.server.MinecraftServer;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

public class Console {

    private final YggdrasilServer server;

    public Console(YggdrasilServer server) {
        this.server = server;
    }

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
                     if (!MinecraftServer.getCommandManager().commandExists(line.split(" ")[0]))
                        server.info("Unknown command: " + line);
                     else {
                         server.info("Executing command /" + line);
                         MinecraftServer.getCommandManager().execute(sender, line);
                     }
                 }
             } catch (UserInterruptException | EndOfFileException e) {
                 running = false;
             } catch (Exception e) {
                 server.severe(e.getMessage());
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
