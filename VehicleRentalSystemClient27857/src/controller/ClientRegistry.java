package controller;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientRegistry {

    private static Registry registry;

    public static Registry getRegistry() {

        try {

            registry = LocateRegistry.getRegistry("127.0.0.1", 5000);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return registry;
    }
}