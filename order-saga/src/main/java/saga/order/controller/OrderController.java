package saga.order.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import saga.order.event.OrderRequest;
import saga.order.service.SagaOrchestrator;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final SagaOrchestrator orchestrator;

    public OrderController(SagaOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createOrder(@RequestBody OrderRequest request) {
        String orderId = orchestrator.startSaga(request);
        return ResponseEntity.ok(Map.of("orderId", orderId));
    }
}
