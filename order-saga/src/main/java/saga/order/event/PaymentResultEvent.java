package saga.order.event;

import lombok.Getter;
import support.uuid.UuidGenerator;

@Getter
public class PaymentResultEvent extends BaseEvent {
    private final String orderId;
    private final boolean success;

    public PaymentResultEvent(UuidGenerator uuidGenerator, String aggregateId, String aggregateType, String source, String message, String orderId, boolean success) {
        super(uuidGenerator, aggregateId, aggregateType, source, message);
        this.orderId = orderId;
        this.success = success;
    }
}
