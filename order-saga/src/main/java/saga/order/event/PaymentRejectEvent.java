package saga.order.event;

public record PaymentRejectEvent(
    String paymentId,
    String orderId,
    String message
) {
}
