package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.Rental;

public interface RentalService extends Remote {

    String registerRental(Rental rental) throws RemoteException;

    String updateRental(Rental rental) throws RemoteException;

    String deleteRental(Rental rental) throws RemoteException;

    Rental searchRentalById(int id) throws RemoteException;

    List<Rental> displayAllRentals() throws RemoteException;
}