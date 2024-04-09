package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface KeyValueStoreService extends Remote {

    String put(String key, String value) throws RemoteException;

    String get(String key) throws RemoteException;

    String delete(String key) throws RemoteException;

    String getAll() throws RemoteException;

    String deleteAll() throws RemoteException;

    String preparePut(String key, String value) throws RemoteException;

    String prepareDelete(String key) throws RemoteException;

    void commitPut(String key, String value) throws RemoteException;

    void commitDelete(String key) throws RemoteException;
}
