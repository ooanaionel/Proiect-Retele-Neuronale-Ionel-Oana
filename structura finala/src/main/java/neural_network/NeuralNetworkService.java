package neural_network;

import app.Truck;
import app.PackageItem;
import app.SimulationState;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NeuralNetworkService {

    private List<Truck> trucks = new ArrayList<>();
    private List<PackageItem> warehouseStock = new ArrayList<>();
    private List<PackageItem> history = new ArrayList<>();
    private final String DELIVERED_FILE_PATH = "data/delivered_packages.csv";
    private String currentSimTime = "2026-01-22 | Ora 18"; // Va fi actualizată de Node.js

    // Snapshot-uri pentru Tabelul 4
    private Map<String, Integer> oldStockSnapshot = new HashMap<>();
    private Map<String, Integer> currentStepArrivals = new HashMap<>();

    public static class OptimizationResult {
        public List<String> route;
        public double totalETA;
        public double distanceKm;
        public String weatherCondition;

        public OptimizationResult(List<String> route, double totalETA, double distanceKm, String weather) {
            this.route = route;
            this.totalETA = totalETA;
            this.distanceKm = distanceKm;
            this.weatherCondition = weather;
        }
    }

    private void loadSpecificFile(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine(); // Header

            while ((line = br.readLine()) != null) {
                String[] v = line.split(",");
                if (v.length < 4) continue;

                String currentAwb = v[0] + "/" + v[3];

                // Verificăm dacă există deja
                boolean exists = warehouseStock.stream().anyMatch(p -> p.awb.equals(currentAwb)) ||
                        history.stream().anyMatch(p -> p.awb.equals(currentAwb));

                if (!exists) {
                    PackageItem p = new PackageItem();
                    p.awb = currentAwb;
                    p.origin = v[1];
                    p.finalDestination = v[2];
                    p.currentLocation = v[1];

                    if (p.currentLocation.equals(p.finalDestination)) {
                        p.status = "LIVRAT";
                        history.add(p);
                    } else {
                        List<String> path = DijkstraRouter.getShortestPath(p.origin, p.finalDestination);
                        p.status = "Tranzit 1 din " + path.size();
                        warehouseStock.add(p);
                        currentStepArrivals.merge(p.origin, 1, Integer::sum);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void simulateInitialHistory() {
        File dataFolder = new File("data/generated");
        if (!dataFolder.exists()) return;

        // Luăm toate fișierele și le sortăm alfabetic/după nume (fiind numite cu H0, H6, H12, ele se sortează cronologic)
        File[] files = dataFolder.listFiles((dir, name) -> name.endsWith(".csv"));
        if (files == null || files.length == 0) return;

        Arrays.sort(files, Comparator.comparing(File::getName));

        System.out.println(" Pornire simulare istorică (48 ore)...");

        for (File f : files) {
            System.out.println(" Procesăm pasul: " + f.getName());

            // Snapshot stoc înainte de pas (pentru Tabelul 4)
            captureOldStockSnapshot();

            // Resetăm sosirile noi pentru acest fișier
            currentStepArrivals.replaceAll((k, v) -> 0);

            // Încărcăm datele din acest fișier specific
            loadSpecificFile(f);

            // Rulăm logica de mișcare (procesăm un pas de 6 ore)
            processStep();
        }

        // La finalul celor 8 fișiere, salvăm inventarul curent
        updateInventoryCSV();
        System.out.println(" Simulare inițială finalizată. Flota este acum distribuită în teren.");
    }

    @PostConstruct
    public void init() {

        String[] hubs = {"HUB_BUCURESTI", "HUB_OTOPENI", "HUB_PLOIESTI", "HUB_BRASOV", "HUB_SIBIU",
                "HUB_CLUJ", "HUB_ORADEA", "HUB_TIMISOARA", "HUB_ARAD", "HUB_CRAIOVA",
                "HUB_PITESTI", "HUB_CONSTANTA", "HUB_GALATI", "HUB_IASI", "HUB_BACAU"};

        for (String hubName : hubs) {
            for (int i = 1; i <= 5; i++) {
                trucks.add(new Truck("TRK-" + hubName + "-" + i, hubName));
            }
            oldStockSnapshot.put(hubName, 0);
            currentStepArrivals.put(hubName, 0);
        }

        simulateInitialHistory();
    }

    public void loadLatestData(String folderPath) {
        File dataFolder = new File(folderPath);
        if (!dataFolder.exists()) return;

        File[] files = dataFolder.listFiles((dir, name) -> name.endsWith(".csv"));
        if (files == null || files.length == 0) return;

        // Luăm cel mai nou fișier din folderul specificat
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
        File latestFile = files[0];

        System.out.println(" Citire date din: " + folderPath + "/" + latestFile.getName());
        //  Declarăm variabila AICI, la începutul metodei
        int loadedThisStep = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(latestFile))) {
            String line;
            br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                String[] v = line.split(",");
                if (v.length < 4) continue;

                String currentAwb = v[0] + "/" + v[3];

                boolean exists = warehouseStock.stream().anyMatch(p -> p.awb.equals(currentAwb)) ||
                        history.stream().anyMatch(p -> p.awb.equals(currentAwb));

                if (!exists) {
                    PackageItem p = new PackageItem();
                    p.awb = currentAwb;
                    p.origin = v[1];
                    p.finalDestination = v[2];
                    p.currentLocation = v[1]; // Locația inițială este originea
                    if (p.currentLocation.equals(p.finalDestination)) {
                        p.status = "LIVRAT";
                        history.add(p); // Merge direct în istoricul de livrări
                    } else {
                        // Dacă are de călătorit, calculăm runda de tranzit
                        List<String> path = DijkstraRouter.getShortestPath(p.origin, p.finalDestination);
                        p.status = "Tranzit 1 din " + path.size();
                        warehouseStock.add(p); // Merge în stocul de așteptare pentru camioane

                        // Contorizăm sosirea nouă pentru Tabelul 4
                        currentStepArrivals.merge(p.origin, 1, Integer::sum);
                    }
                    loadedThisStep++;
                }
            }
            //  Acum variabila este vizibilă aici
            System.out.println(" Am încărcat " + loadedThisStep + " colete noi.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runNextSimulationStep() {
        history.clear();

        try {
            System.out.println("\n[BUTON APĂSAT]  Pornire simulare pentru următorul pas de 6 ore...");
            System.out.println("Ora curentă a sistemului: " + new java.util.Date());
            System.out.println("\n[DEBUG] Lansare script Node.js...");


            captureOldStockSnapshot();

            ProcessBuilder pb = new ProcessBuilder("node", "src/data_acquisition/DataStepGenerator.js");
            Process p = pb.start();



            // Citim eventualele erori din Node.js în consola Java (foarte util pentru debug)
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("   [NODE]: " + line);

                // MODIFICARE: Verificăm dacă Node ne trimite timpul
                if (line.contains("DATA_SIMULARE:")) {
                    this.currentSimTime = line.split(":")[1].trim();
                }
            }

            p.waitFor();

            int exitCode = p.waitFor();
            if (exitCode == 0) {
                // Încărcăm din folderul de pași noi
                loadLatestDataFromFolder("data/simulation_steps");
                processStep();
            } else {
                System.err.println(" Scriptul Node.js a eșuat cu codul " + exitCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadLatestDataFromFolder(String folderPath) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".csv"));

        if (files != null && files.length > 0) {
            Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
            File latest = files[0];
            String fileName = latest.getName();

            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("telemetry_(.*)_H(\\d+)");
            java.util.regex.Matcher matcher = pattern.matcher(fileName);

            if (matcher.find()) {
                String date = matcher.group(1);
                String hour = matcher.group(2);
                // ✅ ACEASTA ESTE LINIA CRUCIALĂ: Salvăm în variabila de sus a clasei
                this.currentSimTime = date + " | Ora " + hour;
            }

            loadSpecificFile(latest);
        }
    }
    private void captureOldStockSnapshot() {
        for (String hub : oldStockSnapshot.keySet()) {
            long count = warehouseStock.stream()
                    .filter(p -> p.currentLocation.equals(hub))
                    .count();
            oldStockSnapshot.put(hub, (int) count);
        }
    }

    public void processStep() {
        // 1. Descărcare camioane ajunse
        for (Truck t : trucks) {
            if (t.isInTransit()) {
                t.remainingTime -= 6;
                if (t.remainingTime <= 0) {
                    List<PackageItem> arrivedItems = t.unload();
                    List<PackageItem> deliveredThisStep = new ArrayList<>();

                    for (PackageItem p : arrivedItems) {
                        p.currentLocation = t.currentLocation;

                        if (p.currentLocation.equals(p.finalDestination)) {
                            p.status = "LIVRAT";
                            deliveredThisStep.add(p);
                            // Îl punem în history pentru a fi afișat o singură dată (în pasul curent)
                            if (!history.contains(p)) history.add(p);
                        }else {
                            // Dacă nu e la destinație, calculăm pasul intermediar
                            List<String> path = DijkstraRouter.getShortestPath(p.origin, p.finalDestination);
                            int currentIdx = path.indexOf(p.currentLocation) + 1;
                            p.status = "Tranzit " + currentIdx + " din " + path.size();
                            warehouseStock.add(p);
                        }
                    }
                    archiveDeliveredPackages(deliveredThisStep);
                }
            }
        }

        // 2. Încărcare camioane libere
        for (Truck t : trucks) {
            if (!t.isInTransit()) {
                assignCargoToTruck(t);
            }
        }
    }

    private void assignCargoToTruck(Truck t) {
        Map<String, List<PackageItem>> groups = warehouseStock.stream()
                .filter(p -> p.currentLocation.equals(t.currentLocation))
                .collect(Collectors.groupingBy(p -> {
                    List<String> path = DijkstraRouter.getShortestPath(p.currentLocation, p.finalDestination);
                    return path.size() > 1 ? path.get(1) : p.finalDestination;
                }));

        String bestNextHop = groups.entrySet().stream()
                .max(Comparator.comparingInt(e -> e.getValue().size()))
                .map(Map.Entry::getKey)
                .orElse(null);

        if (bestNextHop != null) {
            List<PackageItem> pkgs = groups.get(bestNextHop);

            if (pkgs.size() >= 37) {
                List<PackageItem> toLoad = pkgs.stream().limit(75).collect(Collectors.toList());
                t.load(toLoad);
                t.targetHub = bestNextHop;
                t.inTransit = true;
                t.remainingTime = (DijkstraRouter.getDistance(t.currentLocation, bestNextHop) > 300) ? 8 : 4;

                warehouseStock.removeAll(toLoad);

                for (PackageItem p : toLoad) {
                    List<String> fullPath = DijkstraRouter.getShortestPath(p.origin, p.finalDestination);
                    int x = fullPath.indexOf(bestNextHop) + 1;
                    p.status = "Tranzit " + x + " din " + fullPath.size();
                }
            }
        }
    }

    public void updateInventoryCSV() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("data/warehouse_inventory.csv"))) {
            pw.println("HUB,STOC_VECHI,COLETE_NOI,STOC_TOTAL");
            for (String hub : oldStockSnapshot.keySet()) {
                long total = warehouseStock.stream().filter(p -> p.currentLocation.equals(hub)).count();
                pw.printf("%s,%d,%d,%d%n", hub, oldStockSnapshot.get(hub), currentStepArrivals.get(hub), total);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void archiveDeliveredPackages(List<PackageItem> deliveredNow) {
        if (deliveredNow.isEmpty()) return;

        File file = new File(DELIVERED_FILE_PATH);
        boolean isNew = !file.exists();

        try (PrintWriter pw = new PrintWriter(new FileWriter(file, true))) {
            // Dacă fișierul e nou, scriem capul de tabel
            if (isNew) {
                pw.println("AWB,Origin,Destination,FinalLocation,Timestamp");
            }

            for (PackageItem p : deliveredNow) {
                pw.printf("%s,%s,%s,%s,%s%n",
                        p.awb, p.origin, p.finalDestination, p.currentLocation, new java.util.Date());
            }
            System.out.println(" Arhivate " + deliveredNow.size() + " colete în " + DELIVERED_FILE_PATH);
        } catch (IOException e) {
            System.err.println(" Eroare la arhivarea coletelor: " + e.getMessage());
        }
    }

    // Getters pentru SimulationState
    public List<Truck> getTrucks() { return trucks; }
    public List<PackageItem> getWarehouseStock() { return warehouseStock; }
    public List<PackageItem> getHistory() { return history; }
    public Map<String, Integer> getOldStock() { return oldStockSnapshot; }
    public Map<String, Integer> getNewArrivals() { return currentStepArrivals; }
    public String getCurrentSimTime() { return currentSimTime; }



    public OptimizationResult getOptimalRouteAndETA(List<String> hubs, int cap, String weather) {
        return new OptimizationResult(hubs, 12.5, 450.0, weather);
    }
}