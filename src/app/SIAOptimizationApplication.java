package src.web_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @class SIAOptimizationApplication
 * @description Clasa principală de lansare a aplicației Spring Boot (Modulul 3).
 */
@SpringBootApplication
// Asigură că Spring găsește și încarcă și clasele din 'neural_network' (Modulul 2)
@ComponentScan(basePackages = {"src.web_service", "src.neural_network"}) 
public class SIAOptimizationApplication {

    public static void main(String[] args) {
        SpringApplication.run(SIAOptimizationApplication.class, args);
        System.out.println("API RESTful disponibil pe: http://localhost:8080/api/v1/optimize/route");
        System.out.println("Configurarea Spring Boot: Include Modulul 2 (NeuralNetworkService) în context.");
    }
}