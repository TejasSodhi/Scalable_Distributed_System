package server;
import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * This represents a UDP server which receives datagram requests, validates them and sends responses
 * to the client. This class extends the abstract server class which has the common implementation
 * to handle key value requests.
 */
public class UDPServer extends ServerFactory {

  static final ServerLogger serverLogger = new ServerLogger();
  private static final int MAX_PACKET_SIZE = 65507;
  
  // public void listen(int portNumber) {

  //   // Server socket creation on specified port number.
  //   try (DatagramSocket socket = new DatagramSocket(portNumber)) {
  //     System.out.println("Server active on port " + portNumber);
  //     serverLogger.log("Server active on port " + portNumber);

  //     while (true) {
  //       byte[] buffer = new byte[1000];

  //       // Receiving datagram request
  //       DatagramPacket request = new DatagramPacket(buffer,buffer.length);
  //       socket.receive(request);

  //       // Validating requests on server side.
  //       if (!validRequest(request)) {
  //         String response = "Couldn't process request.";
  //         serverLogger.logMalformedRequest(request.getAddress(), request.getLength());
  //         DatagramPacket reply = new DatagramPacket(response.getBytes(),
  //           response.getBytes().length, request.getAddress(), request.getPort());
  //         socket.send(reply);
  //         continue;
  //       }

  //       // parsing and processing request
  //       String msg = new String(request.getData(), 0, request.getLength());
  //       System.out.println("received message =  " + msg);
  //       serverLogger.logRequest(request.getAddress(), msg);

  //       //remove checksum from the msg and send to ServerFactory
  //       String[] parts = msg.split("::");
  //       String isCheckSum = parts[0];
  //       String[] messageWithoutChecksum = Arrays.copyOfRange(parts, 1, parts.length);
  //       String modifiedMsg = String.join("::", messageWithoutChecksum);

  //       // process request from the key value store
  //       //String response = processRequest(msg);
  //       String response = processRequest(modifiedMsg);
  //       System.out.println("response from ServerFactory received = "  + response);

  //       // Append the checksum to the response
  //       String responseWithChecksum = isCheckSum + "::" + response;

  //       // sending response back to client
  //       DatagramPacket reply = new DatagramPacket(responseWithChecksum.getBytes(),
  //         responseWithChecksum.getBytes().length, request.getAddress(), request.getPort());
  //       socket.send(reply);
  //       serverLogger.logResponse(reply.getAddress(),responseWithChecksum);
  //     }
  //   } catch (SocketException e) {
  //     System.out.println("Socket: " + e.getMessage());
  //   } catch (IOException e) {
  //     System.out.println("IO: " + e.getMessage());
  //   }
  // }


    public void listen(int portNumber) {
        try (DatagramSocket socket = new DatagramSocket(portNumber)) {
            System.out.println("Server active on port " + portNumber);

            while (true) {
                byte[] buffer = new byte[MAX_PACKET_SIZE];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);

                // if (!validRequest(request)) {
                //     continue;
                // }

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

                String[] parts = msg.split("::");
                String isCheckSum = parts[0];
                String[] messageWithoutChecksum = Arrays.copyOfRange(parts, 1, parts.length);
                String modifiedMsg = String.join("::", messageWithoutChecksum);

                String response = processRequest(modifiedMsg);
                String responseWithChecksum = isCheckSum + "::" + response;

                // DatagramPacket reply = new DatagramPacket(responseWithChecksum.getBytes(),
                //         responseWithChecksum.getBytes().length, request.getAddress(), request.getPort());
                // socket.send(reply);
                // Send response in chunks
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
    String result = String.join("::", Arrays.copyOfRange(requestParts, 1, requestParts.length));

    byte [] m = result.getBytes();
    Checksum crc32 = new CRC32();
    crc32.update(m, 0, m.length);
    return crc32.getValue();
  }

  /**
   * This function validates a given datagram packet, if it is as per protocol and is not corrupted.
   * @param request Datagram request.
   * @return boolean indicating request is valid or not.
   */
  private boolean validRequest(DatagramPacket request) {

    String requestData = new String(request.getData(), 0, request.getLength());
    String[] parts = requestData.split("::");

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