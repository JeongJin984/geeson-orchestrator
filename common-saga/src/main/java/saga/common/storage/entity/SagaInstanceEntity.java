package saga.common.storage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "saga_instance", schema = "saga_db")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SagaInstanceEntity {

    @Id
    private String id;

    @Column(name = "saga_type", nullable = false)
    private String sagaType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SagaStatus status;

    @Column(columnDefinition = "json")
    private String context;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "sagaInstance")
    private List<SagaStepEntity> sagaSteps = new ArrayList<>();

    public enum SagaStatus {
        PENDING,        // Saga 시작 대기
        IN_PROGRESS,    // 일부 단계 수행 중
        COMPLETED,      // 전체 Saga 성공
        FAILED,         // 일부 단계 실패
        COMPENSATED     // 보상 로직까지 성공적으로 완료
    }

    public void updateStatus(final SagaStatus newStatus) {
        this.status = newStatus;
    }
}
