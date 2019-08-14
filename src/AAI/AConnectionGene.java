package AAI;

import java.io.Serializable;

public class AConnectionGene implements Serializable {
  
  public ANodeGene in_node;
  public ANodeGene out_node;
  public float weight;
  private boolean enabled;
  private int innovation_number;
  public String name;
  
  public AConnectionGene(ANodeGene in_node, ANodeGene out_node, float weight, boolean enabled, int innovation_number) {
    this.in_node = in_node;
    this.out_node = out_node;
    this.weight = weight;
    this.enabled = enabled;
    this.innovation_number = innovation_number;
    this.name = String.valueOf(this.in_node.getInnovationNumber()) + " to " + String.valueOf(this.out_node.getInnovationNumber());
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (!(obj instanceof AConnectionGene)) return false;
    AConnectionGene other = (AConnectionGene) obj;
    return
      in_node == other.in_node &&
        out_node == other.out_node &&
        weight == other.weight &&
        enabled == other.enabled &&
        innovation_number == other.innovation_number &&
        name.equals(other.name);
  }
  
  public void disableConnection() {
    enabled = false;
    /*
    if (CarBase.Config.DEBUG)
      System.out.println("Disabled a connection");
      */
  }
  
  public boolean isEnabled() {
    return enabled;
  }
  
  public int getInnovationNumber() {
    return this.innovation_number;
  }
  
}
