package saga.common.command;

import lombok.Getter;
import support.uuid.UuidGenerator;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
public class BaseCommand {
    private final String commandId;
    private final String sagaId;
    private final String stepId;
    private final String aggregateId;
    private final String timestamp;
    private final String source;
    private final Map<String, String> metadata;

    public BaseCommand(String commandId, String sagaId, String stepId, String aggregateId, String source) {
        this.commandId = commandId;
        this.sagaId = sagaId;
        this.stepId = stepId;
        this.aggregateId = aggregateId;
        this.timestamp = LocalDateTime.now().toString();
        this.source = source;
        this.metadata = new HashMap<>();
    }
}
