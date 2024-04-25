package server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import shared.KeyValueStoreService;

public class PaxosServer {
    public static void main(String[] args) {
        try {
            int basePort = Integer.parseInt(args[0]);

            // Initialize Proposers and Acceptors
            List<Proposer> proposers = new ArrayList<>();
            List<Acceptor> acceptors = new ArrayList<>();
            List<Learner> learners = new ArrayList<>();
            List<Thread> acceptorThreads = new ArrayList<>();
            List<KeyValueStoreService> serverList = new ArrayList<>();

            boolean isProposer = true;

            int numServers = 5;
            for (int i = 0; i < numServers; i++) {
                int failureRate = 10;
                int port = basePort + i;
                //boolean isProposer = (i == 0); // First server acts as Proposer, rest as Acceptors
                Proposer proposer = new ProposerImpl(acceptors, learners, serverList);
                Acceptor acceptor = new AcceptorImpl(failureRate);
                Learner learner = new LearnerImpl(proposer);

                if (isProposer) {
                    proposers.add(proposer);
                } else {
                    acceptors.add(acceptor);
                    learners.add(learner);
                    Thread acceptorThread = new Thread(acceptor);
                    acceptorThread.start(); // Start the acceptor thread
                    acceptorThreads.add(acceptorThread);
                }

                // Start RMI registry and bind KeyValueStoreService
                Registry registry = LocateRegistry.createRegistry(port);
                KeyValueStoreImpl_Paxos server = new KeyValueStoreImpl_Paxos(proposer);
                serverList.add(server);
                KeyValueStoreService stub = (KeyValueStoreService) UnicastRemoteObject.exportObject(server, 0);
                registry.rebind("KeyValueStoreService", stub);
            }

            // Start acceptor monitor thread
            int restartDelay = 5000; // Restart delay in milliseconds
            AcceptorMonitor monitor = new AcceptorMonitor(acceptorThreads, restartDelay);
            Thread monitorThread = new Thread(monitor);
            monitorThread.start(); // Start the acceptor monitor thread

            System.out.println("Servers connected and ready.");
        } catch (RemoteException e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
