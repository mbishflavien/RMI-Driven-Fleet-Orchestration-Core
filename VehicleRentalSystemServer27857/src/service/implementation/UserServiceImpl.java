package service.implementation;

import dao.UserDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import model.User;
import service.UserService;

public class UserServiceImpl extends UnicastRemoteObject implements UserService {

    UserDao dao = new UserDao();

    public UserServiceImpl() throws RemoteException {
    }

    @Override
    public String registerUser(User user) throws RemoteException {
        return dao.registerUser(user);
    }

    @Override
    public String updateUser(User user) throws RemoteException {
        return dao.updateUser(user);
    }

    @Override
    public String deleteUser(User user) throws RemoteException {
        return dao.deleteUser(user);
    }

    @Override
    public User searchUserById(int id) throws RemoteException {
        return dao.searchUserById(id);
    }

    @Override
    public List<User> displayAllUsers() throws RemoteException {
        return dao.displayAllUsers();
    }
}