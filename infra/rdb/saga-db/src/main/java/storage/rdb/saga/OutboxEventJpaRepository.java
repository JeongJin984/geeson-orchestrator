package storage.rdb.saga;

import org.springframework.stereotype.Repository;
import saga.common.storage.repository.OutboxEventRepository;

@Repository
public class OutboxEventJpaRepository implements OutboxEventRepository {
}
