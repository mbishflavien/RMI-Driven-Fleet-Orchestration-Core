package dao;

import java.util.List;
import model.User;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class UserDao {

    public String registerUser(User user) {

        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();

            ss.save(user);

            tr.commit();
            ss.close();

            return "User registered successfully";

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "User registration failed";
    }

    public String updateUser(User user) {

        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();

            ss.update(user);

            tr.commit();
            ss.close();

            return "User updated successfully";

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "User update failed";
    }

    public String deleteUser(User user) {

        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();

            ss.delete(user);

            tr.commit();
            ss.close();

            return "User deleted successfully";

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "User deletion failed";
    }

    public User searchUserById(int id) {

        Session ss = HibernateUtil.getSessionFactory().openSession();
        User user = (User) ss.get(User.class, id);
        ss.close();

        return user;
    }
    
    public List<User> displayAllUsers() {

        Session ss = HibernateUtil.getSessionFactory().openSession();

        Query query = ss.createQuery("from User");
        List<User> list = query.list();

        ss.close();

        return list;
    }
    
}