package saga.order.event;

import lombok.Builder;

import java.math.BigDecimal;

public record PaymentRequestPayload (
    Long paymentMethodId,
    String transactionId,
    BigDecimal totalPrice,
    String currency
) {

}
