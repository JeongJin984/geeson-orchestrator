package saga.order.event;

public record OrderCancelEvent(
    String orderId
) {}
