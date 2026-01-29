package neural_network;

import neural_network.LSTMPredictionModel;
import org.jgap.*;
import org.jgap.impl.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Component
public class RouteOptimizerGA {

    private final LSTMPredictionModel lstmModel;

    // Mapare ID Hub -> Nume Oraș și Coordonate [Lat, Lng]
    private static final Map<String, Object[]> HUB_DATA = Map.ofEntries(
            Map.entry("HUB_BUCURESTI", new Object[]{"București", 44.4268, 26.1025}),
            Map.entry("HUB_OTOPENI", new Object[]{"Otopeni", 44.5517, 26.0839}),
            Map.entry("HUB_PLOIESTI", new Object[]{"Ploiești", 44.9367, 26.0167}),
            Map.entry("HUB_BRASOV", new Object[]{"Brașov", 45.6427, 25.5887}),
            Map.entry("HUB_SIBIU", new Object[]{"Sibiu", 45.7983, 24.1256}),
            Map.entry("HUB_CLUJ", new Object[]{"Cluj-Napoca", 46.7712, 23.6236}),
            Map.entry("HUB_ORADEA", new Object[]{"Oradea", 47.0465, 21.9189}),
            Map.entry("HUB_TIMISOARA", new Object[]{"Timișoara", 45.7489, 21.2087}),
            Map.entry("HUB_ARAD", new Object[]{"Arad", 46.1833, 21.3167}),
            Map.entry("HUB_CRAIOVA", new Object[]{"Craiova", 44.3167, 23.8000}),
            Map.entry("HUB_PITESTI", new Object[]{"Pitești", 44.8565, 24.8697}),
            Map.entry("HUB_CONSTANTA", new Object[]{"Constanța", 44.1733, 28.6383}),
            Map.entry("HUB_GALATI", new Object[]{"Galați", 45.4333, 28.0333}),
            Map.entry("HUB_IASI", new Object[]{"Iași", 47.1517, 27.5872}),
            Map.entry("HUB_BACAU", new Object[]{"Bacău", 46.5670, 26.9145})
    );

    public RouteOptimizerGA(LSTMPredictionModel lstmModel) {
        this.lstmModel = lstmModel;
        System.out.println("Modulul GA: Optimizatorul a fost inițializat cu suport JGAP.");
    }

    /**
     * Calculează ruta optimă folosind JGAP.
     */
    public List<String> calculateOptimalRoute(List<String> hubsToVisit, int vehicleCapacity, Object dynamicData) {
        try {
            // 1. Configurare JGAP
            Configuration conf = new DefaultConfiguration();
            conf.setPreservFittestIndividual(true);
            conf.setKeepPopulationSizeConstant(false);

            // 2. Definirea Funcției de Fitness (bazată pe LSTM)
            FitnessFunction myFunc = new RouteFitnessFunction(hubsToVisit, lstmModel, dynamicData);
            conf.setFitnessFunction(myFunc);

            // 3. Definirea Genomului (Genele reprezintă indexul hub-ului din listă)
            Gene[] sampleGenes = new Gene[hubsToVisit.size()];
            for (int i = 0; i < hubsToVisit.size(); i++) {
                sampleGenes[i] = new IntegerGene(conf, 0, hubsToVisit.size() - 1);
            }

            IChromosome sampleChromosome = new Chromosome(conf, sampleGenes);
            conf.setSampleChromosome(sampleChromosome);
            conf.setPopulationSize(50);

            // 4. Evoluția populației
            Genotype population = Genotype.randomInitialGenotype(conf);
            for (int i = 0; i < 100; i++) { // 100 de generații
                population.evolve();
            }

            // 5. Extragerea celei mai bune rute
            IChromosome bestSolution = population.getFittestChromosome();
            List<String> optimalRoute = new ArrayList<>();
            for (Gene gene : bestSolution.getGenes()) {
                int index = (Integer) gene.getAllele();
                optimalRoute.add(hubsToVisit.get(index));
            }

            return optimalRoute;

        } catch (Exception e) {
            System.err.println("Eroare la rularea GA: " + e.getMessage());
            return hubsToVisit; // Fallback la lista inițială în caz de eroare
        }
    }

    /**
     * Clasă internă pentru calculul scorului unei rute (Fitness).
     * Un scor mai mare înseamnă o rută mai rapidă (timp total mai mic).
     */
    private static class RouteFitnessFunction extends FitnessFunction {
        private final List<String> hubs;
        private final LSTMPredictionModel lstm;
        private final Object dynamicData;

        public RouteFitnessFunction(List<String> hubs, LSTMPredictionModel lstm, Object dynamicData) {
            this.hubs = hubs;
            this.lstm = lstm;
            this.dynamicData = dynamicData;
        }

        @Override
        protected double evaluate(IChromosome chromosome) {
            double totalTime = 0;
            Gene[] genes = chromosome.getGenes();

            for (int i = 0; i < genes.length - 1; i++) {
                String start = hubs.get((Integer) genes[i].getAllele());
                String end = hubs.get((Integer) genes[i + 1].getAllele());
                // Apelăm LSTM pentru a prezice timpul între segmente
                totalTime += lstm.predictETT(start, end, dynamicData);
            }

            // JGAP maximizează fitness-ul, deci inversăm timpul
            return (totalTime == 0) ? 0 : 10000 / totalTime;
        }
    }
}