package saga.order.event;

import lombok.Getter;
import support.uuid.UuidGenerator;

@Getter
public class OrderCompletePayload {
    private final String orderId;
    private final boolean success;

    public OrderCompletePayload(String orderId, boolean success) {
        this.orderId = orderId;
        this.success = success;
    }
}
