package dao;

import java.util.List;
import model.OTPVerification;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Query;

public class OTPVerificationDao {

    public String saveOTP(OTPVerification otp) {

        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();

            ss.save(otp);

            tr.commit();
            ss.close();

            return "OTP saved successfully";

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "OTP save failed";
    }

    public String updateOTP(OTPVerification otp) {

        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();

            ss.update(otp);

            tr.commit();
            ss.close();

            return "OTP updated successfully";

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "OTP update failed";
    }

    public OTPVerification searchOTPById(int id) {

        Session ss = HibernateUtil.getSessionFactory().openSession();
        OTPVerification otp = (OTPVerification) ss.get(OTPVerification.class, id);
        ss.close();

        return otp;
    }

    public List<OTPVerification> displayAllOTPs() {

        Session ss = HibernateUtil.getSessionFactory().openSession();

        Query query = ss.createQuery("from OTPVerification");
        List<OTPVerification> list = query.list();

        ss.close();

        return list;
    }
}