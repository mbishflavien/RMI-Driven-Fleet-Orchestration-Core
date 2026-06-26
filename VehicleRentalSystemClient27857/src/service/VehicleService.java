package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.Vehicle;

public interface VehicleService extends Remote {

    String registerVehicle(Vehicle vehicle) throws RemoteException;

    String updateVehicle(Vehicle vehicle) throws RemoteException;

    String deleteVehicle(Vehicle vehicle) throws RemoteException;

    Vehicle searchVehicleById(int id) throws RemoteException;

    List<Vehicle> displayAllVehicles() throws RemoteException;
}