package src.web_service.dto;

import java.util.List;

/**
 * @class RouteResponse
 * @description DTO pentru datele de ieșire ale API-ului de optimizare.
 */
public class RouteResponse {
    
    private final List<String> optimalRoute;
    private final double totalEstimatedTimeMinutes;
    private final String status;

    public RouteResponse(List<String> optimalRoute, double totalEstimatedTimeMinutes, String status) {
        this.optimalRoute = optimalRoute;
        this.totalEstimatedTimeMinutes = totalEstimatedTimeMinutes;
        this.status = status;
    }

    // Getters

    public List<String> getOptimalRoute() {
        return optimalRoute;
    }

    public double getTotalEstimatedTimeMinutes() {
        return totalEstimatedTimeMinutes;
    }

    public String getStatus() {
        return status;
    }
}