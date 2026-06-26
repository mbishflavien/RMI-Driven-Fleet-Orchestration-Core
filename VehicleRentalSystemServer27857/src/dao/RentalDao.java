package dao;

import java.util.List;
import model.Rental;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class RentalDao {

    public String registerRental(Rental rental) {

        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();

            ss.save(rental);

            tr.commit();
            ss.close();

            return "Rental registered successfully";

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "Rental registration failed";
    }

    public String updateRental(Rental rental) {

        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();

            ss.update(rental);

            tr.commit();
            ss.close();

            return "Rental updated successfully";

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "Rental update failed";
    }

    public String deleteRental(Rental rental) {

        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();

            ss.delete(rental);

            tr.commit();
            ss.close();

            return "Rental deleted successfully";

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "Rental deletion failed";
    }

    public Rental searchRentalById(int id) {

        Session ss = HibernateUtil.getSessionFactory().openSession();
        Rental rental = (Rental) ss.get(Rental.class, id);
        ss.close();

        return rental;
    }
    
    public List<Rental> displayAllRentals() {

        Session ss = HibernateUtil.getSessionFactory().openSession();

        Query query = ss.createQuery("from Rental");
        List<Rental> list = query.list();

        ss.close();

        return list;
    }
}