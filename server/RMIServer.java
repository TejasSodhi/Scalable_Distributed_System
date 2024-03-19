package server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import shared.KeyValueStoreService;

 
public class RMIServer {
    public static void main(String[] args) {
        try {
            int port = Integer.parseInt(args[0]);
            KeyValueStoreImpl keyValueStore = new KeyValueStoreImpl();
            KeyValueStoreService stub = (KeyValueStoreService) UnicastRemoteObject.exportObject(keyValueStore, 0);

            Registry registry = LocateRegistry.createRegistry(port);
            registry.rebind("KeyValueStoreService", stub);

            System.out.println("Server connected");
        } catch (RemoteException e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}