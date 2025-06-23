package saga.common.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import support.uuid.UuidGenerator;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BaseCommand {
    private String commandId;
    private String sagaId;
    private String stepId;
    private String aggregateId;
    private String timestamp;
    private String source;
    private Map<String, String> metadata;

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
