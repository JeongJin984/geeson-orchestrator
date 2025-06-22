package saga.order.event;

import java.math.BigDecimal;
import java.util.List;

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
