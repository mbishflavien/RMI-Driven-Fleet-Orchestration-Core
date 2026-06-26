package service.implementation;

import dao.OTPVerificationDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import model.OTPVerification;
import service.OTPService;
import util.EmailUtil;

public class OTPServiceImpl extends UnicastRemoteObject implements OTPService {

    OTPVerificationDao dao = new OTPVerificationDao();

    public OTPServiceImpl() throws RemoteException {
    }

    @Override
    public String saveOTP(OTPVerification otp) throws RemoteException {
        String result = dao.saveOTP(otp);

        // Send OTP email after saving successfully
        if (result.contains("successfully")) {
            try {
                EmailUtil.sendOTP(otp.getEmail(), otp.getOtpCode());
            } catch (Exception ex) {
                System.err.println("[OTPServiceImpl] Failed to send OTP email to "
                        + otp.getEmail() + ": " + ex.getMessage());
                // Don't fail the whole operation — OTP is still saved in DB
            }
        }

        return result;
    }

    @Override
    public String updateOTP(OTPVerification otp) throws RemoteException {
        return dao.updateOTP(otp);
    }

    @Override
    public OTPVerification searchOTPById(int id) throws RemoteException {
        return dao.searchOTPById(id);
    }

    @Override
    public List<OTPVerification> displayAllOTPs() throws RemoteException {
        return dao.displayAllOTPs();
    }
}
