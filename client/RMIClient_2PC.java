package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import shared.KeyValueStoreService;

public class RMIClient_2PC {

    static final ClientLogger clientLogger = new ClientLogger();

    public static void main(String[] args) throws IOException {
        try {
            int numberOfServers = 5;
            String hostname = (args.length < 1) ? null : args[0];
            int basePort = Integer.parseInt(args[1]);
            int coordinatorPort = basePort + numberOfServers-1; // Coordinator is the last server

            List<KeyValueStoreService> replicaStubs = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                int port = basePort + i;
                Registry registry = LocateRegistry.getRegistry(hostname, port);
                KeyValueStoreService stub = (KeyValueStoreService) registry.lookup("KeyValueStoreService"+(i+1));
                replicaStubs.add(stub);
            }

            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            int serverNumber = -1;
            do {
                System.out.println("Enter the server number (1-5) to send the request:");
                try {
                    serverNumber = Integer.parseInt(userInput.readLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid server number. Please enter a number between 1 and 5.");
                    continue;
                }
            } while (serverNumber < 1 || serverNumber > 5);

            KeyValueStoreService selectedServer = replicaStubs.get(serverNumber - 1);
            //System.out.println(selectedServer);
            if (serverNumber == 5) {
                System.out.println("Coordinator selected. You can send 2PC requests.");
            }

            // Pre-populate data
            System.out.println("Do you want to prepopulate the key-value pairs? (yes/no)");
            String prepopulateChoice = userInput.readLine().toLowerCase();
            if (prepopulateChoice.equals("yes")) {
                prePopulateKeyValuePairs(selectedServer);
            }

            while (true) {
                System.out.println("The application can add keys and respective values, get all the values for a particular key, and delete any particular key value");
                System.out.println("Which functionality do you want to use?");
                System.out.println("1. PUT");
                System.out.println("2. GET");
                System.out.println("3. GETALL");
                System.out.println("4. DELETE");
                System.out.println("5. DELETEALL");
                System.out.print("Enter from the above options only (1/2/3/4/5): ");

                String option = userInput.readLine();
                switch (option) {
                    case "1":
                        putRequest(userInput, selectedServer);
                        break;
                    case "2":
                        getRequest(userInput, selectedServer);
                        break;
                    case "3":
                        getAllRequest(selectedServer);
                        break;
                    case "4":
                        deleteRequest(userInput, selectedServer);
                        break;
                    case "5":
                        deleteAllRequest(selectedServer);
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter your choice between 1 and 5.");
                        clientLogger.log("Invalid choice. Please enter your choice between 1 and 5.");
                        break;
                }

                System.out.print("Want to perform another operation? (yes/no): ");
                String requestContinue = userInput.readLine().toLowerCase();
                if (!requestContinue.equals("yes")) {
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    private static void prePopulateKeyValuePairs(KeyValueStoreService stub) {
        final int KEYS_COUNT = 10;
        try {
            // PUT requests
            for (int i = 1; i <= KEYS_COUNT; i++) {
                String res = stub.put("key" + i,  "value" + i*10);
                System.out.println(res);
                clientLogger.log(res);
            }
            //GET requests
            for (int i = 1; i <= KEYS_COUNT; i++) {
                String res = stub.get("key" + i);
                System.out.println(res);
                clientLogger.log(res);
            }
            //DELETE requests
            for (int i = 1; i <= 5; i++) {
                String res = stub.delete("key" + i);
                System.out.println(res);
                clientLogger.log(res);
            }
        } catch (IOException e) {
            
        }
    }

    private static void putRequest(BufferedReader userInput, KeyValueStoreService selectedServer) throws IOException {
        System.out.print("Please enter the key: ");
        String key = userInput.readLine();
        System.out.print("Please enter the value for the key: ");
        String value = userInput.readLine();
        String res = selectedServer.put(key, value);
        System.out.println(res);
        clientLogger.log(res);
    }

    private static void getRequest(BufferedReader userInput, KeyValueStoreService selectedServer) throws IOException {
        System.out.print("Please enter the key (only integer values): ");
        String key = userInput.readLine();
        String res = selectedServer.get(key);
        System.out.println(res);
        clientLogger.log(res);
    }

    private static void getAllRequest(KeyValueStoreService selectedServer) throws IOException {
        String res = selectedServer.getAll();
        System.out.println(res);
        clientLogger.log(res);
    }

    private static void deleteRequest(BufferedReader userInput, KeyValueStoreService selectedServer) throws IOException {
        System.out.print("Please enter the key (integer): ");
        String key = userInput.readLine();
        String res = selectedServer.delete(key);
        System.out.println(res);
        clientLogger.log(res);
    }

    private static void deleteAllRequest(KeyValueStoreService selectedServer) throws IOException {
        String res = selectedServer.deleteAll();
        System.out.println(res);
        clientLogger.log(res);
    }
}
