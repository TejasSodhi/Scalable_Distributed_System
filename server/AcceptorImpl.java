package server;

import java.rmi.RemoteException;
import java.util.Random;


public class AcceptorImpl implements Acceptor, Runnable {

    private final int failureRate; // Percentage chance of failure (0-100)
    private final Random random;
    private int promisedRound = -1;
    private String acceptedValue = null;

    public AcceptorImpl(int failureRate) {
        this.failureRate = failureRate;
        this.random = new Random();
    }

    @Override
    public PrepareResponse prepare(PrepareRequest request) throws RemoteException {
        // Simulate failure
        if (random.nextInt(100) < failureRate) {
            System.out.println("Acceptor failed to respond to prepare request.");
            throw new RemoteException("Simulated Acceptor Failure");
        }

        int round = request.getRound();
        if (round > promisedRound) {
            promisedRound = round;
            acceptedValue = request.getValue();
            return new PrepareResponse(round, true, promisedRound, acceptedValue);
        } else {
            return new PrepareResponse(round, false, promisedRound, acceptedValue);
        }
    }

    @Override
    public AcceptResponse accept(AcceptRequest request) throws RemoteException {
        // Simulate failure (less likely for accept)
        if (random.nextInt(100) < failureRate / 2) {
            System.out.println("Acceptor failed to respond to accept request.");
            throw new RemoteException("Simulated Acceptor Failure");
        }

        int round = request.getRound();
        if (round >= promisedRound) {
            promisedRound = round;
            acceptedValue = request.getValue();
            return new AcceptResponse(round, true);
        } else {
            return new AcceptResponse(round, false);
        }
    }
}


