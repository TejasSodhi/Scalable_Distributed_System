package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Coordinator extends Remote {

    String preparePut(String key, String value) throws RemoteException;

    String prepareDelete(String key) throws RemoteException;

    //String prepareDeleteAll() throws RemoteException;

    void commitPut(String key, String value) throws RemoteException;

    void commitDelete(String key) throws RemoteException;

    void abort(String key, String value) throws RemoteException;
}
