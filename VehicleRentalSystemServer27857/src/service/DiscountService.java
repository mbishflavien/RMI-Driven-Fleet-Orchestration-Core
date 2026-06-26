package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.Discount;

public interface DiscountService extends Remote {

    String registerDiscount(Discount discount) throws RemoteException;

    String updateDiscount(Discount discount) throws RemoteException;

    String deleteDiscount(Discount discount) throws RemoteException;

    Discount searchDiscountById(int id) throws RemoteException;

    List<Discount> displayAllDiscounts() throws RemoteException;
}