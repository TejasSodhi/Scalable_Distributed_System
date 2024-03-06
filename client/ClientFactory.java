package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.UUID;

public abstract class ClientFactory {

  public String generateRequest(BufferedReader userInput) throws IOException {
    System.out.println("The application can add keys and repective values, get all the values for a particular key and delete any particular key value");
    System.out.println("Which functionality do you want to use?");
    System.out.println("1. PUT");
    System.out.println("2. GET");
    System.out.println("3. GETALL");
    System.out.println("4. DELETE");
    System.out.println("5. DELETEALL");
    System.out.println("6. GETALL-FOR-UDP");
    System.out.print("Enter from the above options only (1/2/3/4/5): ");

    String option = userInput.readLine();
    switch (option) {
      case "1":
        return putRequest(userInput);
      case "2":
        return getRequest(userInput);
      case "3":
        return getAllRequest(userInput);
      case "4":
        return deleteRequest(userInput);
      case "5":
        return deleteAllRequest(userInput);
      case "6":
        return getAllRequest(userInput);
      default:
        System.out.println("Invalid choice. Please enter your choice between 1 and 5.");
        return null;
    }
  }

  protected String getRequest(BufferedReader userInput) throws IOException {
    String requestId = generateRequestId();
    System.out.print("Please enter the key (only integer values): ");
    String key = userInput.readLine();
    return requestId + "::" + "GET" + "::" + key;
  }

  protected String getAllRequest(BufferedReader userInput) throws IOException {
    String requestId = generateRequestId();
    //System.out.print("Please enter the key (only integer values): ");
    //String key = userInput.readLine();
    String key = "getAll";
    return requestId + "::" + "GETALL" + "::" + key;
  }

  protected String putRequest(BufferedReader userInput) throws IOException {
    String requestId = generateRequestId();
    System.out.print("Please enter the key: ");
    String key = userInput.readLine();
    System.out.print("Please enter the value for the key: ");
    String value = userInput.readLine();
    return requestId + "::" + "PUT" + "::" + key + "::" + value;
  }

  protected String deleteRequest(BufferedReader userInput) throws IOException {
    String requestId = generateRequestId();
    System.out.print("Please enter the key (integer): ");
    String key = userInput.readLine();
    return requestId + "::" + "DELETE" + "::" + key;
  }

  protected String deleteAllRequest(BufferedReader userInput) throws IOException {
  String requestId = generateRequestId();
  //System.out.print("Please enter the key (integer): ");
  String key = "deleteAll";
  return requestId + "::" + "DELETEALL" + "::" + key;
  }

  protected static String generateRequestId() {
    return UUID.randomUUID().toString();
  }
}
