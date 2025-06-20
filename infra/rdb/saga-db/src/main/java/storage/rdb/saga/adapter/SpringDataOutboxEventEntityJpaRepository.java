package storage.rdb.saga.adapter;

import org.springframework.data.jpa.repository.JpaRepository;
import saga.common.storage.entity.OutboxEventEntity;

public interface SpringDataOutboxEventEntityJpaRepository extends JpaRepository<OutboxEventEntity, String> {
}
