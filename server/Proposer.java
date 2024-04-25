package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface Proposer extends Remote{
    String proposePut(String key, String value) throws RemoteException;
    String proposeDelete(String key) throws RemoteException;
    void notifyLearners(LearnRequest request) throws RemoteException;
    void commitPut(String key, String value);
    void commitDelete(String key);
    Map<Acceptor, PrepareResponse> sendPrepare(PrepareRequest request) throws RemoteException;
    void sendAccept(AcceptRequest request, int promisedRound, String acceptedValue) throws RemoteException;
    int getHighestAcceptedRound();
    void setHighestAcceptedRound(int round); 
}