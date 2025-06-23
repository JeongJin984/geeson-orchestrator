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
public class BaseEvent {
        private String eventId;
        private String eventType;
        private String aggregateId;
        private String aggregateType;
        private String occurredAt;
        private String source;
        private String sagaId;
        private String stepId;
        private String message;
        private Map<String, String> metadata;

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
