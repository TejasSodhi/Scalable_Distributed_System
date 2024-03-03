package server;

import java.io.IOException;
import java.net.Socket;

/**
 * Abstract class that implements common functionalities like processing key value store read/write
 * for any type of server.
 */
public abstract class ServerFactory {
  private static final KeyValueStore keyValueStore = new KeyValueStore();
  //static final ServerLogger serverLogger = new ServerLogger();

  public String processRequest(String inputLine) throws IOException {
    String[] tokens = inputLine.split("::");
    System.out.println(inputLine);
    if (tokens.length < 4) {
      return "Invalid request format";
    }

    String requestID = tokens[0];
    String operation = tokens[2];
    String key = tokens[3];
    String value = tokens.length > 4 ? tokens[4] : null;

    switch (operation.toUpperCase()) {
      case "PUT":
        return putRequest(requestID, key, value);
      case "GET":
        return getRequest(requestID, key);
      case "DELETE":
        return deleteRequest(requestID, key);
      default:
        return requestID + ": Unsupported operation: " + operation;
    }
  }

  protected String getRequest(String requestID, String key) throws IOException {
    String value = keyValueStore.get(key);
    if(value != null) {
      String keyPresent = requestID + ": Value for key '" + key + "': " + value;
      return keyPresent;
    }
    else return requestID + ": Key '" + key + "' not found";
  }

   protected String putRequest(String requestID, String key, String value) throws IOException {
      if (value == null) {
        return requestID + ": PUT operation requires a value";
      }
      keyValueStore.put(key, value);
      String successMessage = requestID + ": Key '" + key + "' stored with value '" + value + "'";
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


  // @Override
  // public void handleRequest(Socket clientSocket) throws IOException {
  //   serverLogger.log("Unable to process request. Server handle request behavior undefined");
  // }
}
