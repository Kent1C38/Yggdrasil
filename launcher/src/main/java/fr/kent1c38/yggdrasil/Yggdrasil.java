package fr.kent1c38.yggdrasil;

import fr.kent1c38.yggdrasil.engine.kernel.YggdrasilServer;

public class Yggdrasil {

    public static void main(String[] args) throws Exception {
        YggdrasilServer server = new YggdrasilServer();
        server.start();
    }
}
