package service.implementation;

import dao.VehicleDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import model.Vehicle;
import service.VehicleService;

public class VehicleServiceImpl extends UnicastRemoteObject implements VehicleService {

    VehicleDao dao = new VehicleDao();

    public VehicleServiceImpl() throws RemoteException {
    }

    @Override
    public String registerVehicle(Vehicle vehicle) throws RemoteException {
        return dao.registerVehicle(vehicle);
    }

    @Override
    public String updateVehicle(Vehicle vehicle) throws RemoteException {
        return dao.updateVehicle(vehicle);
    }

    @Override
    public String deleteVehicle(Vehicle vehicle) throws RemoteException {
        return dao.deleteVehicle(vehicle);
    }

    @Override
    public Vehicle searchVehicleById(int id) throws RemoteException {
        return dao.searchVehicleById(id);
    }

    @Override
    public List<Vehicle> displayAllVehicles() throws RemoteException {
        return dao.displayAllVehicles();
    }
}