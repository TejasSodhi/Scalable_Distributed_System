package server.paxos;

public class AcceptRequest {
    private final long round;
    private final String key;
    private final String value; // null for delete

    public AcceptRequest(long round, String key, String value) {
        this.round = round;
        this.key = key;
        this.value = value;
    }

    public long getRound() {
        return round;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}