package client;

import java.net.*;
import java.io.*;
import java.util.UUID;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * This represents the UDP client which communicates to the UDP server which is connectionless
 */
public class UDPClient extends ClientFactory {

  private static final int MAX_PACKET_SIZE = 65507;

  public void initiateCommunication(String serverIP, int serverPort) {
    try (DatagramSocket dataSocket = new DatagramSocket();
      BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {
      InetAddress hostAddress = InetAddress.getByName(serverIP);

      prePopulateKeyValuePairs(dataSocket, hostAddress, serverPort);

      while (true) {
        String request = generateRequest(userInput);
        if(request.isEmpty()) {
          continue;
        }
        sendUserRequest(dataSocket, request, hostAddress, serverPort);

        System.out.print("Want to perform another operation? (yes/no): ");
        String requestContinue = userInput.readLine().toLowerCase();
        if (!requestContinue.equals("yes")) {
          break;
        }
      }
    } catch (SocketException e) {
        System.out.println("Error communicating with server: " + e.getMessage());
        ClientLogger.log("Error communicating with server: " + e.getMessage());
    } catch (IOException e) {
        System.out.println("Error communicating with server: " + e.getMessage());
        ClientLogger.log("Error communicating with server: " + e.getMessage());
    } catch (NumberFormatException e) {
        System.out.println("Error communicating with server: " + e.getMessage());
        ClientLogger.log("Error communicating with server: " + e.getMessage());
    } catch (ArrayIndexOutOfBoundsException e) {
        System.out.println("Error communicating with server: " + e.getMessage());
        ClientLogger.log("Error communicating with server: " + e.getMessage());    }
  }

  private static long generateChecksum(String requestString) {
    byte [] m = requestString.getBytes();
    Checksum crc32 = new CRC32();
    crc32.update(m, 0, m.length);
    return crc32.getValue();
  }

private static void sendUserRequest(DatagramSocket dataSocket, String userRequest, InetAddress hostAddress, int serverPort) throws IOException {
        
        String[] requestToken = userRequest.split("==");
        String action = requestToken[1];
        
        long requestId = generateChecksum(userRequest);
        userRequest = requestId + "==" + userRequest;

        byte[] m = userRequest.getBytes();
        DatagramPacket request = new DatagramPacket(m, m.length, hostAddress, serverPort);
        dataSocket.send(request);

        //dataSocket.setSoTimeout(5000);
        StringBuilder responseBuilder = new StringBuilder();
        byte[] buffer = new byte[MAX_PACKET_SIZE];
        DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

        try {
            while (true) {
                dataSocket.receive(reply);
                String response = new String(reply.getData(), 0, reply.getLength());
                String[] responseToken = response.split("==");
                long responseRequestId = Long.parseLong(responseToken[0]);
                if(responseRequestId != requestId) {
                  ClientLogger.log("Received Malformed response for request: " + requestId +
                    " ; Received response for " + responseToken[0]);
                } else {
                  responseBuilder.append(response);
                  ClientLogger.log("Received response " + response);
                  System.out.println(action+" Reply: " + new String(reply.getData(), 0, reply.getLength()));
                }
                if (reply.getLength() < MAX_PACKET_SIZE) {
                    break;
                }
            }
            String completeResponse = responseBuilder.toString();
            System.out.println("Complete Response: " + completeResponse);
        } catch (SocketTimeoutException e) {
            System.out.println("Request timed out.. received no response from server.");
        }
    }


  private static void prePopulateKeyValuePairs(DatagramSocket socket, InetAddress hostAddress, int serverPort) {
        final int KEYS_COUNT = 1000;
        try {
            // PUT requests
            for (int i = 1; i <= KEYS_COUNT; i++) {
                String requestId = UUID.randomUUID().toString();
                String key = Integer.toString(i);
                String value = Integer.toString(i * 10);
                String putString = requestId + "==PUT==" + key + "==" + value;

                sendUserRequest(socket, putString, hostAddress, serverPort);
                System.out.println("Pre-populated key: " + key + " with value: " + value);
                ClientLogger.log("Pre-populated key: " + key + " with value: " + value);
            }
            //GET requests
            for (int i = 1; i <= KEYS_COUNT; i++) {
                String requestId = UUID.randomUUID().toString();
                String key = Integer.toString(i);
                String getString = requestId + "==GET==" + key;

                sendUserRequest(socket, getString, hostAddress, serverPort);
                System.out.println("GET Pre-populated key: " + key);
                ClientLogger.log("GET Pre-populated key: " + key);
            }
            //DELETE requests
            for (int i = 1; i <= 5; i++) {
                String requestId = UUID.randomUUID().toString();
                String key = Integer.toString(i);
                String deleteString = requestId + "==DELETE==" + key;

                sendUserRequest(socket, deleteString, hostAddress, serverPort);
                System.out.println("DELETED Pre-populated key: " + key);
                ClientLogger.log("DELETED Pre-populated key: " + key);
            }
        } catch (IOException e) {
            System.out.println("Error pre-populating data " + e.getMessage());
            ClientLogger.log("Error pre-populating data " + e.getMessage());
          }
      }
}
