package saga.order.event;

import java.math.BigDecimal;

public class PaymentRequestEvent {
    private String orderId;
    private BigDecimal amount;

    public PaymentRequestEvent() {
    }

    public PaymentRequestEvent(String orderId, BigDecimal amount) {
        this.orderId = orderId;
        this.amount = amount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
