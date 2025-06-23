package storage.rdb.saga.repository.adapter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saga.common.storage.entity.OutboxEventEntity;
import java.util.List;

@Repository
public interface SpringDataOutboxEventEntityJpaRepository extends JpaRepository<OutboxEventEntity, String> {
    List<OutboxEventEntity> findByStatus(OutboxEventEntity.EventStatus status);
}
