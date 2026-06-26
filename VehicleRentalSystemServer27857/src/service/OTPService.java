package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.OTPVerification;

public interface OTPService extends Remote {

    String saveOTP(OTPVerification otp) throws RemoteException;

    String updateOTP(OTPVerification otp) throws RemoteException;

    OTPVerification searchOTPById(int id) throws RemoteException;

    List<OTPVerification> displayAllOTPs() throws RemoteException;
}