package saga.common.storage.repository;

import saga.common.storage.entity.SagaStepEntity;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for SagaStepEntity.
 */
public interface SagaStepRepository {
    /**
     * Saves a saga step.
     *
     * @param sagaStepEntity The saga step to save
     * @return The saved saga step
     */
    SagaStepEntity save(SagaStepEntity sagaStepEntity);

    /**
     * Finds a saga step by its ID.
     *
     * @param sagaStepId The ID of the saga step
     * @return The saga step, if found
     */
    Optional<SagaStepEntity> findById(String sagaStepId);

    /**
     * Finds all saga steps for a saga instance.
     *
     * @param sagaInstanceId The ID of the saga instance
     * @return The saga steps for the saga instance
     */
    List<SagaStepEntity> findBySagaInstanceId(String sagaInstanceId);
}
