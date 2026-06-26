package controller;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import service.implementation.UserServiceImpl;
import service.implementation.VehicleServiceImpl;
import service.implementation.RentalServiceImpl;
import service.implementation.PaymentServiceImpl;
import service.implementation.DiscountServiceImpl;
import service.implementation.OTPServiceImpl;

public class Server {

    public static void main(String[] args) {

        try {

            Registry registry = LocateRegistry.createRegistry(5000);

            registry.rebind("user", new UserServiceImpl());
            registry.rebind("vehicle", new VehicleServiceImpl());
            registry.rebind("rental", new RentalServiceImpl());
            registry.rebind("payment", new PaymentServiceImpl());
            registry.rebind("discount", new DiscountServiceImpl());
            registry.rebind("otp", new OTPServiceImpl());

            System.out.println("Server is running on port 5000...");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}