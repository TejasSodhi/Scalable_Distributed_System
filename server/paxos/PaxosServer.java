package server.paxos;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


import shared.KeyValueStoreService;

public class PaxosServer {
    public static void main(String[] args) {
        try {
            int basePort = Integer.parseInt(args[0]);

            // Initialize Proposers and Acceptors
            List<Proposer> proposers = new ArrayList<>();
            List<Acceptor> acceptors = new ArrayList<>();
            List<Learner> learners = new ArrayList<>();
            //List<Thread> acceptorThreads = new ArrayList<>();
            List<KeyValueStoreService> serverList = new ArrayList<>();

            //boolean isProposer = true;

            int numServers = 5;
            Registry registry = null;

            Proposer proposer = new ProposerImpl(acceptors, learners, serverList);

            for (int i = 0; i < numServers; i++) {
                int failureRate = 10;
                int port = basePort + i;
                //isProposer = (i == 0); // First server acts as Proposer, rest as Acceptors
                // Proposer proposer = new ProposerImpl(acceptors, learners, serverList);
                Acceptor acceptor = new AcceptorImpl(failureRate);
                Learner learner = new LearnerImpl(proposer);

                // if (isProposer) {
                //     proposers.add(proposer);
                // } else {
                //     acceptors.add(acceptor);
                //     learners.add(learner);
                // }

                acceptors.add(acceptor);
                learners.add(learner);

                // Start RMI registry and bind KeyValueStoreService
                registry = LocateRegistry.createRegistry(port);
                KeyValueStoreImpl_Paxos server = new KeyValueStoreImpl_Paxos(proposer);
                serverList.add(server);
                KeyValueStoreService stub = (KeyValueStoreService) UnicastRemoteObject.exportObject(server, 0);
                registry.rebind("KeyValueStoreService" + (i+1), stub);
            }

            // Start acceptor monitor thread
            // int restartDelay = 5000; // Restart delay in milliseconds
            // ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

            // AcceptorMonitor monitor = new AcceptorMonitor(acceptors, restartDelay, scheduler);
            // Thread monitorThread = new Thread(monitor);
            // monitorThread.start(); // Start the acceptor monitor thread

            System.out.println("Servers connected and ready.");
        } catch (RemoteException e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
