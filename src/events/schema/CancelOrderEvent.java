package events.schema;

public class CancelOrderEvent {
    private String userId;
    private Reason reason;

    public CancelOrderEvent() {
    }

    public CancelOrderEvent(String userId, Reason reason) {
        this.userId = userId;
        this.reason= reason;
    }

    public String getUserId() {
        return userId;
    }

    public Reason getReason() {
        return reason;
    }

    public static enum Reason {
        WRONG_PRODUCT, DELIVERY, FAULTY_PRODUCT
    }
}