package client;

import java.net.*;
import java.io.*;
import java.util.UUID;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * This represents the UDP client which communicates to the UDP server over a given port and host
 * address.
 */
public class UDPClient extends ClientFactory {
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

  private static void sendUserRequest(DatagramSocket dataSocket, String userRequest, InetAddress hostAddress,int serverPort) throws IOException {

    // Parse request information from the request string.
    String[] requestToken = userRequest.split("::");
    String action = requestToken[1];

    // creating datagram packet
    long requestId = generateChecksum(userRequest);
    userRequest = requestId + "::" + userRequest;

    byte[] m = userRequest.getBytes();
    DatagramPacket request = new DatagramPacket(m, m.length, hostAddress, serverPort);

    // sending datagram packet
    dataSocket.send(request);

    // setting timeout of 5 seconds for udp request and waiting for response from server
    dataSocket.setSoTimeout(5000);
    byte[] buffer = new byte[1000];
    DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

    try {
      // receive response
      dataSocket.receive(reply);
      String response = new String(reply.getData(), 0, reply.getLength());
      String[] responseToken = response.split(":");
      long responseRequestId = Long.parseLong(responseToken[0]);

      // validating malformed responses from server
      if(responseRequestId != requestId) {
        ClientLogger.log("Received Malformed response for request: " + requestId +
          " ; Received response for " + responseToken[0]);
      } else {
        ClientLogger.log("Received response " + response);
        System.out.println(action+" Reply: " + new String(reply.getData(), 0, reply.getLength()));
      }
    } catch(SocketTimeoutException e) {
      System.out.println("Request timed out.. received no response from server for request: "
        + requestId);
      ClientLogger.log("Request timed out.. received no response from server for request: "
          + requestId);
    }
  }

  private static void prePopulateKeyValuePairs(DatagramSocket aSocket, InetAddress aHost, int serverPort) {
        final int KEYS_COUNT = 10;
        try {
            // PUT requests
            for (int i = 1; i <= KEYS_COUNT; i++) {
                String requestId = generateRequestId();
                String key = Integer.toString(i);
                String value = Integer.toString(i * 10);
                String putString = requestId + "::PUT::key" + key + "::value" + value;

                sendUserRequest(aSocket, putString, aHost, serverPort);
                System.out.println("Pre-populated key" + key + " with value " + value);
                ClientLogger.log("Pre-populated key" + key + " with value " + value);
            }
            //GET requests
            for (int i = 1; i <= KEYS_COUNT; i++) {
                String requestId = generateRequestId();
                String key = Integer.toString(i);
                String getString = requestId + "::GET::key" + key;

                sendUserRequest(aSocket, getString, aHost, serverPort);
                System.out.println("GET Pre-populated key" + key);
                ClientLogger.log("GET Pre-populated key" + key);
            }
            //DELETE requests
            for (int i = 1; i <= KEYS_COUNT-5; i++) {
                String requestId = generateRequestId();
                String key = Integer.toString(i);
                String deleteString = requestId + "::DELETE::key" + key;

                sendUserRequest(aSocket, deleteString, aHost, serverPort);
                System.out.println("DELETED Pre-populated key" + key);
                ClientLogger.log("DELETED Pre-populated key" + key);
            }
        } catch (IOException e) {
            System.out.println("Error pre-populating data " + e.getMessage());
            ClientLogger.log("Error pre-populating data " + e.getMessage());
          }
      }
}
