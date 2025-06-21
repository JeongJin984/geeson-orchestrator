package saga.order.event;

import lombok.Getter;

@Getter
public record CompensatePayload(String aggregateId, String aggregateType, String triggeringEventId, String reason) {
}
