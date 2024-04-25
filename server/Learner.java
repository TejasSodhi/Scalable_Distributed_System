package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Learner extends Remote{
    void learn(LearnRequest request) throws RemoteException;
}