package server;

import java.util.HashMap;
import java.util.Map;

import java.rmi.Remote;
import java.rmi.RemoteException;

public class KeyValueStoreImpl implements KeyValueStoreService {

    private Map<String, String> store;

    public KeyValueStoreImpl() {
        this.store = new HashMap<>();
    }

    public synchronized String put(String key, String value) throws RemoteException {
        store.put(key, value);
        return "Key '" + key + "' stored with value '" + value + "'";
    }

    public synchronized String get(String key) throws RemoteException {
        String value = store.get(key);
        return value != null ? value : "Key '" + key + "' not found";
    }

    public synchronized String delete(String key) throws RemoteException {
        String value = store.remove(key);
        return value != null ? "Deleted key '" + key + "' with value '" + value + "'" : "Key '" + key + "' not found";
    }

    public synchronized String getAll() throws RemoteException {
        StringBuilder response = new StringBuilder();
        for (Map.Entry<String, String> entry : store.entrySet()) {
            response.append(entry.getKey()).append(" : ").append(entry.getValue()).append("\n");
        }
        return response.toString();
    }

    public synchronized String deleteAll() throws RemoteException {
        store.clear();
        return "All key-value pairs deleted successfully";
    }
}