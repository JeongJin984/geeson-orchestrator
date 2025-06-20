package storage.rdb.saga.adapter;

import org.springframework.data.jpa.repository.JpaRepository;
import saga.common.storage.entity.SagaInstanceEntity;

public interface SpringDataSagaInstanceJpaRepository extends JpaRepository<SagaInstanceEntity, String> {
}
