package storage.rdb.saga.repository.adapter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saga.common.storage.entity.SagaStepEntity;
import java.util.List;

@Repository
public interface SpringDataSagaStepJpaRepository extends JpaRepository<SagaStepEntity, String> {
    List<SagaStepEntity> findBySagaInstance_Id(String sagaInstanceId);
}
