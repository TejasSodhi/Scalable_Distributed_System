package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.UUID;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class TCPClient extends ClientFactory {
    public void initiateCommunication(String serverIP, int serverPort) {
        Socket socket = null;
        try (Socket socket = new Socket(serverIP, serverPort);) {
            establishConnection(socket);
        } catch (IOException e) {
            System.out.println("Couldn't connect to server at mentioned IP and port");
            ClientLogger.log("Couldn't connect to server at mentioned IP and port\"");
            System.exit(1);
        }

        try (
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter serverWriter = new PrintWriter(socket.getOutputStream(), true)
        ) {
            prePopulateKeyValuePairs(serverReader, serverWriter);
            while (true) {
                String userRequest = generateRequest(userInput);
                if(userRequest.isEmpty()) {
                    continue;
                }
                sendUserRequest(serverWriter, serverReader, userRequest);

                System.out.print("Want to perform another operation? (yes/no): ");
                String requestContinue = userInput.readLine().toLowerCase();
                if (!requestContinue.equals("yes")) {
                    break;
                }
            }

        } catch (IOException e) {
            handleCommunicationError(e);
        }
    }

    private void establishConnection(Socket socket) throws IOException {
        socket.setSoTimeout(5000);
        System.out.println("Connected to the server");
        ClientLogger.log("Connected to the server");
    }

    private void handleCommunicationError(IOException e) {
        System.out.println("Error communicating with server: " + e.getMessage());
        ClientLogger.log("Error communicating with server: " + e.getMessage());
    }

    private static void sendUserRequest(PrintWriter out, BufferedReader in, String request) throws IOException {
        try{
            out.println(request); // send request
            String response = in.readLine(); // reponse  from server
            System.out.println(response);
            ClientLogger.log("Response from server: " + response); //logging  the response in CLientLogger

        } catch (SocketTimeoutException e){
            String[] strArr = request.split("::");
            String requestId = strArr[0];
            System.out.println("Received no response from the server for request id : "+requestId);
            ClientLogger.log("Received no response from the server for the request id : "+requestId);
        }
    }

    private static void prePopulateKeyValuePairs(BufferedReader in, PrintWriter out) {
        final int KEYS_COUNT = 10;
        try {
            // PUT requests
            for (int i = 1; i <= KEYS_COUNT; i++) {
                String requestId = generateRequestId();
                String key = Integer.toString(i);
                String value = Integer.toString(i * 10);
                String putString = requestId + "::PUT::key" + key + "::value" + value;

                sendRequest(out, in, putString);
                System.out.println("Pre-populated key" + key + " with value " + value);
                ClientLogger.log("Pre-populated key" + key + " with value " + value);
            }
            //GET requests
            for (int i = 1; i <= KEYS_COUNT; i++) {
                String requestId = generateRequestId();
                String key = Integer.toString(i);
                String getString = requestId + "::GET::key" + key;

                sendRequest(out, in, getString);
                System.out.println("GET key" + key);
                ClientLogger.log("GET key" + key);
            }
            //DELETE requests
            for (int i = 1; i <= KEYS_COUNT-5; i++) {
                String requestId = generateRequestId();
                String key = Integer.toString(i);
                String deleteString = requestId + "::DELETE::key" + key;

                sendRequest(out, in, deleteString);
                System.out.println("DELETED key" + key);
                ClientLogger.log("DELETED key" + key);
            }
        } catch (IOException e) {
            System.out.println("Error pre-populating data " + e.getMessage());
            ClientLogger.log("Error pre-populating data " + e.getMessage());
        }
    }

    private String generateRequestId() {
        return UUID.randomUUID().toString();
    }
}
