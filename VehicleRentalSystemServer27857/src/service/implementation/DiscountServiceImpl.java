package service.implementation;

import dao.DiscountDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import model.Discount;
import service.DiscountService;

public class DiscountServiceImpl extends UnicastRemoteObject implements DiscountService {

    DiscountDao dao = new DiscountDao();

    public DiscountServiceImpl() throws RemoteException {
    }

    @Override
    public String registerDiscount(Discount discount) throws RemoteException {
        return dao.registerDiscount(discount);
    }

    @Override
    public String updateDiscount(Discount discount) throws RemoteException {
        return dao.updateDiscount(discount);
    }

    @Override
    public String deleteDiscount(Discount discount) throws RemoteException {
        return dao.deleteDiscount(discount);
    }

    @Override
    public Discount searchDiscountById(int id) throws RemoteException {
        return dao.searchDiscountById(id);
    }

    @Override
    public List<Discount> displayAllDiscounts() throws RemoteException {
        return dao.displayAllDiscounts();
    }
}