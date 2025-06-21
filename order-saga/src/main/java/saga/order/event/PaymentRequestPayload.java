package saga.order.event;

import lombok.Builder;

import java.math.BigDecimal;

public class PaymentRequestPayload {
    private final Long paymentMethodId;
    private final String transactionId;
    private final BigDecimal totalPrice;
    private final String currency;

    @Builder
    public PaymentRequestPayload(Long paymentMethodId, String transactionId, BigDecimal totalPrice, String currency) {
        this.paymentMethodId = paymentMethodId;
        this.transactionId = transactionId;
        this.totalPrice = totalPrice;
        this.currency = currency;
    }
}
