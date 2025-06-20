# Order-Payment Saga Orchestrator

This project implements a saga orchestration pattern for order-payment processing using Kafka for event-driven communication and MySQL for state storage.

## Architecture

The saga orchestrator coordinates the following flow:

1. Order is created and an event is published to Kafka
2. Saga orchestrator receives the order creation event and initiates the saga
3. Payment is requested via Kafka
4. Payment service processes the payment and publishes success/failure event
5. Saga orchestrator receives the payment result and completes or cancels the order
6. The state of each saga is stored in MySQL for durability and recovery

## Prerequisites

- Java 21
- MySQL 8.0
- Kafka

## Configuration

The application is configured in `application.yml`:

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
  datasource:
    url: jdbc:mysql://localhost:3306/saga_db?createDatabaseIfNotExist=true
    username: root
    password: root
```

## Running the Application

1. Start MySQL and Kafka
2. Run the application:
   ```
   ./gradlew bootRun
   ```

## Testing the Saga

You can test the saga by creating an order using the REST API:

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "customer123",
    "amount": 100.00,
    "productId": "product456",
    "quantity": 1
  }'
```

The saga orchestrator will:
1. Create a new saga state in the database
2. Request payment via Kafka
3. Process the payment result (success/failure)
4. Complete or cancel the order accordingly
5. Update the saga state in the database

## Monitoring

You can monitor the saga state in the MySQL database:

```sql
SELECT * FROM saga_states;
```

## Components

- `SagaOrchestrator`: Coordinates the saga and handles state transitions
- `SagaState`: Entity for storing saga state in the database
- `SagaStateService`: Service for managing saga state
- `OrderService`: Handles order-related operations
- `PaymentService`: Handles payment-related operations
