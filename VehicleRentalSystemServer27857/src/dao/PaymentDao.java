package dao;

import java.util.List;
import model.Payment;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Query;

public class PaymentDao {

    public String registerPayment(Payment payment) {

        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();

            ss.save(payment);

            tr.commit();
            ss.close();

            return "Payment registered successfully";

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "Payment registration failed";
    }

    public String updatePayment(Payment payment) {

        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();

            ss.update(payment);

            tr.commit();
            ss.close();

            return "Payment updated successfully";

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "Payment update failed";
    }

    public String deletePayment(Payment payment) {

        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();

            ss.delete(payment);

            tr.commit();
            ss.close();

            return "Payment deleted successfully";

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "Payment deletion failed";
    }

    public Payment searchPaymentById(int id) {

        Session ss = HibernateUtil.getSessionFactory().openSession();
        Payment payment = (Payment) ss.get(Payment.class, id);
        ss.close();

        return payment;
    }

    public List<Payment> displayAllPayments() {

        Session ss = HibernateUtil.getSessionFactory().openSession();

        Query query = ss.createQuery("from Payment");
        List<Payment> list = query.list();

        ss.close();

        return list;
    }
}