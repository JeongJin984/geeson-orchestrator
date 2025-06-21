package saga.order.event;

import lombok.Getter;
import saga.common.command.BaseCommand;

@Getter
public class CompensatePayload {
    private final String aggregateId;
    private final String aggregateType;
    private final String triggeringEventId;
    private final String reason;

    public CompensatePayload(String aggregateId, String aggregateType, String triggeringEventId, String reason) {
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.triggeringEventId = triggeringEventId;
        this.reason = reason;
    }
}
