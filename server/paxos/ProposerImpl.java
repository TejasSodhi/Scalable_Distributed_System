package server.paxos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.rmi.RemoteException;

import shared.KeyValueStoreService;


public class ProposerImpl implements Proposer {

    private final List<Acceptor> acceptors;
    private final List<Learner> learners;  // Added learner service
    private List<KeyValueStoreService> servers;
    private long highestAcceptedRound = -1;

    public ProposerImpl(List<Acceptor> acceptors, List<Learner> learners, List<KeyValueStoreService> servers) throws RemoteException {
        this.acceptors = acceptors;
        this.learners = learners;
        this.servers = servers;
    }

    @Override
    public String proposePut(String key, String value) throws RemoteException {
        long round = propose(key, value);
        if (round > 0) {
            notifyLearners(new LearnRequest(round, key, value));
            return "Success";
        } else {
            return "Failed to propose put";
        }
    }

    @Override
    public String proposeDelete(String key) throws RemoteException {
        long round = propose(key, null);
        if (round > 0) {
            notifyLearners(new LearnRequest(round, key, null));
            return "Success";
        } else {
            return "Failed to propose delete";
        }
    }

    private long propose(String key, String value) throws RemoteException {
        System.out.println("Inside propose");
        long round = nextRound();
        System.out.println("round value of proposer = " + round);
        PrepareRequest prepareRequest = new PrepareRequest(round, key, value);
        Map<Acceptor, PrepareResponse> responses = sendPrepare(prepareRequest);

        long promisedRound = -1;
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

    private long nextRound() {
        long timestamp = System.currentTimeMillis();
        System.out.println("Timestamp = " + timestamp);
        
        // int currentProposerId = 0;
        // currentProposerId++;
        
        // int next = (int)(timestamp << 32) | currentProposerId;
        return timestamp;
    }

    @Override
    public Map<Acceptor, PrepareResponse> sendPrepare(PrepareRequest request) throws RemoteException {
        Map<Acceptor, PrepareResponse> responses = new HashMap<>();
        for (Acceptor acceptor : acceptors) {
            responses.put(acceptor, acceptor.prepare(request));
        }
        System.out.println("Prepare response = " + responses);
        return responses;
    }

    @Override
    public void sendAccept(AcceptRequest request, long promisedRound, String acceptedValue) throws RemoteException {
        for (Acceptor acceptor : acceptors) {
            acceptor.accept(new AcceptRequest(request.getRound(), request.getKey(), 
                    (acceptedValue != null && request.getRound() >= promisedRound) ? acceptedValue : request.getValue()));
        }
    }

    @Override
    public long getHighestAcceptedRound() {
        return highestAcceptedRound;
    }

    @Override
    public void setHighestAcceptedRound(long round) {
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
        // Traverse serverList and call commitPut on each server
        for (KeyValueStoreService server : servers) {
            try {
                server.commitPut(key, value);
            } catch (RemoteException e) {
                System.err.println("Failed to commit put on server " + server + ": " + e.getMessage());
            }
        }
    }

    @Override
    public void commitDelete(String key) {
        // Traverse serverList and call commitDelete on each server
        for (KeyValueStoreService server : servers) {
            try {
                server.commitDelete(key);
            } catch (RemoteException e) {
                System.err.println("Failed to commit delete on server " + server + ": " + e.getMessage());
            }
        }
    }
}
