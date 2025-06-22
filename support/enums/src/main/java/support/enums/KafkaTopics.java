package support.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum KafkaTopics {
    PAYMENT_REQUEST_COMMAND("payment-request-command", "payment", "request", "command"),
    PAYMENT_REQUEST_EVENT("payment-request-event", "payment", "request", "event"),

    PAYMENT_COMPLETE_COMMAND("payment-complete-command", "payment", "complete", "command"),
    PAYMENT_COMPLETE_EVENT("payment-complete-event", "payment", "complete", "event"),

    PAYMENT_REJECTED_COMMAND("payment-rejected-command", "payment", "rejected", "command"),
    PAYMENT_REJECTED_EVENT("payment-rejected-event", "payment", "rejected", "event"),

    ORDER_REQUEST_COMMAND("order-request-command", "order", "request", "command"),
    ORDER_REQUEST_EVENT("order-request-event", "order", "request", "event"),

    ORDER_COMPLETE_COMMAND("order-complete-command", "order", "complete", "command"),
    ORDER_COMPLETE_EVENT("order-complete-event", "order", "complete", "event"),
    ;

    private final String topicName;
    private final String aggregateType;
    private final String eventType;
    private final String intentType;

    public static KafkaTopics getKafkaTopic(final String aggregateType, final String eventType, final String intentType) {
        return Arrays.stream(KafkaTopics.values())
            .filter(topic ->
                topic.aggregateType.equals(aggregateType)
                    && topic.eventType.equals(eventType)
                    && topic.intentType.equals(intentType)
            )
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No such topic"));
    }
}
