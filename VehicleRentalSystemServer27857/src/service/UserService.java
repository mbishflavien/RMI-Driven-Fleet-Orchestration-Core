package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.User;

public interface UserService extends Remote {

    String registerUser(User user) throws RemoteException;

    String updateUser(User user) throws RemoteException;

    String deleteUser(User user) throws RemoteException;

    User searchUserById(int id) throws RemoteException;

    List<User> displayAllUsers() throws RemoteException;
}