package saga.common.storage.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saga.common.command.BaseCommand;
import saga.common.command.Command;
import saga.common.storage.entity.SagaInstanceEntity;
import saga.common.storage.entity.SagaStepEntity;
import saga.common.storage.repository.SagaInstanceRepository;
import saga.common.storage.repository.SagaStepRepository;
import support.uuid.UuidGenerator;

import java.time.LocalDateTime;

/**
 * CommandGateway is responsible for sending commands to services and managing saga steps.
 * It provides methods to create and execute saga steps, and to handle responses.
 */
@Service
@RequiredArgsConstructor
public class CommandGateway {

    private final SagaInstanceRepository sagaInstanceRepository;
    private final SagaStepRepository sagaStepRepository;
    private final ObjectMapper objectMapper;
    private final UuidGenerator uuidGenerator;
    private final EventPublisher eventPublisher;

    /**
     * Creates a new saga instance.
     *
     * @param sagaType The type of saga
     * @param context The context data for the saga
     * @return The created saga instance
     */
    @Transactional
    public SagaInstanceEntity createSagaInstance(String sagaType, Object context) throws JsonProcessingException {
        String contextJson = objectMapper.writeValueAsString(context);

        SagaInstanceEntity sagaInstance = SagaInstanceEntity.builder()
                .id(String.valueOf(uuidGenerator.nextId()))
                .sagaType(sagaType)
                .status(SagaInstanceEntity.SagaStatus.PENDING)
                .context(contextJson)
                .build();

        return sagaInstanceRepository.save(sagaInstance);
    }

    /**
     * Creates a new saga step.
     *
     * @param sagaInstance The saga instance
     * @param stepName The name of the step
     * @param stepType The type of step (FORWARD or COMPENSATION)
     * @param executionOrder The order in which the step should be executed
     * @param command The command to be executed
     * @param orgStep Original step that this step compensates (for compensation steps)
     * @return The created saga step
     */
    @Transactional
    public <T> SagaStepEntity createSagaStep(
        SagaInstanceEntity sagaInstance,
        String stepName,
        SagaStepEntity.StepType stepType,
        int executionOrder,
        Command<T> command,
        String aggregateId,
        String aggregateType,
        SagaStepEntity orgStep
    ) throws JsonProcessingException {
        String stepId = String.valueOf(uuidGenerator.nextId());

        command.setBaseCommand(
            new BaseCommand(
                String.valueOf(uuidGenerator.nextId()),
                sagaInstance.getId(),
                stepId,
                aggregateId,
                "orchestrator"
            )
        );

        String commandJson = objectMapper.writeValueAsString(command);

        SagaStepEntity sagaStep = SagaStepEntity.builder()
            .id(stepId)
            .sagaInstance(sagaInstance)
            .stepName(stepName)
            .stepType(stepType)
            .status(SagaStepEntity.StepStatus.PENDING)
            .executionOrder(executionOrder)
            .aggregateId(aggregateId)
            .aggregateType(aggregateType)
            .command(commandJson)
            .build();

        if (orgStep != null && stepType == SagaStepEntity.StepType.COMPENSATION) {
            sagaStep.setCompensatesStep(orgStep);
        }

        return sagaStepRepository.save(sagaStep);
    }

    /**
     * Executes a saga step by publishing an event with the command.
     *
     * @param sagaStep The saga step to execute
     * @param aggregateType The type of aggregate
     * @param aggregateId The ID of the aggregate
     * @param eventType The type of event
     * @throws JsonProcessingException If there's an error processing JSON
     */
    @Transactional
    public void executeSagaStep(
            SagaStepEntity sagaStep,
            String aggregateType,
            String aggregateId,
            String eventType
    ) throws JsonProcessingException {

        // Update step status to IN_PROGRESS
        sagaStep.setStatus(SagaStepEntity.StepStatus.IN_PROGRESS);
        sagaStep.setStartedAt(LocalDateTime.now());
        sagaStepRepository.save(sagaStep);

        // Publish event with command
        eventPublisher.publishEvent(
                aggregateType,
                aggregateId,
                "command",
                eventType,
                sagaStep.getCommand()
        );
    }

    /**
     * Handles a response to a saga step.
     *
     * @param sagaStepId The ID of the saga step
     * @param response The response data
     * @param success Whether the step was successful
     * @throws JsonProcessingException If there's an error processing JSON
     */
    @Transactional
    public void handleStepResponse(String sagaStepId, Object response, boolean success) throws JsonProcessingException {
        SagaStepEntity sagaStep = sagaStepRepository.findById(sagaStepId)
                .orElseThrow(() -> new IllegalArgumentException("Saga step not found: " + sagaStepId));

        String responseJson = objectMapper.writeValueAsString(response);
        sagaStep.setEventResponse(responseJson);
        sagaStep.setEndedAt(LocalDateTime.now());

        if (success) {
            if (sagaStep.getStepType() == SagaStepEntity.StepType.FORWARD) {
                sagaStep.setStatus(SagaStepEntity.StepStatus.DONE);
            } else {
                sagaStep.setStatus(SagaStepEntity.StepStatus.COMPENSATED);
            }
        } else {
            sagaStep.setStatus(SagaStepEntity.StepStatus.FAILED);
        }

        sagaStepRepository.save(sagaStep);

        // Update saga instance status
        updateSagaInstanceStatus(sagaStep.getSagaInstance().getId());
    }

    /**
     * Updates the status of a saga instance based on its steps.
     *
     * @param sagaInstanceId The ID of the saga instance
     */
    @Transactional
    public void updateSagaInstanceStatus(String sagaInstanceId) {
        SagaInstanceEntity sagaInstance = sagaInstanceRepository.findByIdWithOrderedSteps(sagaInstanceId)
                .orElseThrow(() -> new IllegalArgumentException("Saga instance not found: " + sagaInstanceId));

        // Get all steps for this saga instance
        Iterable<SagaStepEntity> steps = sagaStepRepository.findBySagaInstanceId(sagaInstanceId);

        boolean allStepsDone = true;
        boolean anyStepFailed = false;
        boolean anyStepInProgress = false;
        boolean allCompensationDone = true;

        for (SagaStepEntity step : steps) {
            if (step.getStepType() == SagaStepEntity.StepType.FORWARD) {
                if (step.getStatus() != SagaStepEntity.StepStatus.DONE) {
                    allStepsDone = false;
                }
                if (step.getStatus() == SagaStepEntity.StepStatus.FAILED) {
                    anyStepFailed = true;
                }
                if (step.getStatus() == SagaStepEntity.StepStatus.IN_PROGRESS) {
                    anyStepInProgress = true;
                }
            } else if (step.getStepType() == SagaStepEntity.StepType.COMPENSATION) {
                if (step.getStatus() != SagaStepEntity.StepStatus.COMPENSATED) {
                    allCompensationDone = false;
                }
                if (step.getStatus() == SagaStepEntity.StepStatus.IN_PROGRESS) {
                    anyStepInProgress = true;
                }
            }
        }

        if (anyStepInProgress) {
            sagaInstance.updateStatus(SagaInstanceEntity.SagaStatus.IN_PROGRESS);
        } else if (allStepsDone) {
            sagaInstance.updateStatus(SagaInstanceEntity.SagaStatus.COMPLETED);
        } else if (anyStepFailed && allCompensationDone) {
            sagaInstance.updateStatus(SagaInstanceEntity.SagaStatus.COMPENSATED);
        } else if (anyStepFailed) {
            sagaInstance.updateStatus(SagaInstanceEntity.SagaStatus.FAILED);
        }

        sagaInstanceRepository.save(sagaInstance);
    }
}
