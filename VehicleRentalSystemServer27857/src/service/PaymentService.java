package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.Payment;

public interface PaymentService extends Remote {

    String registerPayment(Payment payment) throws RemoteException;

    String updatePayment(Payment payment) throws RemoteException;

    String deletePayment(Payment payment) throws RemoteException;

    Payment searchPaymentById(int id) throws RemoteException;

    List<Payment> displayAllPayments() throws RemoteException;
}