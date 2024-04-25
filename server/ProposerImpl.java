package server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.rmi.RemoteException;

import shared.KeyValueStoreService;


public class ProposerImpl implements Proposer {

    private final List<Acceptor> acceptors;
    private final List<Learner> learners;  // Added learner service
    private KeyValueStoreService server;
    private int highestAcceptedRound = -1;

    public ProposerImpl(List<Acceptor> acceptors, List<Learner> learners, KeyValueStoreService server) throws RemoteException {
        this.acceptors = acceptors;
        this.learners = learners;
        this.server = server;
    }

    @Override
    public String proposePut(String key, String value) throws RemoteException {
        int round = propose(key, value);
        if (round > 0) {
            notifyLearners(new LearnRequest(round, key, value));
            return "Success";
        } else {
            return "Failed to propose put";
        }
    }

    @Override
    public String proposeDelete(String key) throws RemoteException {
        int round = propose(key, null);
        if (round > 0) {
            notifyLearners(new LearnRequest(round, key, null));
            return "Success";
        } else {
            return "Failed to propose delete";
        }
    }

    private int propose(String key, String value) throws RemoteException {
        int round = nextRound();
        PrepareRequest prepareRequest = new PrepareRequest(round, key, value);
        Map<Acceptor, PrepareResponse> responses = sendPrepare(prepareRequest);

        int promisedRound = -1;
        String acceptedValue = null;
        for (PrepareResponse response : responses.values()) {
            if (response.isPromise()) {
                promisedRound = Math.max(promisedRound, response.getAcceptedRound());
                acceptedValue = response.getAcceptedValue();
            }
        }

        if (promisedRound >= round) {
            AcceptRequest acceptRequest = new AcceptRequest(round, key, value);
            sendAccept(acceptRequest, promisedRound, acceptedValue);
            return round;
        } else {
            return 0;
        }
    }

    private int nextRound() {
        long timestamp = System.currentTimeMillis();
        
        int currentProposerId = 0;
        currentProposerId++;
        
        int next = (int)(timestamp << 32) | currentProposerId;
        return next;
    }

    @Override
    public Map<Acceptor, PrepareResponse> sendPrepare(PrepareRequest request) throws RemoteException {
        Map<Acceptor, PrepareResponse> responses = new HashMap<>();
        for (Acceptor acceptor : acceptors) {
            responses.put(acceptor, acceptor.prepare(request));
        }
        return responses;
    }

    @Override
    public void sendAccept(AcceptRequest request, int promisedRound, String acceptedValue) throws RemoteException {
        for (Acceptor acceptor : acceptors) {
            acceptor.accept(new AcceptRequest(request.getRound(), request.getKey(), 
                    (acceptedValue != null && request.getRound() >= promisedRound) ? acceptedValue : request.getValue()));
        }
    }

    @Override
    public int getHighestAcceptedRound() {
        return highestAcceptedRound;
    }

    @Override
    public void setHighestAcceptedRound(int round) {
        this.highestAcceptedRound = round;
    }

    @Override
    public void notifyLearners(LearnRequest request) throws RemoteException {
        for (Learner learner : learners) {
            try {
                learner.learn(request);
            } catch (RemoteException e) {
                System.err.println("Failed to notify Learner " + learner + ": " + e.getMessage());
            }
        }
    }

    @Override
    public void commitPut(String key, String value) {
        try {
            server.commitPut(key, value);
        } catch (RemoteException e) {
            throw new RemoteException("Errors occurred during commit put");
        }
    }

    @Override
    public void commitDelete(String key) {
        try {
            server.commitDelete(key);
        } catch (RemoteException e) {
            throw new RemoteException("Errors occurred during commit delete");
        }
    }
}
