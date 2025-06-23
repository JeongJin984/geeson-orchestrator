package saga.order.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderCreated (
    String orderId,
    String customerId,
    String paymentMethodId,
    String transactionId,
    BigDecimal totalPrice,
    String currency,
    List<OrderItem> items
) {
    public record OrderItem(
        String productId,
        int quantity,
        int unitPrice
    ) {}
}
