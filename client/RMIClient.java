package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import shared.KeyValueStoreService;

public class RMIClient {

    static final ClientLogger clientLogger = new ClientLogger();

    public static void main(String[] args) throws IOException{
        try {
            String hostname = (args.length < 1) ? null : args[0];
            int port = Integer.parseInt(args[1]);

            Registry registry = LocateRegistry.getRegistry(hostname, port);
            KeyValueStoreService stub = (KeyValueStoreService) registry.lookup("KeyValueStoreService");

            // Pre-populate  the store with some data
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Do you want to prepopulate the key-value pairs? (yes/no)");
            String prepopulateChoice = userInput.readLine().toLowerCase();
            if (prepopulateChoice.equals("yes")) {
                prePopulateKeyValuePairs(stub);
            }

            while (true) {

                System.out.println("The application can add keys and repective values, get all the values for a particular key and delete any particular key value");
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
                    putRequest(userInput, stub);
                    break;
                case "2":
                    getRequest(userInput, stub);
                    break;
                case "3":
                    getAllRequest(stub);
                    break;
                case "4":
                    deleteRequest(userInput, stub);
                    break;
                case "5":
                    deleteAllRequest(stub);
                    break;
                default:
                    System.out.println("Invalid choice. Please enter your choice between 1 and 5.");
                    clientLogger.log("Invalid choice. Please enter your choice between 1 and 5.");
                    break;
                }

                // if(userRequest.isEmpty()) {
                //     continue;
                // }
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

    private static void putRequest(BufferedReader userInput, KeyValueStoreService stub) throws IOException {
        System.out.print("Please enter the key: ");
        String key = userInput.readLine();
        System.out.print("Please enter the value for the key: ");
        String value = userInput.readLine();
        String res = stub.put("key" + key,  "value" + value);
        System.out.println(res);
        clientLogger.log(res);
    }

    private static void getRequest(BufferedReader userInput, KeyValueStoreService stub) throws IOException {
        System.out.print("Please enter the key (only integer values): ");
        String key = userInput.readLine();
        String res = stub.get("key" + key);
        System.out.println(res);
        clientLogger.log(res);
    }

    private static void getAllRequest(KeyValueStoreService stub) throws IOException {
        String res = stub.getAll();
        System.out.println(res);
        clientLogger.log(res);
    }

    private static void deleteRequest(BufferedReader userInput, KeyValueStoreService stub) throws IOException {
        System.out.print("Please enter the key (integer): ");
        String key = userInput.readLine();
        String res  = stub.delete("key" + key);
        System.out.println(res);
        clientLogger.log(res);
    }

    private static void deleteAllRequest(KeyValueStoreService stub) throws IOException {
        String res = stub.deleteAll();
        System.out.println(res);
        clientLogger.log(res);
    }
}
