package fr.kent1c38.yggdrasil;

import fr.kent1c38.yggdrasil.engine.kernel.YggdrasilServer;

import java.io.File;

public class Yggdrasil {

    void main(String[] args) throws Exception {
        String modulePath = "modules";
        int port;
        if (args.length < 1) {
            port = 25565;
        } else {
            port = Integer.parseInt(args[0]);
        }
        File moduleDir = new File(modulePath);
        new YggdrasilServer(moduleDir).start(port);
    }
}
