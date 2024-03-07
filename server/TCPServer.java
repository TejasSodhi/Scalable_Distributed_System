package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This represents a TCP based server which listens from a specific TCP client.
 */
public class TCPServer extends ServerFactory {

    static final ServerLogger serverLogger = new ServerLogger();

    public void initiateCommunication(int portNumber) {
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {

            System.out.println("Server is listening on port " + portNumber);
            serverLogger.log("Server is listening on port " + portNumber);

            while (true) {
                // Start listening to client requests and creating client socket
                Socket clientSocket = serverSocket.accept();
                System.out.println("client.Client connected: " + clientSocket.getInetAddress());
                serverLogger.logRequest(clientSocket.getInetAddress(), "Client connected");

                try {
                    handleRequest(clientSocket);
                } catch (IOException e) {
                    //e.printStackTrace();
                    serverLogger.log("Error fetching data from server for client: " + clientSocket.getInetAddress());
                } finally {
                    // log information when client closes connection
                    clientSocket.close();
                    System.out.println("client.Client disconnected");
                    serverLogger.log("Client disconnected: " + clientSocket.getInetAddress());
                }
            }
        } catch (IOException e) {
            serverLogger.log("Error communicating with client: " + e.getMessage());
        }
    }

    public void handleRequest(Socket clientSocket) throws IOException {
        try (
          BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
          PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received from client: " + inputLine);
                // log client request information
                serverLogger.logRequest(clientSocket.getInetAddress(), inputLine);

                // get information from the key value store
                String response = processRequest(inputLine);

                // write back the response to the client
                out.println(response);

                // log the response information
                serverLogger.logResponse(clientSocket.getInetAddress(),response);
            }
        } catch (IOException e) {
            // Logging info about timeouts
            System.err.println("Timeout occurred. Server did not respond within the specified time.");
            serverLogger.logMalformedRequest(clientSocket.getInetAddress());

        }
    }
}
 