package fr.kent1c38.aurastomcore;

import fr.kent1c38.aurastomcore.kernel.Kernel;

import java.io.File;

public class AuraStomCore {

    private static Kernel server;

    void main(String[] args) throws Exception {
        String modulePath = "modules";
        int port;
        if (args.length < 1) {
            port = 25565;
        } else {
            port = Integer.parseInt(args[0]);
        }
        File moduleDir = new File(modulePath);
        server = new Kernel(moduleDir);
        server.start(port);
    }

    public static Kernel getServer() {
        return server;
    }
}
