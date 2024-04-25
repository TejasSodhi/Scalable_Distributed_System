package server.paxos;

public class AcceptResponse {
    private final long round;
    private final boolean accepted;

    public AcceptResponse(long round, boolean accepted) {
        this.round = round;
        this.accepted = accepted;
    }

    public long getRound() {
        return round;
    }

    public boolean isAccepted() {
        return accepted;
    }
}