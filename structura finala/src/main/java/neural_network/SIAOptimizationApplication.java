package neural_network;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"neural_network", "app"})
public class SIAOptimizationApplication {
    public static void main(String[] args) {
        SpringApplication.run(SIAOptimizationApplication.class, args);
    }
}