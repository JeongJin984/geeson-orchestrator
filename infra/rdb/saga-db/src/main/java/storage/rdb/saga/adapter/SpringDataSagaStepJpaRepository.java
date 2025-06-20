package storage.rdb.saga.adapter;

import org.springframework.data.jpa.repository.JpaRepository;
import saga.common.storage.entity.SagaStepEntity;

public interface SpringDataSagaStepJpaRepository extends JpaRepository<SagaStepEntity, String> {
}
