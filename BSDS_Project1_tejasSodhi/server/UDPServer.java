package server;
import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * This represents a UDP server which receives datagram requests, validates them and sends responses
 * to the client.
 */
public class UDPServer extends ServerFactory {

  static final ServerLogger serverLogger = new ServerLogger();
  private static final int MAX_PACKET_SIZE = 65507;
  

  public void initiateCommunication(int portNumber) {
        try (DatagramSocket socket = new DatagramSocket(portNumber)) {
            System.out.println("Server active on port " + portNumber);

            while (true) {
                byte[] buffer = new byte[MAX_PACKET_SIZE];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);

                // Validating requests on server side.
                if (!validRequest(request)) {
                  String response = "Couldn't process request.";
                  serverLogger.logMalformedRequest(request.getAddress(), request.getLength());
                  DatagramPacket reply = new DatagramPacket(response.getBytes(),
                    response.getBytes().length, request.getAddress(), request.getPort());
                  socket.send(reply);
                  continue;
                }

                String msg = new String(request.getData(), 0, request.getLength());
                System.out.println("received message =  " + msg);
                serverLogger.logRequest(request.getAddress(), msg);

                String[] parts = msg.split("==");
                String isCheckSum = parts[0];
                String[] messageWithoutChecksum = Arrays.copyOfRange(parts, 1, parts.length);
                String modifiedMsg = String.join("==", messageWithoutChecksum);

                String response = processRequest(modifiedMsg);
                String responseWithChecksum = isCheckSum + "==" + response;

                byte[] responseBytes = responseWithChecksum.getBytes();
                int numChunks = (int) Math.ceil((double) responseBytes.length / MAX_PACKET_SIZE);
                for (int i = 0; i < numChunks; i++) {
                    int start = i * MAX_PACKET_SIZE;
                    int end = Math.min((i + 1) * MAX_PACKET_SIZE, responseBytes.length);
                    byte[] chunk = Arrays.copyOfRange(responseBytes, start, end);
                    DatagramPacket reply = new DatagramPacket(chunk, chunk.length, request.getAddress(), request.getPort());
                    socket.send(reply);
                }
            }
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        }
  }

 

  private static long generateChecksum(String[] requestParts) {
    String result = String.join("==", Arrays.copyOfRange(requestParts, 1, requestParts.length));

    byte [] m = result.getBytes();
    Checksum crc32 = new CRC32();
    crc32.update(m, 0, m.length);
    return crc32.getValue();
  }

  /**
   * This function is used as validation for a given datagram packet checking for malformed requests.
   */
  private boolean validRequest(DatagramPacket request) {

    String requestData = new String(request.getData(), 0, request.getLength());
    String[] parts = requestData.split("==");

    if (parts.length < 3) {
      return false;
    }

    if (parts[0].isEmpty() || parts[1].isEmpty()) {
      return false;
    }

    long responseRequestId = Long.parseLong(parts[0]);

    // compare checksums, if not equal means malformed request.
    return responseRequestId == generateChecksum(parts);
  }
}