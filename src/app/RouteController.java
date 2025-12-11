package src.web_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import src.neural_network.NeuralNetworkService;
import src.neural_network.NeuralNetworkService.OptimizationResult; // Import clasa ajutătoare
import src.web_service.dto.RouteRequest;
import src.web_service.dto.RouteResponse;

/**
 * @class RouteController
 * @description Expune funcționalitatea principală de optimizare rută printr-un API REST.
 */
@RestController 
@RequestMapping("/api/v1/optimize") 
public class RouteController {

    private final NeuralNetworkService neuralNetworkService;

    // Injectarea Modulului 2 (NeuralNetworkService)
    public RouteController(NeuralNetworkService neuralNetworkService) {
        // În Spring Boot, această clasă va fi inițializată automat de DI
        this.neuralNetworkService = neuralNetworkService;
    }

    /**
     * Endpoint POST: http://localhost:8080/api/v1/optimize/route
     * @param request Detaliile rutei în format JSON.
     * @return Răspunsul cu ruta optimă și ETA-ul total.
     */
    @PostMapping("/route")
    public ResponseEntity<RouteResponse> optimizeRoute(@RequestBody RouteRequest request) {
        
        System.out.println("-> Modul 3 API: Cerere de optimizare primită: " + request);
        
        if (request.getHubsToVisit() == null || request.getHubsToVisit().isEmpty()) {
            return ResponseEntity.badRequest().body(new RouteResponse(
                List.of(), 0.0, "ERROR: Lista de hub-uri nu poate fi goală."
            ));
        }

        try {
            // Apelarea logicii din Modulul 2 (LSTM + GA)
            OptimizationResult optimizationResult = neuralNetworkService.getOptimalRouteAndETA(
                request.getHubsToVisit(), 
                request.getVehicleCapacityKg(), 
                request.getContextData()
            );

            // Construirea răspunsului API
            RouteResponse response = new RouteResponse(
                optimizationResult.route, 
                optimizationResult.totalETA, 
                "SUCCESS: Ruta optimă a fost calculată dinamic (LSTM + GA)."
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Eroare în procesarea rutei: " + e.getMessage());
            
            RouteResponse errorResponse = new RouteResponse(
                List.of(), 
                0.0, 
                "ERROR: Eroare internă în timpul calculului. Detalii: " + e.getMessage()
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}