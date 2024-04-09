package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import shared.KeyValueStoreService;

public class CoordinatorImpl implements Coordinator {

    private List<KeyValueStoreService> serverList;

    public CoordinatorImpl(List<KeyValueStoreService> serverList) throws RemoteException {
        this.serverList = serverList;
    }

    @Override
    public String preparePut(String key, String value) throws RemoteException {
        List<RemoteException> exceptions = new ArrayList<>();
        for (KeyValueStoreService server : serverList) {
            System.out.println("Updating " +  server + "...");
            try {
                if (!server.preparePut(key, value).equals("Success")) {
                    // Abort on any server failing to respond with "Success" during prepare
                    abort(key, value);
                    exceptions.add(new RemoteException("Failed to prepare put for key: " + key));
                }
            } catch (RemoteException e) {
                exceptions.add(e);
                // Abort on any server failure during prepare
                abort(key, value);
            }
        }

        if (!exceptions.isEmpty()) {
            throw new RemoteException("Errors occurred during prepare put", exceptions.get(0));
        }
        return "Success";  // All servers responded with "Success" during prepare
    }


   @Override
    public String prepareDelete(String key) throws RemoteException {
        List<RemoteException> exceptions = new ArrayList<>();
        for (KeyValueStoreService server : serverList) {
            try {
                if (!server.prepareDelete(key).equals("Success")) {
                    // Abort on any server failing to respond with "Success" during prepare
                    abort(key, null);
                    exceptions.add(new RemoteException("Failed to prepare delete for key: " + key));
                }
            } catch (RemoteException e) {
                exceptions.add(e);
                // Abort on any server failure during prepare
                abort(key, null);
            }
        }

        if (!exceptions.isEmpty()) {
            throw new RemoteException("Errors occurred during prepare delete", exceptions.get(0));
        }

        return "Success";  // All servers responded with "Success" during prepare
    }


    // @Override
    // public String prepareDeleteAll() throws RemoteException {
    //     List<RemoteException> exceptions = new ArrayList<>();
    //     for (KeyValueStoreService server : serverList) {
    //         try {
    //             if (!server.deleteAll().equals("Success")) {
    //                 // Abort on any server failing to respond with "Success" during prepare
    //                 abort(null, null);
    //                 throw new RemoteException("Failed to prepare delete all", exceptions);
    //             }
    //         } catch (RemoteException e) {
    //             exceptions.add(e);
    //             // Abort on any server failure during prepare
    //             abort(null, null);
    //             throw new RemoteException("Failed to prepare delete all", exceptions);
    //         }
    //     }

    //     return "Success";  // All servers responded with "Success" during prepare
    // }

    @Override
    public void commitPut(String key, String value) throws RemoteException {
        List<RemoteException> exceptions = new ArrayList<>();
        for (KeyValueStoreService server : serverList) {
            try {
                server.commitPut(key, value);
            } catch (RemoteException e) {
                exceptions.add(e);
                // Log exceptions here (optional)
            }
        }
        // Potentially log exceptions here (optional)
    }

    @Override
    public void commitDelete(String key) throws RemoteException {
        List<RemoteException> exceptions = new ArrayList<>();
        for (KeyValueStoreService server : serverList) {
            try {
                server.commitDelete(key);
            } catch (RemoteException e) {
                exceptions.add(e);
                // Log exceptions here (optional)
            }
        }
        // Potentially log exceptions here (optional)
    }

    @Override
    public void abort(String key, String value) throws RemoteException {
        for (KeyValueStoreService server : serverList) {
            try {
                if (key != null) {
                    server.delete(key);  // If key exists, delete it
                }
            } catch (RemoteException e) {
                // Log exceptions here (optional)
            }
        }
        // Potentially log exceptions here (optional)
    }
}
