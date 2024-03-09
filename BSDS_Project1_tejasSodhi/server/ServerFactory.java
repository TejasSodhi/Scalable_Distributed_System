package server;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.*; 

/**
 * Abstract class that implements common functionalities for TCP and UDP
 */
public abstract class ServerFactory {
  private KeyValueStore keyValueStore = new KeyValueStore();

  public String processRequest(String inputLine) throws IOException {
    String[] tokens = inputLine.split("==");
    System.out.println(inputLine);
    if (tokens.length < 3) {
      return "Invalid request format";
    }

    String requestID = tokens[0];
    String operation = tokens[1];
    String key = tokens[2];
    String value = tokens.length > 3 ? tokens[3] : null;

    switch (operation.toUpperCase()) {
      case "PUT":
        return putRequest(requestID, key, value);
      case "GET":
        return getRequest(requestID, key);
      case "GETALL":
        return getAllRequest(requestID);
      case "DELETE":
        return deleteRequest(requestID, key);
      case "DELETEALL":
        return deleteAllRequest(requestID);
      default:
        return requestID + "== Unsupported operation== " + operation;
    }
  }

  protected String getRequest(String requestID, String key) throws IOException {
    String getValue = keyValueStore.get(key);
    //System.out.println("value = " + getValue);

    if(getValue != null) {
      String keyPresent = requestID + "==Value for key==" + key + "==" + getValue;
      return keyPresent;
    }
    else return requestID + "==Key==" + key + " not found";
  }

protected String getAllRequest(String requestID) throws IOException {
  // StringBuilder responseBuilder = new StringBuilder();
  // responseBuilder.append(requestID).append(":-"); // Start with a newline for better formatting

  // // Get all keys using keySet()
  // Set<String> allKeys = keyValueStore.getAllKeys();

  // if (allKeys.isEmpty()) {
  //   return requestID + ": Key value store is empty";
  // }

  // for (String key : allKeys) {
  //   String value = keyValueStore.get(key);
  //   responseBuilder.append(key).append(" : ").append(value); // Append key-value pair on a newline
  // }

  // return responseBuilder.toString(); // Return the complete response with newlines
  return keyValueStore.toString();
}


  protected String putRequest(String requestID, String key, String value) throws IOException {
      if (value == null) {
        return requestID + "==Please enter a value for key==" + key;
      }
      keyValueStore.put(key, value);
      String successMessage = requestID + "==Key== " + key + "stored with value==" + value + "";
      System.out.println("successMessage = "  + successMessage);
      return successMessage;
  }

  protected String deleteRequest(String requestID, String key) throws IOException {
      String value = keyValueStore.delete(key);
      if(value !=  null){ 
        String deletedKeyValue = requestID + "==Deleted key==" + key + " with value==" + value + "";
        return deletedKeyValue;
      }
      else return requestID + "==Key== " + key + " not found";
  }

  protected String deleteAllRequest(String requestID) throws IOException {
    keyValueStore.deleteAll();
    return requestID + "== All key-value pairs deleted successfully";
  }

}
