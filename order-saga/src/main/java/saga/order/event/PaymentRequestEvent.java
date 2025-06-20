package saga.order.event;

import java.math.BigDecimal;

public record PaymentRequestEvent (
    Long paymentMethodId,
    String transactionId,
    BigDecimal totalPrice,
    String currency
) {
}
