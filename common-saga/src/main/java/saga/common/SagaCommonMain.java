package saga.common;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication(scanBasePackages = {"saga.common"})
public class SagaCommonMain {
    public static void main(String[] args) { run(SagaCommonMain.class, args); }
}
