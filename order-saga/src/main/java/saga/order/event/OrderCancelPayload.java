package saga.order.event;

import lombok.Getter;

@Getter
public class OrderCancelPayload {
    private final String orderId;

    public OrderCancelPayload(String orderId) {
        this.orderId = orderId;
    }
}
