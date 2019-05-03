package AAI;

public class ANetwork {
  
  private ALayer[] layers;
  private AConnection[] conns;
  
  private ANetworkGene gene;
  
  public ANetwork(ANetworkGene gene) {
    this.gene = gene;
    generateNetwork();
  }
  
  public void generateNetwork() {
  
  }
  
}

// How the network is supposed to be structured
class ANetworkFormat {
  
  // The size of each layer in the network. This should be at least 2.
  public int[] layers;
  public double range;
  
  public ANetworkFormat(int[] hidden_layers, double d_range) {
    layers = hidden_layers;
    range = d_range;
  }
  
  public int getNumberNodes() {
    int t = 0;
    for (int i = 0; i < layers.length; i++) {
      t += layers[i];
    }
    return t;
  }
  
}

class ALayer {
  
  public ANode[] nodes;
  
  public ALayer(int count) {
    nodes = new ANode[count];
  }
  
}

class ANode {

}

class AConnection {

}