package server;

public class PrepareResponse {
    private final int round;
    private final boolean promise;
    private final int acceptedRound;
    private final String acceptedValue;

    public PrepareResponse(int round, boolean promise, int acceptedRound, String acceptedValue) {
        this.round = round;
        this.promise = promise;
        this.acceptedRound = acceptedRound;
        this.acceptedValue = acceptedValue;
    }

        public int getRound() {
        return round;
    }

    public boolean isPromise() {
        return promise;
    }

    public int getAcceptedRound() {
        return acceptedRound;
    }

    public String getAcceptedValue() {
        return acceptedValue;
    }
}