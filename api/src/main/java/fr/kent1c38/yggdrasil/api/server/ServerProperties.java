package fr.kent1c38.yggdrasil.api.server;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ServerProperties {
    private final int serverPort;
    private final int maxPlayers;

    public ServerProperties() throws IOException {
        String PROP_FILE = "server.properties";
        File file = new File(PROP_FILE);
        if (!file.exists()) {
            file.createNewFile();
            try (PrintWriter writer = new PrintWriter(PROP_FILE, StandardCharsets.UTF_8)) {
                writer.println("server-port=25565");
                writer.println("max-players=20");
            }
        }

        FileReader reader = new FileReader(PROP_FILE);
        Properties properties = new Properties();
        properties.load(reader);
        this.serverPort = Integer.parseInt(properties.getProperty("server-port"));
        this.maxPlayers = Integer.parseInt(properties.getProperty("max-players"));
    }

    public int getServerPort() {
        return serverPort;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }
}
