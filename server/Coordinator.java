package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The Coordinator interface defines the remote methods that a coordinator node
 * must implement to manage distributed transactions using the Two-Phase Commit (2PC) protocol.
 * It extends the java.rmi.Remote interface to enable remote method invocation.
 */
public interface Coordinator extends Remote {

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
     * Aborts a transaction by rolling back any changes associated with the specified key and value.
     * @param key The key of the key-value pair involved in the transaction.
     * @param value The value associated with the key (null for DELETE operations).
     * @throws RemoteException If a communication-related error occurs during the method invocation.
     */
    void abort(String key, String value) throws RemoteException;
}
