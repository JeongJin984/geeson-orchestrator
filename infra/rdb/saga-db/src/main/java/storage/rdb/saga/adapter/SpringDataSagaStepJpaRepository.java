package storage.rdb.saga.adapter;

import org.springframework.data.jpa.repository.JpaRepository;
import saga.common.storage.entity.SagaStepEntity;
import java.util.List;

public interface SpringDataSagaStepJpaRepository extends JpaRepository<SagaStepEntity, String> {
    List<SagaStepEntity> findBySagaInstance_Id(String sagaInstanceId);
}
