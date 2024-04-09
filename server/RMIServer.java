package server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import shared.KeyValueStoreService;

 
public class RMIServer {
    public static void main(String[] args) {
        try {
            int port = Integer.parseInt(args[0]);

            // Create Coordinator
            List<KeyValueStoreService> serverList = new ArrayList<>();
            Coordinator coordinator = new CoordinatorImpl(serverList);
            Registry registry = null;
            // Create and bind Servers
            for (int i = 0; i < 5; i++) {
                KeyValueStoreImpl server = new KeyValueStoreImpl(coordinator); // Pass coordinator reference to each server
                serverList.add(server); // Add server to the list of servers
                KeyValueStoreService stub = (KeyValueStoreService) UnicastRemoteObject.exportObject(server, 0);
                registry = LocateRegistry.createRegistry(port + i);
                registry.rebind("KeyValueStoreService", stub);
            }
            System.out.println("Servers connected");
            try {
                Arrays.asList(registry.list()).forEach(System.out::println);
            } catch  (RemoteException e) {

            }
        } catch (RemoteException e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}