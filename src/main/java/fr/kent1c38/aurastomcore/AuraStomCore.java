package fr.kent1c38.aurastomcore;

import fr.kent1c38.aurastomcore.kernel.Kernel;

import java.io.File;

public class AuraStomCore {
    void main(String[] args) throws Exception {
        String modulePath;
        int port;
        if (args.length <= 1) {
            port = 25565;
            modulePath = "modules";
        } else if (args.length == 2) {
            port = Integer.getInteger(args[1]);
            modulePath = "modules";
        } else {
            port = Integer.getInteger(args[1]);
            modulePath = args[2];
        }
        File moduleDir = new File(modulePath);
        new Kernel(moduleDir).start(port);
    }
}
