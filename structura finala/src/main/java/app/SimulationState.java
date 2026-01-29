package app;

import java.util.List;
import java.util.Map;

public class SimulationState {
    public List<Truck> trucks;
    public List<PackageItem> warehouseStock;
    public List<PackageItem> history;
    public String currentSimTime;

    // Date pentru Tabelul 4 (Inventar)
    public Map<String, Integer> oldStock;
    public Map<String, Integer> newArrivals;

    public SimulationState(List<Truck> trucks, List<PackageItem> stock, List<PackageItem> history,
                           Map<String, Integer> oldStock, Map<String, Integer> newArrivals,
                           String currentSimTime) {
        this.trucks = trucks;
        this.warehouseStock = stock;
        this.history = history;
        this.oldStock = oldStock;
        this.newArrivals = newArrivals;
        this.currentSimTime = currentSimTime;
    }
}