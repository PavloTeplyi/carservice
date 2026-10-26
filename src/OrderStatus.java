
public enum OrderStatus {

    CREATED,
    DIAGNOSED,
    APPROVED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED;

    public boolean canTransitionTo(OrderStatus next) {
        switch (this) {
            case CREATED:
                return next == DIAGNOSED || next == CANCELLED;
            case DIAGNOSED:
                return next == APPROVED || next == CANCELLED;
            case APPROVED:
                return next == IN_PROGRESS || next == CANCELLED;
            case IN_PROGRESS:
                return next == COMPLETED;
            default:
                return false;
        }
    }

    public boolean isClosed() {
        return this == COMPLETED || this == CANCELLED;
    }
}
