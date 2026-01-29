package app;

import java.util.List;


public class RouteRequest {
    
    private List<String> hubsToVisit;
    private int vehicleCapacityKg;
    private String contextData;
    private double currentLat;
    private double currentLng;
    private String destinationHub;

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

    // Getter și Setter pentru Latitudine
    public double getCurrentLat() { return currentLat; }
    public void setCurrentLat(double currentLat) { this.currentLat = currentLat; }

    // Getter și Setter pentru Longitudine
    public double getCurrentLng() { return currentLng; }
    public void setCurrentLng(double currentLng) { this.currentLng = currentLng; }

    // Getter și Setter pentru Destinație
    public String getDestinationHub() { return destinationHub; }
    public void setDestinationHub(String destinationHub) { this.destinationHub = destinationHub; }

    @Override
    public String toString() {
        return "RouteRequest{hubs=" + hubsToVisit + ", capacity=" + vehicleCapacityKg + ", context=" + contextData + "}";
    }
}