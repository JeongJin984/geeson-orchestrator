package storage.rdb.saga.repository.adapter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import saga.common.storage.entity.SagaInstanceEntity;

import java.util.Optional;

@Repository
public interface SpringDataSagaInstanceJpaRepository extends JpaRepository<SagaInstanceEntity, String> {
    @Query("SELECT DISTINCT si FROM SagaInstanceEntity si left join fetch si.sagaSteps ss WHERE si.id = :instanceId order by ss.executionOrder asc limit 1")
    Optional<SagaInstanceEntity> findByIdWithOrderedSteps(String instanceId);
}
