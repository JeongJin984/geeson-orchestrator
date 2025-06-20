package saga.order.event;

public record PaymentResultEvent (
    String orderId,
    boolean success
) {
}
