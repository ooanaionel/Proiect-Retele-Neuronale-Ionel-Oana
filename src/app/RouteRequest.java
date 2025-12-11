package src.web_service.dto;

import java.util.List;

/**
 * @class RouteRequest
 * @description DTO pentru datele de intrare ale API-ului de optimizare.
 */
public class RouteRequest {
    
    private List<String> hubsToVisit;
    private int vehicleCapacityKg;
    private String contextData; 

    // Constructor, Getters și Setters (necesare pentru Spring Jackson/JSON Mapping)

    public List<String> getHubsToVisit() {
        return hubsToVisit;
    }

    public void setHubsToVisit(List<String> hubsToVisit) {
        this.hubsToVisit = hubsToVisit;
    }

    public int getVehicleCapacityKg() {
        return vehicleCapacityKg;
    }

    public void setVehicleCapacityKg(int vehicleCapacityKg) {
        this.vehicleCapacityKg = vehicleCapacityKg;
    }

    public String getContextData() {
        return contextData;
    }

    public void setContextData(String contextData) {
        this.contextData = contextData;
    }
    
    @Override
    public String toString() {
        return "RouteRequest{hubs=" + hubsToVisit + ", capacity=" + vehicleCapacityKg + ", context=" + contextData + "}";
    }
}