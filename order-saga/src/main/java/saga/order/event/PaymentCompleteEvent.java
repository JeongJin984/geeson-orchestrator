package saga.order.event;

public record PaymentCompleteEvent(
    String paymentId,
    String orderId
) {
}
