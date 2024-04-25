package server;

public class AcceptResponse {
    private final int round;
    private final boolean accepted;

    public AcceptResponse(int round, boolean accepted) {
        this.round = round;
        this.accepted = accepted;
    }

    public int getRound() {
        return round;
    }

    public boolean isAccepted() {
        return accepted;
    }
}