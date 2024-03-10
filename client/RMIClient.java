package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import shared.KeyValueStoreService;

public class RMIClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost");
            KeyValueStoreService stub = (KeyValueStoreService) registry.lookup("KeyValueStoreService");

            // Perform operations using stub
            System.out.println(stub.put("key1", "value1"));
            System.out.println(stub.put("key2", "value2"));
            System.out.println(stub.get("key1"));
            System.out.println(stub.getAll());
            System.out.println(stub.delete("key2"));
            System.out.println(stub.getAll());
            System.out.println(stub.deleteAll());
            System.out.println(stub.getAll());
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
