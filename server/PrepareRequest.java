package server;


public class PrepareRequest {
    private final int round;
    private final String key;
    private final String value; // null for delete

    public PrepareRequest(int round, String key, String value) {
        this.round = round;
        this.key = key;
        this.value = value;
    }

    public int getRound() {
        return round;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}