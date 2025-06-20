package saga.order.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saga.order.entity.SagaState;
import saga.order.repository.SagaStateRepository;

import java.util.Optional;

@Service
public class SagaStateService {

    private final SagaStateRepository repository;

    public SagaStateService(SagaStateRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public SagaState create(String orderId, SagaState.Status status) {
        SagaState state = new SagaState();
        state.setOrderId(orderId);
        state.setStatus(status);
        return repository.save(state);
    }

    @Transactional
    public void updateStatus(String orderId, SagaState.Status status) {
        Optional<SagaState> optional = repository.findByOrderId(orderId);
        optional.ifPresent(state -> {
            state.setStatus(status);
            repository.save(state);
        });
    }
}
