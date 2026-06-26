package controller;

import java.rmi.registry.Registry;

public class TestConnection {

    public static void main(String[] args) {

        try {

            Registry registry = ClientRegistry.getRegistry();

            System.out.println("Client connected successfully.");

            String[] services = registry.list();

            for (String service : services) {
                System.out.println(service);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}