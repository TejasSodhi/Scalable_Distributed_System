package server.paxos;

import java.rmi.RemoteException;
import java.util.Random;


public class AcceptorImpl implements Acceptor {

    private final int failureRate; // Percentage chance of failure (0-100)
    private final Random random;
    private long promisedRound = -1;
    private String acceptedValue = null;
    private boolean active;

    public AcceptorImpl(int failureRate) {
        this.failureRate = failureRate;
        this.random = new Random();
        this.active = true;
    }

    @Override
    public PrepareResponse prepare(PrepareRequest request) throws RemoteException {
        // Simulate failure
        System.out.println("failure rate inside prepare= " + failureRate);
        int randomNumber = random.nextInt(100);
        System.out.println("random number inside prepare= " + randomNumber);
        if (randomNumber < failureRate) {
            System.out.println("Acceptor failed to respond to prepare request.");
            return new PrepareResponse(request.getRound(), false, promisedRound, acceptedValue);
        }

        long round = request.getRound();
        System.out.println("request round inside prepare = " + round);
        System.out.println("promised round inside prepare = " + promisedRound);  
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
        System.out.println("failure rate inside accept= " + failureRate);
        int randomNumber = random.nextInt(100);
        System.out.println("random number inside accept= " + randomNumber);
        if (randomNumber < failureRate / 2) {
            System.out.println("Acceptor failed to respond to accept request.");
            return new AcceptResponse(request.getRound(), false);
        }

        long round = request.getRound();
        System.out.println("request round inside accept = " + round);
        System.out.println("promised round inside accept = " + promisedRound); 
        if (round >= promisedRound) {
            promisedRound = round;
            acceptedValue = request.getValue();
            return new AcceptResponse(round, true);
        } else {
            return new AcceptResponse(round, false);
        }
    }

    @Override
    public boolean isActive() {
        return active;
    }
}


