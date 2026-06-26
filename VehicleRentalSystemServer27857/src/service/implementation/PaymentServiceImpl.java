package service.implementation;

import dao.PaymentDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import model.Payment;
import service.PaymentService;

public class PaymentServiceImpl extends UnicastRemoteObject implements PaymentService {

    PaymentDao dao = new PaymentDao();

    public PaymentServiceImpl() throws RemoteException {
    }

    @Override
    public String registerPayment(Payment payment) throws RemoteException {
        return dao.registerPayment(payment);
    }

    @Override
    public String updatePayment(Payment payment) throws RemoteException {
        return dao.updatePayment(payment);
    }

    @Override
    public String deletePayment(Payment payment) throws RemoteException {
        return dao.deletePayment(payment);
    }

    @Override
    public Payment searchPaymentById(int id) throws RemoteException {
        return dao.searchPaymentById(id);
    }

    @Override
    public List<Payment> displayAllPayments() throws RemoteException {
        return dao.displayAllPayments();
    }
}