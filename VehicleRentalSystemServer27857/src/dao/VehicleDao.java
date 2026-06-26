package dao;

import java.util.List;
import model.Vehicle;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class VehicleDao {

    public String registerVehicle(Vehicle vehicle) {

        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();

            ss.save(vehicle);

            tr.commit();
            ss.close();

            return "Vehicle registered successfully";

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "Vehicle registration failed";
    }

    public String updateVehicle(Vehicle vehicle) {

        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();

            ss.update(vehicle);

            tr.commit();
            ss.close();

            return "Vehicle updated successfully";

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "Vehicle update failed";
    }

    public String deleteVehicle(Vehicle vehicle) {

        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();

            ss.delete(vehicle);

            tr.commit();
            ss.close();

            return "Vehicle deleted successfully";

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "Vehicle deletion failed";
    }

    public Vehicle searchVehicleById(int id) {

        Session ss = HibernateUtil.getSessionFactory().openSession();
        Vehicle vehicle = (Vehicle) ss.get(Vehicle.class, id);
        ss.close();

        return vehicle;
    }
    
    public List<Vehicle> displayAllVehicles() {

        Session ss = HibernateUtil.getSessionFactory().openSession();

        Query query = ss.createQuery("from Vehicle");
        List<Vehicle> list = query.list();

        ss.close();

        return list;
    }
}