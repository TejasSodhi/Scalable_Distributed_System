package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The KeyValueStoreService interface defines the remote methods that a key-value store
 * service must implement to provide CRUD (Create, Read, Update, Delete) operations
 * over a distributed network. It extends the java.rmi.Remote interface to enable remote
 * method invocation.
 */
public interface KeyValueStoreService extends Remote {

    /**
     * Stores a key-value pair in the key-value store.
     * @param key The key of the key-value pair.
     * @param value The value associated with the key.
     * @return A status message indicating the outcome of the operation.
     * @throws RemoteException If a communication-related error occurs during the method invocation.
     */
    String put(String key, String value) throws RemoteException;

    /**
     * Retrieves the value associated with the specified key from the key-value store.
     * @param key The key whose associated value is to be retrieved.
     * @return The value associated with the key, or an error message if the key is not found.
     * @throws RemoteException If a communication-related error occurs during the method invocation.
     */
    String get(String key) throws RemoteException;

    /**
     * Deletes the key-value pair with the specified key from the key-value store.
     * @param key The key of the key-value pair to be deleted.
     * @return A status message indicating the outcome of the operation.
     * @throws RemoteException If a communication-related error occurs during the method invocation.
     */
    String delete(String key) throws RemoteException;

    /**
     * Retrieves all key-value pairs stored in the key-value store.
     * @return A string containing all key-value pairs, or a message indicating if the store is empty.
     * @throws RemoteException If a communication-related error occurs during the method invocation.
     */
    String getAll() throws RemoteException;

    /**
     * Deletes all key-value pairs stored in the key-value store.
     * @return A status message indicating the outcome of the operation.
     * @throws RemoteException If a communication-related error occurs during the method invocation.
     */
    String deleteAll() throws RemoteException;

    /**
     * Initiates the prepare phase for a PUT operation with the specified key and value.
     * @param key The key of the key-value pair to be stored.
     * @param value The value associated with the key.
     * @return A status message indicating the outcome of the prepare phase.
     * @throws RemoteException If a communication-related error occurs during the method invocation.
     */
    String preparePut(String key, String value) throws RemoteException;

    /**
     * Initiates the prepare phase for a DELETE operation with the specified key.
     * @param key The key of the key-value pair to be deleted.
     * @return A status message indicating the outcome of the prepare phase.
     * @throws RemoteException If a communication-related error occurs during the method invocation.
     */
    String prepareDelete(String key) throws RemoteException;

    /**
     * Commits a previously prepared PUT operation with the specified key and value.
     * @param key The key of the key-value pair to be committed.
     * @param value The value associated with the key.
     * @throws RemoteException If a communication-related error occurs during the method invocation.
     */
    void commitPut(String key, String value) throws RemoteException;

    /**
     * Commits a previously prepared DELETE operation with the specified key.
     * @param key The key of the key-value pair to be deleted.
     * @throws RemoteException If a communication-related error occurs during the method invocation.
     */
    void commitDelete(String key) throws RemoteException;

    /**
    * Rolls back a previously prepared PUT
    * @param key The key of the key-value pair to be aborted.
    * @throws RemoteException If a communication-related error occurs during the method invocation.
    */
    void abort(String key) throws RemoteException;
}
