package src.neural_network;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

/**
 * @class LSTMPredictionModel
 * @description Definește și configurează arhitectura Rețelei Neuronale LSTM
 * pentru a estima Timpul de Tranzit Efectiv (ETT) dinamic.
 */
public class LSTMPredictionModel {

    private MultiLayerNetwork model;
    private final int inputSize = 10; 
    private final int hiddenLayerSize = 50;
    private final int outputSize = 1; 

    public void buildModel() {
        // [dl4j-setup-start]
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(1234)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(new Adam(0.005)) 
                .weightInit(WeightInit.XAVIER) 
                .list()
                .layer(0, new LSTM.Builder()
                        .nIn(inputSize)
                        .nOut(hiddenLayerSize)
                        .activation(Activation.TANH)
                        .gateActivationFunction(Activation.SIGMOID)
                        .build())
                .layer(1, new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.IDENTITY) 
                        .nIn(hiddenLayerSize)
                        .nOut(outputSize)
                        .build())
                .build();
        // [dl4j-setup-end]

        model = new MultiLayerNetwork(conf);
        model.init(); 
        System.out.println(" Modulul LSTM: Arhitectura Rețelei Neuronale a fost definită.");
    }

    /**
     * Simulează predicția Timpului de Tranzit Efectiv (ETT).
     * @param startHub Id-ul depozitului de plecare
     * @param endHub Id-ul depozitului de destinație
     * @param dynamicData Date de intrare dinamice (trafic, vreme)
     * @return Timpul estimat de tranzit în minute.
     */
    public double predictETT(String startHub, String endHub, Object dynamicData) {
        // Simulare ETT dinamic. În implementarea finală se folosește model.rnnTimeStep()
        double baseTime = 60.0; 
        double dynamicPenalty = Math.random() * 20; 
        
        return baseTime + dynamicPenalty; 
    }

    public MultiLayerNetwork getModel() {
        return model;
    }
}