package app;

import java.util.ArrayList;
import java.util.List;

public class Truck {
    public String truckId;
    public String currentLocation;
    public String targetHub;
    public boolean inTransit;
    public int remainingTime; // ETA
    public List<PackageItem> cargo = new ArrayList<>();

    public Truck(String id, String location) {
        this.truckId = id;
        this.currentLocation = location;
        this.inTransit = false;
    }

    public void load(List<PackageItem> packages) {
        this.cargo.addAll(packages);
        this.inTransit = true;
    }

    public List<PackageItem> unload() {
        List<PackageItem> deliveredCargo = new ArrayList<>(this.cargo);
        this.currentLocation = this.targetHub;
        this.targetHub = null;
        this.inTransit = false;
        this.remainingTime = 0;
        this.cargo.clear();
        return deliveredCargo;
    }

    public boolean isInTransit() { return inTransit; }
}