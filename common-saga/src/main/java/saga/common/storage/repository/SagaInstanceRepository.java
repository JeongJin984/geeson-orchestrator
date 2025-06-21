package saga.common.storage.repository;

import saga.common.storage.entity.SagaInstanceEntity;

import java.util.Optional;

public interface SagaInstanceRepository {
    SagaInstanceEntity save(SagaInstanceEntity sagaInstanceEntity);
    Optional<SagaInstanceEntity> findByIdWithOrderedSteps(String sagaId);
}
