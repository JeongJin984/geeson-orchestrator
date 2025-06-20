package saga.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@SpringBootApplication
@ComponentScan(basePackages = {"saga.order", "storage.rdb.saga", "saga.common"})
public class OrderSagaOrchestratorApp {
    public static void main(String[] args) {
        SpringApplication.run(OrderSagaOrchestratorApp.class, args);
    }
}
