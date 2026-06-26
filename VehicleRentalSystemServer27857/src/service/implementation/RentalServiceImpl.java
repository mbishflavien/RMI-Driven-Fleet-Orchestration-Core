package service.implementation;

import dao.RentalDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import model.Rental;
import service.RentalService;

public class RentalServiceImpl extends UnicastRemoteObject implements RentalService {

    RentalDao dao = new RentalDao();

    public RentalServiceImpl() throws RemoteException {
    }

    @Override
    public String registerRental(Rental rental) throws RemoteException {
        return dao.registerRental(rental);
    }

    @Override
    public String updateRental(Rental rental) throws RemoteException {
        return dao.updateRental(rental);
    }

    @Override
    public String deleteRental(Rental rental) throws RemoteException {
        return dao.deleteRental(rental);
    }

    @Override
    public Rental searchRentalById(int id) throws RemoteException {
        return dao.searchRentalById(id);
    }

    @Override
    public List<Rental> displayAllRentals() throws RemoteException {
        return dao.displayAllRentals();
    }
}