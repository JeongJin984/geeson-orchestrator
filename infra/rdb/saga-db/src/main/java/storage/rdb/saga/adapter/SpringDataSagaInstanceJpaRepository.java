package storage.rdb.saga.adapter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import saga.common.storage.entity.SagaInstanceEntity;

import java.util.Optional;

public interface SpringDataSagaInstanceJpaRepository extends JpaRepository<SagaInstanceEntity, String> {
    @Query("SELECT si, ss FROM SagaInstanceEntity si join fetch SagaStepEntity ss WHERE si.id = :instanceId order by ss.executionOrder asc")
    Optional<SagaInstanceEntity> findByIdWithOrderedSteps(String instanceId);
}
