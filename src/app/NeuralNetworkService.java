package src.neural_network;

import java.util.List;

/**
 * @class NeuralNetworkService
 * @description Punctul de intrare pentru Modulul 2, integrând LSTM și GA.
 * Această clasă este injectată în Modulul 3 (API RESTful).
 */
public class NeuralNetworkService {

    private final LSTMPredictionModel lstmModel;
    private final RouteOptimizerGA routeOptimizer;

    public NeuralNetworkService() {
        this.lstmModel = new LSTMPredictionModel();
        this.lstmModel.buildModel(); 
        this.routeOptimizer = new RouteOptimizerGA(this.lstmModel);
    }

    // Clasă ajutătoare pentru rezultat, utilizată și de Modulul 3
    public static class OptimizationResult {
        public List<String> route;
        public double totalETA;

        public OptimizationResult(List<String> route, double totalETA) {
            this.route = route;
            this.totalETA = totalETA;
        }
    }

    /**
     * Primește o cerere de rutare și returnează ruta optimă și ETA-ul.
     */
    public OptimizationResult getOptimalRouteAndETA(List<String> hubsToVisit, int vehicleConstraints, Object externalDynamicData) {
        
        // Etapa 1 & 2: Predicție și Optimizare (GA folosește intern LSTM)
        List<String> optimalRoute = routeOptimizer.calculateOptimalRoute(hubsToVisit, vehicleConstraints, externalDynamicData);

        // Etapa 3: Calcul final ETA pe rută optimă (Simulare)
        double totalDistanceFactor = optimalRoute.size() - 1;
        double finalETA = lstmModel.predictETT(optimalRoute.get(0), optimalRoute.get(optimalRoute.size() - 1), externalDynamicData) * totalDistanceFactor * 1.5; 

        System.out.println(" RUTA OPTIMĂ CALCULATĂ: " + optimalRoute);
        System.out.println(" ETA TOTAL PREZIS: " + String.format("%.2f", finalETA) + " minute.");
        
        return new OptimizationResult(optimalRoute, finalETA);
    }
}