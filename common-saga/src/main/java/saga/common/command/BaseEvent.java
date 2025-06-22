package saga.common.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import support.uuid.UuidGenerator;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public class BaseEvent {
        private final String eventId;
        private final String eventType;
        private final String aggregateId;
        private final String aggregateType;
        private final String occurredAt;
        private final String source;
        private final String sagaId;
        private final String stepId;
        private final String message;
        private final Map<String, String> metadata;

        public BaseEvent(String eventId, String aggregateId, String aggregateType, String source, String message) {
            this.eventId = eventId;
            this.aggregateId = aggregateId;
            this.aggregateType = aggregateType;
            this.occurredAt = LocalDateTime.now().toString();
            this.source = source;
            this.sagaId = null;   // 필요한 경우 setter 또는 생성자 추가
            this.stepId = null;
            this.eventType = this.getClass().getSimpleName(); // or 명시적 설정
            this.message = message;
            this.metadata = new HashMap<>();
        }
}
