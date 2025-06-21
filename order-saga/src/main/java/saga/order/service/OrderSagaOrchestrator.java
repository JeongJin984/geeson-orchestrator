package saga.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saga.common.command.Command;
import saga.common.storage.entity.SagaInstanceEntity;
import saga.common.storage.entity.SagaStepEntity;
import saga.common.storage.repository.SagaInstanceRepository;
import saga.common.storage.repository.SagaStepRepository;
import saga.common.storage.service.CommandGateway;
import saga.order.event.*;
import support.uuid.UuidGenerator;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderSagaOrchestrator {
    private final CommandGateway commandGateway;
    private final SagaStepRepository sagaStepRepository;
    private final UuidGenerator uuidGenerator;
    private final OrderSagaCompensate orderSagaCompensate;
    private final SagaInstanceRepository sagaInstanceRepository;

    public String startSaga(OrderCreated request) {
        try {
            SagaInstanceEntity sagaInstance = commandGateway.createSagaInstance(
                    "OrderSaga", request
            );

            startOrderSaga(request, sagaInstance);

            return sagaInstance.getId();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "order-saga.payment.event", groupId = "order-saga")
    public void handlePaymentResult(PaymentResultEvent event) {
        try {
            if(event.isSuccess()) {
                SagaInstanceEntity sagaInstance = sagaInstanceRepository.findByIdWithOrderedSteps(event.getSagaId())
                    .orElseThrow(() -> new IllegalArgumentException("Saga instance not found: " + event.getSagaId()));

                commandGateway.handleStepResponse(event.getStepId(), event, event.isSuccess());
                orderCompleteStep(
                    new OrderCompletePayload(
                        event.getOrderId(),
                        true
                    ),
                    sagaInstance
                );
            } else {
                // mark the original payment step as failed
                commandGateway.handleStepResponse(event.getStepId(), event, !event.isSuccess());
                orderSagaCompensate.compensate(event.getSagaId(), event.getEventId(), event.getMessage());
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void startOrderSaga(OrderCreated request, SagaInstanceEntity sagaInstance) throws JsonProcessingException {
        String paymentAggregateId = String.valueOf(uuidGenerator.nextId());
        String stepId = String.valueOf(uuidGenerator.nextId());

        SagaStepEntity sagaStep = commandGateway.createSagaStep(
            sagaInstance,
            "paymentRequest",
            SagaStepEntity.StepType.FORWARD,
            1,
            new Command<>(
                new PaymentRequestPayload(
                    request.paymentMethodId(),
                    request.transactionId(),
                    request.totalPrice(),
                    request.currency()
                )
            ),
            paymentAggregateId,
            "payment",
            null
        );

        commandGateway.executeSagaStep(
            sagaStep,
            "payment",
            paymentAggregateId,
            "requests"
        );
    }

    private void orderCompleteStep(OrderCompletePayload orderCompletePayload, SagaInstanceEntity sagaInstance) throws JsonProcessingException {
        SagaStepEntity sagaStep = commandGateway.createSagaStep(
            sagaInstance,
            "paymentRequest",
            SagaStepEntity.StepType.FORWARD,
            1,
            new Command<>(
                new OrderCompletePayload(
                    orderCompletePayload.getOrderId(),
                    true
                )
            ),
            orderCompletePayload.getOrderId(),
            "order",
            null
        );

        commandGateway.executeSagaStep(
            sagaStep,
            "order",
            orderCompletePayload.getOrderId(),
            "complete"
        );
    }
}
