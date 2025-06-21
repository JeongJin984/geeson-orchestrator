package saga.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import saga.common.command.Command;
import saga.common.storage.entity.SagaInstanceEntity;
import saga.common.storage.entity.SagaStepEntity;
import saga.common.storage.repository.SagaInstanceRepository;
import saga.common.storage.repository.SagaStepRepository;
import saga.common.storage.service.CommandGateway;
import saga.order.event.CompensatePayload;
import support.uuid.UuidGenerator;

@Service
@RequiredArgsConstructor
public class OrderSagaCompensate {
    private final SagaInstanceRepository sagaInstanceRepository;
    private final SagaStepRepository sagaStepRepository;
    private final CommandGateway commandGateway;
    private final UuidGenerator uuidGenerator;

    public void compensate(final String sagaId, final String triggerEventId, final String reason) throws JsonProcessingException {
        SagaInstanceEntity sagaInstance = sagaInstanceRepository.findByIdWithOrderedSteps(sagaId)
            .orElseThrow(() -> new IllegalArgumentException("Saga instance not found: " + sagaId));

        for(SagaStepEntity step : sagaInstance.getSagaSteps()) {
            if(step.getStatus() == SagaStepEntity.StepStatus.DONE) {
                SagaStepEntity compensationStep = commandGateway.createSagaStep(
                    step.getSagaInstance(),
                    "cancelOrder",
                    SagaStepEntity.StepType.COMPENSATION,
                    step.getExecutionOrder() + 1,
                    new Command<>(
                        new CompensatePayload(
                            step.getAggregateId(),
                            step.getAggregateType(),
                            triggerEventId,
                            reason
                        )
                    ),
                    step.getAggregateId(),
                    step.getAggregateType(),
                    step
                );

                commandGateway.executeSagaStep(
                    compensationStep,
                    "order",
                    step.getAggregateId(),
                    "cancel"
                );
            } else if(step.getStatus() == SagaStepEntity.StepStatus.PENDING || step.getStatus() == SagaStepEntity.StepStatus.IN_PROGRESS) {
                // TODO
            } else {
                throw new IllegalArgumentException("The saga step status is not valid: " + step.getStatus());
            }
        }
    }

    public void compensate(final String sagaId, final String stepId, final String triggerEventId, final String reason) throws JsonProcessingException {
        SagaInstanceEntity sagaInstance = sagaInstanceRepository.findByIdWithOrderedSteps(sagaId)
            .orElseThrow(() -> new IllegalArgumentException("Saga instance not found: " + sagaId));
        SagaStepEntity step = sagaInstance.getSagaSteps().stream().filter(v -> v.getId().equals(stepId)).findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Saga step not found: " + stepId));

        if (step.getStatus() == SagaStepEntity.StepStatus.DONE) {
            SagaStepEntity compensationStep = commandGateway.createSagaStep(
                step.getSagaInstance(),
                "cancelOrder",
                SagaStepEntity.StepType.COMPENSATION,
                step.getExecutionOrder() + 1,
                new Command<>(
                    new CompensatePayload(
                        step.getAggregateId(),
                        step.getAggregateType(),
                        triggerEventId,
                        reason
                    )
                ),
                step.getAggregateId(),
                step.getAggregateType(),
                step
            );

            commandGateway.executeSagaStep(
                compensationStep,
                "order",
                step.getAggregateId(),
                "cancel"
            );
        } else if(step.getStatus() == SagaStepEntity.StepStatus.PENDING || step.getStatus() == SagaStepEntity.StepStatus.IN_PROGRESS) {
            // TODO
        } else {
            throw new IllegalArgumentException("The saga step status is not valid: " + step.getStatus());
        }
    }
}
