package server.paxos;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Acceptor extends Remote {
    PrepareResponse prepare(PrepareRequest request) throws RemoteException;
    AcceptResponse accept(AcceptRequest request) throws RemoteException;
    boolean isActive();
}