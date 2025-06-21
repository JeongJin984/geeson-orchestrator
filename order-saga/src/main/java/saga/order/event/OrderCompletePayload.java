package saga.order.event;

import lombok.Getter;

public record OrderCompletePayload(String orderId, boolean success) {
}
