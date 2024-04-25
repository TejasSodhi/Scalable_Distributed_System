package server.paxos;

public class PrepareResponse {
    private final long round;
    private final boolean promise;
    private final long acceptedRound;
    private final String acceptedValue;

    public PrepareResponse(long round, boolean promise, long acceptedRound, String acceptedValue) {
        this.round = round;
        this.promise = promise;
        this.acceptedRound = acceptedRound;
        this.acceptedValue = acceptedValue;
    }

    public long getRound() {
        return round;
    }

    public boolean isPromise() {
        return promise;
    }

    public long getAcceptedRound() {
        return acceptedRound;
    }

    public String getAcceptedValue() {
        return acceptedValue;
    }
}