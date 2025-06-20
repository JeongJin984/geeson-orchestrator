package saga.order.event;

public record PaymentFailEvent(
    String sagaStepId
) {}
