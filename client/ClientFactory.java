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
    System.out.println("3. DELETE");
    System.out.print("Enter from the above options only (1/2/3): ");

    String option = userInput.readLine();
    switch (option) {
      case "1":
        return putRequest(userInput);
      case "2":
        return getRequest(userInput);
      case "3":
        return deleteRequest(userInput);
      default:
        System.out.println("Invalid choice. Please enter 1, 2, or 3.");
        return null;
    }
  }

   protected abstract String getRequest(BufferedReader userInput) throws IOException {
    String requestId = generateRequestId();
    System.out.print("Please enter the key (only integer values): ");
    String key = userInput.readLine();
    return requestId + "::" + "GET" + "::" + key;
  }

   protected abstract String putRequest(BufferedReader userInput) throws IOException {
    String requestId = generateRequestId();
    System.out.print("Please enter the key: ");
    String key = userInput.readLine();
    System.out.print("Please enter the value for the key: ");
    String value = userInput.readLine();
    return requestId + "::" + "PUT" + "::" + key + "::" + value;
  }

   protected abstract String deleteRequest(BufferedReader userInput) throws IOException {
    String requestId = generateRequestId();
    System.out.print("Please enter the key (integer): ");
    String key = userInput.readLine();
    return requestId + "::" + "DELETE" + "::" + key;
  }

  protected String generateRequestId() {
    return UUID.randomUUID().toString();
  }
}
