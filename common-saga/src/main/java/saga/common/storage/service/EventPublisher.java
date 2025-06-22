package saga.common.storage.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saga.common.storage.entity.OutboxEventEntity;
import saga.common.storage.repository.OutboxEventRepository;
import support.enums.KafkaTopics;
import support.uuid.UuidGenerator;

import java.time.LocalDateTime;
import java.util.List;

/**
 * EventPublisher is responsible for publishing events from the outbox to Kafka or other message brokers.
 * It provides methods to create and publish events.
 */
@Service
@RequiredArgsConstructor
public class EventPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final UuidGenerator uuidGenerator;

    /**
     * Creates a new event in the outbox.
     *
     * @param aggregateType The type of aggregate
     * @param aggregateId The ID of the aggregate
     * @param eventType The type of event
     * @param payload The payload of the event
     * @return The created event
     * @throws JsonProcessingException If there's an error processing JSON
     */
    @Transactional
    public OutboxEventEntity createEvent(
            String aggregateType,
            String aggregateId,
            String eventType,
            Object payload) throws JsonProcessingException {

        String payloadJson = payload instanceof String ? (String) payload : objectMapper.writeValueAsString(payload);

        OutboxEventEntity event = OutboxEventEntity.builder()
                .id(String.valueOf(uuidGenerator.nextId()))
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .eventType(eventType)
                .payload(payloadJson)
                .status(OutboxEventEntity.EventStatus.PENDING)
                .build();

        return outboxEventRepository.save(event);
    }

    /**
     * Publishes an event to Kafka.
     *
     * @param aggregateType The type of aggregate
     * @param aggregateId The ID of the aggregate
     * @param eventType The type of event
     * @param payload The payload of the event
     * @return The published event
     * @throws JsonProcessingException If there's an error processing JSON
     */
    @Transactional
    public OutboxEventEntity publishEvent(
            String aggregateType,
            String aggregateId,
            String intentType,
            String eventType,
            Object payload
    ) throws JsonProcessingException {

        OutboxEventEntity event = createEvent(aggregateType, aggregateId, eventType, payload);

        // Publish to Kafka
        String topic = KafkaTopics.getKafkaTopic(aggregateType, eventType, intentType).getTopicName();
        kafkaTemplate.send(topic, aggregateId, event.getPayload());

        // Update event status
        event.setStatus(OutboxEventEntity.EventStatus.PUBLISHED);
        event.setPublishedAt(LocalDateTime.now());

        return outboxEventRepository.save(event);
    }

    /**
     * Publishes all pending events to Kafka.
     * This method can be called by a scheduled task to ensure all events are published.
     */
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEventEntity> pendingEvents = outboxEventRepository.findByStatus(OutboxEventEntity.EventStatus.PENDING);

        for (OutboxEventEntity event : pendingEvents) {
            try {
                String topic = event.getAggregateType().toLowerCase() + "-" + event.getEventType().toLowerCase();
                kafkaTemplate.send(topic, event.getAggregateId(), event.getPayload());

                event.setStatus(OutboxEventEntity.EventStatus.PUBLISHED);
                event.setPublishedAt(LocalDateTime.now());
                outboxEventRepository.save(event);
            } catch (Exception e) {
                event.setStatus(OutboxEventEntity.EventStatus.FAILED);
                outboxEventRepository.save(event);
            }
        }
    }

    /**
     * Retries failed events.
     * This method can be called by a scheduled task to retry failed events.
     */
    @Transactional
    public void retryFailedEvents() {
        List<OutboxEventEntity> failedEvents = outboxEventRepository.findByStatus(OutboxEventEntity.EventStatus.FAILED);

        for (OutboxEventEntity event : failedEvents) {
            try {
                String topic = event.getAggregateType().toLowerCase() + "-" + event.getEventType().toLowerCase();
                kafkaTemplate.send(topic, event.getAggregateId(), event.getPayload());

                event.setStatus(OutboxEventEntity.EventStatus.PUBLISHED);
                event.setPublishedAt(LocalDateTime.now());
                outboxEventRepository.save(event);
            } catch (Exception e) {
                // Keep status as FAILED
            }
        }
    }
}
