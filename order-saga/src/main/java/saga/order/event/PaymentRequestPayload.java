package saga.order.event;

import lombok.Builder;

import java.math.BigDecimal;

public record PaymentRequestPayload (
    String customerId,
    String orderId,
    String paymentMethodId,
    String transactionId,
    BigDecimal totalPrice,
    String currency
) {

}
