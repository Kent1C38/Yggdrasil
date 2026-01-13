package fr.kent1c38.yggdrasil;

import fr.kent1c38.yggdrasil.engine.kernel.YggdrasilServer;

import java.io.File;

public class Yggdrasil {

     public static void main(String[] args) throws Exception {
        File moduleDir = new File("modules");
        new YggdrasilServer(moduleDir).start();
    }

}
