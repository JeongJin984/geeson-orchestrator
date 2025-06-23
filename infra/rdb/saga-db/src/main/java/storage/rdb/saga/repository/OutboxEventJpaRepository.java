package storage.rdb.saga.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import saga.common.storage.entity.OutboxEventEntity;
import saga.common.storage.repository.OutboxEventRepository;
import storage.rdb.saga.repository.adapter.SpringDataOutboxEventEntityJpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OutboxEventJpaRepository implements OutboxEventRepository {

    private final SpringDataOutboxEventEntityJpaRepository repository;

    @Override
    public OutboxEventEntity save(OutboxEventEntity event) {
        return repository.save(event);
    }

    @Override
    public Optional<OutboxEventEntity> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public List<OutboxEventEntity> findByStatus(OutboxEventEntity.EventStatus status) {
        return repository.findByStatus(status);
    }

    @Override
    public List<OutboxEventEntity> findAll() {
        return repository.findAll();
    }

    @Override
    public void delete(OutboxEventEntity event) {
        repository.delete(event);
    }
}
