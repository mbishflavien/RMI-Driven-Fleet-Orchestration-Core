package dao;

import java.util.List;
import model.Discount;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Query;

public class DiscountDao {

    public String registerDiscount(Discount discount) {

        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();

            ss.save(discount);

            tr.commit();
            ss.close();

            return "Discount registered successfully";

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "Discount registration failed";
    }

    public String updateDiscount(Discount discount) {

        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();

            ss.update(discount);

            tr.commit();
            ss.close();

            return "Discount updated successfully";

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "Discount update failed";
    }

    public String deleteDiscount(Discount discount) {

        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();

            ss.delete(discount);

            tr.commit();
            ss.close();

            return "Discount deleted successfully";

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "Discount deletion failed";
    }

    public Discount searchDiscountById(int id) {

        Session ss = HibernateUtil.getSessionFactory().openSession();
        Discount discount = (Discount) ss.get(Discount.class, id);
        ss.close();

        return discount;
    }

    public List<Discount> displayAllDiscounts() {

        Session ss = HibernateUtil.getSessionFactory().openSession();

        Query query = ss.createQuery("from Discount");
        List<Discount> list = query.list();

        ss.close();

        return list;
    }
}