package server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.rmi.Remote;
import java.rmi.RemoteException;

import shared.KeyValueStoreService;

public class KeyValueStoreImpl_Paxos implements KeyValueStoreService {

    private ConcurrentHashMap<String, String> store;
    private Proposer proposer; // Interface for communication with proposer
    //private Map<String, String> prepareMap; // Temporary storage for "prepare" requests

    public KeyValueStoreImpl_Paxos(Proposer proposer) {
        this.store = new ConcurrentHashMap<>();
        this.proposer = proposer;
        //this.prepareMap = new HashMap<>();
    }

    public synchronized String put(String key, String value) throws RemoteException {
        String result = proposer.proposePut(key, value); // Delegate to proposer
        if (result.equals("Success")) {
            // Store key-value pair in main store on successful prepare
            //store.put(key, value);
            //prepareMap.remove(key);  // Cleanup temporary storage
            return "Key '" + key + "' stored with value '" + value + "'";
        } else {
            //prepareMap.remove(key); // Cleanup on failure
            return "Error putting the " + key + "with value " + value + " in store";
        }
    }

    public synchronized String get(String key) throws RemoteException {
        String value = store.get(key);
        return value != null ? value : "Key '" + key + "' not found";
    }

    public synchronized String delete(String key) throws RemoteException {
        String result = proposer.proposeDelete(key); // Delegate to proposer
        if (result.equals("Success")) {
            // Store key-value pair in main store on successful prepare
            //store.remove(key);
           // prepareMap.remove(key);  // Cleanup temporary storage
            return "Deleted key '" + key + "'";
        } else {
            //prepareMap.remove(key); // Cleanup on failure
            return "Error deleting the key:-" + key;
        }
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

    public synchronized void commitPut(String key, String value) throws RemoteException {
        store.put(key, value);   // Add/update key-value pair to main store from prepared state
    }

    public synchronized void commitDelete(String key) throws RemoteException {
        store.remove(key);   // Remove to main store from prepared state
    }
}
