package storage.rdb.saga.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import saga.common.storage.entity.SagaStepEntity;
import saga.common.storage.repository.SagaStepRepository;
import storage.rdb.saga.repository.adapter.SpringDataSagaStepJpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SagaStepJpaRepository implements SagaStepRepository {
    private final SpringDataSagaStepJpaRepository sagaStepJpaRepository;

    @Override
    public SagaStepEntity save(SagaStepEntity sagaStepEntity) {
        return sagaStepJpaRepository.save(sagaStepEntity);
    }

    @Override
    public Optional<SagaStepEntity> findById(String sagaStepId) {
        return sagaStepJpaRepository.findById(sagaStepId);
    }

    @Override
    public List<SagaStepEntity> findBySagaInstanceId(String sagaInstanceId) {
        return sagaStepJpaRepository.findBySagaInstance_Id(sagaInstanceId);
    }
}
