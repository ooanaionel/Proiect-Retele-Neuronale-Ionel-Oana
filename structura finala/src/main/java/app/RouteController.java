package app;

import neural_network.NeuralNetworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/simulation")
public class RouteController {

    @Autowired
    private NeuralNetworkService neuralNetworkService;

    @GetMapping("/state")
    public SimulationState getState() {
        return new SimulationState(
                neuralNetworkService.getTrucks(),
                neuralNetworkService.getWarehouseStock(),
                neuralNetworkService.getHistory(),
                neuralNetworkService.getOldStock(),
                neuralNetworkService.getNewArrivals(),
                neuralNetworkService.getCurrentSimTime()
        );
    }

    @PostMapping("/step")
    public SimulationState nextStep() {
        neuralNetworkService.runNextSimulationStep();
        return getState(); // Returnăm starea actualizată după pasul de 6 ore
    }
}