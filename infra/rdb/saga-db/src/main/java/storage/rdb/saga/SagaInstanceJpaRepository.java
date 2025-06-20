package storage.rdb.saga;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import saga.common.storage.entity.SagaInstanceEntity;
import saga.common.storage.repository.SagaInstanceRepository;
import storage.rdb.saga.adapter.SpringDataSagaInstanceJpaRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SagaInstanceJpaRepository implements SagaInstanceRepository {
    private final SpringDataSagaInstanceJpaRepository springDataSagaInstanceJpaRepository;

    @Override
    public SagaInstanceEntity save(SagaInstanceEntity sagaInstanceEntity) {
        return springDataSagaInstanceJpaRepository.save(sagaInstanceEntity);
    }

    @Override
    public Optional<SagaInstanceEntity> findById(String sagaId) {
        return springDataSagaInstanceJpaRepository.findById(sagaId);
    }
}
