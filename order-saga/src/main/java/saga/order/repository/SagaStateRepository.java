package saga.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import saga.order.entity.SagaState;

import java.util.Optional;

public interface SagaStateRepository extends JpaRepository<SagaState, Long> {
    Optional<SagaState> findByOrderId(String orderId);
}
