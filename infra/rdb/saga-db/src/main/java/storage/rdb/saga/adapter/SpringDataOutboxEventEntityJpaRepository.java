package storage.rdb.saga.adapter;

import org.springframework.data.jpa.repository.JpaRepository;
import saga.common.storage.entity.OutboxEventEntity;
import java.util.List;

public interface SpringDataOutboxEventEntityJpaRepository extends JpaRepository<OutboxEventEntity, String> {
    List<OutboxEventEntity> findByStatus(OutboxEventEntity.EventStatus status);
}
