package server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import java.rmi.Remote;
import java.rmi.RemoteException;

import shared.KeyValueStoreService;

public class KeyValueStoreImpl implements KeyValueStoreService {


    private ConcurrentHashMap<String, String> store;
    private Coordinator coordinator;
    private Map<String, String> prepareMap; // Temporary storage for "prepare" requests


    public KeyValueStoreImpl(Coordinator coordinator) {
        this.store = new ConcurrentHashMap<>();
        this.coordinator = coordinator;
        this.prepareMap = new HashMap<>();
    }

    // public synchronized String put(String key, String value) throws RemoteException {
    //     store.put(key, value);
    //     //serverLogger.log("Key '" + key + "' stored with value '" + value + "'");
    //     return "Key '" + key + "' stored with value '" + value + "'";
    // }

    public synchronized String put(String key, String value) throws RemoteException {
        String result = coordinator.preparePut(key, value);
        System.out.println("result inside  put: "+result);
        if (result.equals("Success")) {
            //Store key-value pair in main store on successful prepare**
            //store.put(key, value);
            coordinator.commitPut(key, value);
            prepareMap.remove(key);  // Cleanup temporary storage
            return "Key '" + key + "' stored with value '" + value + "'";
        } else {
            prepareMap.remove(key); // Cleanup on failure
            return "Error putting the " + key + "with value " + value + " in store";
        }
    }


    public synchronized String get(String key) throws RemoteException {
        String value = store.get(key);
        return value != null ? value : "Key '" + key + "' not found";
    }

    // public synchronized String delete(String key) throws RemoteException {
    //     String value = store.remove(key);
    //     return value != null ? "Deleted key '" + key + "' with value '" + value + "'" : "Key '" + key + "' not found";
    // }

    public synchronized String delete(String key) throws RemoteException {
        String result = coordinator.prepareDelete(key);
        if (result.equals("Success")) {
            //Store key-value pair in main store on successful prepare**
            //store.remove(key);
            coordinator.commitDelete(key);
            prepareMap.remove(key);  // Cleanup temporary storage
            return "Deleted key '" + key + "'";
        } else {
            prepareMap.remove(key); // Cleanup on failure
            return "Error deleting the key:-" + key;
        }
    }

    public synchronized String preparePut(String key, String value) throws RemoteException {
        prepareMap.put(key, value);  // Store key-value pair for potential commit
        return "Success";
    }

    public synchronized String prepareDelete(String key) throws RemoteException {
        if (store.containsKey(key)) {
            prepareMap.put(key, null);  // Store key for deletion in commit
            return "Success";
        } else {
            return "Key '" + key + "' not found";
        }
    }

    public synchronized void commitPut(String key, String value) throws RemoteException {
        store.put(key, value);   // Add/update key-value pair to main store from prepared state
    }

    public synchronized void commitDelete(String key) throws RemoteException {
        store.remove(key);   // Remove to main store from prepared state
    }

    public synchronized String getAll() throws RemoteException {
        StringBuilder response = new StringBuilder();
        for (Map.Entry<String, String> entry : store.entrySet()) {
            response.append(entry.getKey()).append(" : ").append(entry.getValue()).append("\n");
        }
        return response.length() > 0 ? response.toString() : "No keys in the store.";
    }

    public synchronized String deleteAll() throws RemoteException {
        store.clear();
        return "All key-value pairs deleted successfully";
    }
}