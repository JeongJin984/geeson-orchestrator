package saga.common.storage.repository;

import saga.common.storage.entity.OutboxEventEntity;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for OutboxEventEntity.
 */
public interface OutboxEventRepository {

    /**
     * Saves an outbox event.
     *
     * @param event The event to save
     * @return The saved event
     */
    OutboxEventEntity save(OutboxEventEntity event);

    /**
     * Finds an outbox event by its ID.
     *
     * @param id The ID of the event
     * @return The event, if found
     */
    Optional<OutboxEventEntity> findById(String id);

    /**
     * Finds outbox events by their status.
     *
     * @param status The status of the events
     * @return The events with the given status
     */
    List<OutboxEventEntity> findByStatus(OutboxEventEntity.EventStatus status);

    /**
     * Finds all outbox events.
     *
     * @return All outbox events
     */
    List<OutboxEventEntity> findAll();

    /**
     * Deletes an outbox event.
     *
     * @param event The event to delete
     */
    void delete(OutboxEventEntity event);
}
