package fr.kent1c38.aurastomcore;

import fr.kent1c38.aurastomcore.kernel.Kernel;

import java.io.File;

public class AuraStomCore {
    void main(String[] args) throws Exception {
        String modulePath = "modules";
        int port;
        if (args.length < 1) {
            port = 25565;
        } else {
            port = Integer.parseInt(args[0]);
        }
        File moduleDir = new File(modulePath);
        new Kernel(moduleDir).start(port);
    }
}
