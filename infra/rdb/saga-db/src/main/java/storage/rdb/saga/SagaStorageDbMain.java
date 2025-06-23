package storage.rdb.saga;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
    scanBasePackages = {"storage.rdb.saga", "saga.common"}
)
public class SagaStorageDbMain {
}
