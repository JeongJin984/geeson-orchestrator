package saga.order.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import saga.order.entity.SagaState;
import saga.order.event.OrderRequest;
import saga.order.event.PaymentRequestEvent;
import saga.order.event.PaymentResultEvent;

import java.util.UUID;

@Service
public class SagaOrchestrator {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final SagaStateService sagaStateService;

    public SagaOrchestrator(KafkaTemplate<String, Object> kafkaTemplate, SagaStateService sagaStateService) {
        this.kafkaTemplate = kafkaTemplate;
        this.sagaStateService = sagaStateService;
    }

    public String startSaga(OrderRequest request) {
        String orderId = UUID.randomUUID().toString();
        sagaStateService.create(orderId, SagaState.Status.CREATED);

        PaymentRequestEvent event = new PaymentRequestEvent(orderId, request.getAmount());
        kafkaTemplate.send("payment-requests", orderId, event);
        sagaStateService.updateStatus(orderId, SagaState.Status.PAYMENT_REQUESTED);
        return orderId;
    }

    @KafkaListener(topics = "payment-results", groupId = "order-saga")
    public void handlePaymentResult(PaymentResultEvent event) {
        if (event.isSuccess()) {
            sagaStateService.updateStatus(event.getOrderId(), SagaState.Status.PAYMENT_SUCCEEDED);
            sagaStateService.updateStatus(event.getOrderId(), SagaState.Status.COMPLETED);
        } else {
            sagaStateService.updateStatus(event.getOrderId(), SagaState.Status.PAYMENT_FAILED);
            sagaStateService.updateStatus(event.getOrderId(), SagaState.Status.CANCELLED);
        }
    }
}
