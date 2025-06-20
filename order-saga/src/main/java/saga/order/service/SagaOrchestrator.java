package saga.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import saga.common.storage.entity.SagaInstanceEntity;
import saga.common.storage.entity.SagaStepEntity;
import saga.common.storage.repository.SagaStepRepository;
import saga.common.storage.service.CommandGateway;
import saga.order.event.OrderCreated;
import saga.order.event.OrderCancelEvent;
import saga.order.event.PaymentFailEvent;
import saga.order.event.PaymentRequestEvent;
import saga.order.event.PaymentResultEvent;

@Service
@RequiredArgsConstructor
public class SagaOrchestrator {
    private final CommandGateway commandGateway;
    private final SagaStepRepository sagaStepRepository;

    public String startSaga(OrderCreated request) {
        try {
            SagaInstanceEntity sagaInstance = commandGateway.createSagaInstance(
                    "OrderSaga", request
            );

            PaymentRequestEvent paymentRequest = new PaymentRequestEvent(
                    request.paymentMethodId(),
                    request.transactionId(),
                    request.totalPrice(),
                    request.currency()
            );

            SagaStepEntity sagaStep = commandGateway.createSagaStep(
                    sagaInstance,
                    "paymentRequest",
                    SagaStepEntity.StepType.FORWARD,
                    1,
                    paymentRequest,
                    null
            );

            commandGateway.executeSagaStep(
                    sagaStep,
                    "payment",
                    String.valueOf(request.paymentMethodId()),
                    "requests"
            );

            return sagaInstance.getId();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "payment-results", groupId = "order-saga")
    public void handlePaymentResult(PaymentResultEvent event) {
        try {
            commandGateway.handleStepResponse(event.orderId(), event, event.success());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "payment-fail", groupId = "order-saga")
    public void handlePaymentFail(PaymentFailEvent event) {
        try {
            // mark the original payment step as failed
            commandGateway.handleStepResponse(event.sagaStepId(), event, false);

            SagaStepEntity failedStep = sagaStepRepository.findById(event.sagaStepId())
                    .orElseThrow(() -> new IllegalArgumentException("Saga step not found: " + event.sagaStepId()));

            // create compensation step to cancel order
            OrderCancelEvent cancelEvent = new OrderCancelEvent(event.sagaStepId());
            SagaStepEntity compensationStep = commandGateway.createSagaStep(
                    failedStep.getSagaInstance(),
                    "cancelOrder",
                    SagaStepEntity.StepType.COMPENSATION,
                    failedStep.getExecutionOrder() + 1,
                    cancelEvent,
                    failedStep.getId()
            );

            commandGateway.executeSagaStep(
                    compensationStep,
                    "order",
                    event.sagaStepId(),
                    "cancel"
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
