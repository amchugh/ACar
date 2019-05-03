package AAI;

import java.util.Random;

public class ANetworkGene {
  
  // how the network structure is supposed to be setup
  public ANetworkFormat format;
  // the data of the gene
  public ANodeWeights[] weights;
  
  // The effectiveness of the algorithm
  public int fitness;
  
  public ANetworkGene(ANetworkFormat f) {
    format = f;
    generateRandom();
  }
  
  // Generate random weights for this gene
  public void generateRandom() {
    weights = new ANodeWeights[format.getNumberNodes()];
    int current_weight = 0;
    
    int incoming;
    
    for (ANodeWeights w : weights) {
      w.generateRandomValues(incoming, format.range);
    }
    
    /*
    for (int l = 0; l < format.layers.length; l++) {
      for (int n = 0; n < format.layers[l]; n++) {
      
      }
    }
    */
  }
  
}

class ANodeWeights {
  
  public double[] inComingWeights;
  public double principalWeight;
  
  public void generateRandomValues(int weightCount, double range) {
    inComingWeights = new double[weightCount];
    for (int i = 0; i < inComingWeights.length; i++) {
      inComingWeights[i] = getRandomWeight();
    }
    principalWeight = getRandomWeight();
  }
  
  public double getRandomWeight() {
    Random r = new Random();
    double d = r.nextDouble();
    return d;
  }
  
}