package neural_network;

import java.util.*;

public class DijkstraRouter {
    private static final Map<String, List<String>> adj = new HashMap<>();
    // Tabel pentru distanțe reale între hub-uri vecine
    private static final Map<String, Integer> distances = new HashMap<>();

    static {
        // Definim conexiunile și distanțele (km)
        addConnection("HUB_TIMISOARA", "HUB_ARAD", 55);
        addConnection("HUB_TIMISOARA", "HUB_CRAIOVA", 325);
        addConnection("HUB_TIMISOARA", "HUB_PITESTI", 360);
        addConnection("HUB_ARAD", "HUB_ORADEA", 115);
        addConnection("HUB_PITESTI", "HUB_SIBIU", 165);
        addConnection("HUB_PITESTI", "HUB_BUCURESTI", 115);
        addConnection("HUB_BUCURESTI", "HUB_OTOPENI", 20);
        addConnection("HUB_BUCURESTI", "HUB_PLOIESTI", 60);
        addConnection("HUB_BUCURESTI", "HUB_CONSTANTA", 225);
        addConnection("HUB_SIBIU", "HUB_BRASOV", 145);
        addConnection("HUB_SIBIU", "HUB_CLUJ", 175);
        addConnection("HUB_CLUJ", "HUB_ORADEA", 150);
        addConnection("HUB_CLUJ", "HUB_BACAU", 280);
    }

    private static void addConnection(String h1, String h2, int dist) {
        adj.computeIfAbsent(h1, k -> new ArrayList<>()).add(h2);
        adj.computeIfAbsent(h2, k -> new ArrayList<>()).add(h1);
        distances.put(h1 + "-" + h2, dist);
        distances.put(h2 + "-" + h1, dist);
    }

    public static List<String> getShortestPath(String start, String end) {
        if (start.equals(end)) return Collections.singletonList(start);

        Queue<List<String>> queue = new LinkedList<>();
        queue.add(Collections.singletonList(start));
        Set<String> visited = new HashSet<>();

        while (!queue.isEmpty()) {
            List<String> path = queue.poll();
            String node = path.get(path.size() - 1);

            if (node.equals(end)) return path;

            if (!visited.contains(node)) {
                visited.add(node);
                List<String> neighbors = adj.getOrDefault(node, new ArrayList<>());
                for (String neighbor : neighbors) {
                    List<String> newPath = new ArrayList<>(path);
                    newPath.add(neighbor);
                    queue.add(newPath);
                }
            }
        }
        return Collections.singletonList(start);
    }

    // REZOLVARE: Metoda returnează acum distanța dintre două HUB-uri vecine
    public static int getDistance(String start, String end) {
        if (start.equals(end)) return 0;
        return distances.getOrDefault(start + "-" + end, 100); // 100km default dacă nu e găsit
    }
}