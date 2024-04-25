package server.paxos;

import java.rmi.RemoteException;


public class LearnerImpl implements Learner {

    private final Proposer proposer;

    public LearnerImpl(Proposer proposer) throws RemoteException {
        this.proposer = proposer;
    }

    @Override
    public void learn(LearnRequest request) throws RemoteException {
        long round = request.getRound();
        String key = request.getKey();
        String value = request.getValue();

        if (round > proposer.getHighestAcceptedRound()) {
            proposer.setHighestAcceptedRound(round);
            if (value != null) {
                proposer.commitPut(key, value);
            } else {
                proposer.commitDelete(key);
            }
        }
    }
}
