package server;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.*; 

/**
 * Abstract class that implements common functionalities like processing key value store read/write
 * for any type of server.
 */
public abstract class ServerFactory {
  private KeyValueStore keyValueStore = new KeyValueStore();
  //static final ServerLogger serverLogger = new ServerLogger();

  public String processRequest(String inputLine) throws IOException {
    String[] tokens = inputLine.split("::");
    System.out.println(inputLine);
    if (tokens.length < 3) {
      return "Invalid request format";
    }
    // if(tokens[0] != "N/A") {
    //   String checkSum = tokens[0];
    // }
    //String checksum = tokens[0] != "N/A" ? tokens[0] : "NoChecksum";
    String requestID = tokens[0];
    String operation = tokens[1];
    String key = tokens[2];
    //System.out.println("key = "  + key);
    String value = tokens.length > 3 ? tokens[3] : null;
    //System.out.println("value = "  + value);

    switch (operation.toUpperCase()) {
      case "PUT":
        return putRequest(requestID, key, value);
      case "GET":
        String res = getRequest(requestID, key);
        System.out.println("res of GET value = " + res);
        return res;
      case "GETALL":
        return getAllRequest(requestID);
      case "DELETE":
        return deleteRequest(requestID, key);
      case "DELETEALL":
        return deleteAllRequest(requestID);
      case "GETALLFORUDP":
        return getAllRequestForUDP(requestID);
      default:
        return requestID + ": Unsupported operation: " + operation;
    }
  }

  protected String getRequest(String requestID, String key) throws IOException {
    String getValue = keyValueStore.get(key);
    System.out.println("value = " + getValue);

    if(getValue != null) {
      String keyPresent = requestID + ": Value for key '" + key + "': " + getValue;
      return keyPresent;
    }
    else return requestID + ": Key '" + key + "' not found";
  }

protected String getAllRequest(String requestID) throws IOException {
  StringBuilder responseBuilder = new StringBuilder();
  responseBuilder.append(requestID).append(":\n"); // Start with a newline for better formatting

  // Get all keys using keySet()
  Set<String> allKeys = keyValueStore.getAllKeys();

  if (allKeys.isEmpty()) {
    return requestID + ": Key value store is empty";
  }

  for (String key : allKeys) {
    String value = keyValueStore.get(key);
    responseBuilder.append(key).append(" : ").append(value).append("\n"); // Append key-value pair on a newline
  }

  return responseBuilder.toString(); // Return the complete response with newlines
}


protected String getAllRequestForUDP(String requestID) throws IOException {
    StringBuilder responseBuilder = new StringBuilder();
    responseBuilder.append(requestID).append(":\n");

    Set<String> allKeys = keyValueStore.getAllKeys();

    if (allKeys.isEmpty()) {
        return requestID + ": Key value store is empty";
    }
    List<String> allKeysList = new ArrayList<>(allKeys);
    final int CHUNK_SIZE = 10; // Defining the chunk size

    for (int i = 0; i < allKeysList.size(); i += CHUNK_SIZE) {
        int end = Math.min(i + CHUNK_SIZE, allKeysList.size());
        List<String> keysChunk = allKeysList.subList(i, end);
        StringBuilder chunkBuilder = new StringBuilder();

        for (String key : keysChunk) {
            String value = keyValueStore.get(key);
            chunkBuilder.append(key).append(" : ").append(value).append("\n");
        }

        String chunkResponse = chunkBuilder.toString();
        responseBuilder.append(chunkResponse);
    }

    return responseBuilder.toString();
}


  protected String putRequest(String requestID, String key, String value) throws IOException {
      System.out.println("** Inside putRequest: key=" + key + ", value=" + value);
      if (value == null) {
        return requestID + ": PUT operation requires a value";
      }
      keyValueStore.put(key, value);
      String successMessage = requestID + ": Key '" + key + "' stored with value '" + value + "'";
      System.out.println("successMessage = "  + successMessage);
      return successMessage;
  }

  protected String deleteRequest(String requestID, String key) throws IOException {
      String value = keyValueStore.delete(key);
      if(value !=  null){ 
        String deletedKeyValue = requestID + ": Deleted key '" + key + "' with value '" + value + "'";
        return deletedKeyValue;
      }
      else return requestID + ": Key '" + key + "' not found";
  }

  protected String deleteAllRequest(String requestID) throws IOException {
    keyValueStore.deleteAll();
    return requestID + ": All key-value pairs deleted successfully";
  }

}
