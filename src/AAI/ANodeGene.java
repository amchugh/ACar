package AAI;

import java.io.Serializable;

public class ANodeGene implements Serializable {
  
  public enum Type {INPUT, HIDDEN, OUTPUT}
  
  public Type node_type;
  private int innovation_number;
  
  // Variables used for drawing the image
  public int x;
  public int y;
  
  public ANodeGene(Type node_type, int innovation_number) {
    this.node_type = node_type;
    this.innovation_number = innovation_number;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (!(obj instanceof ANodeGene)) return false;
    ANodeGene other = (ANodeGene) obj;
    return
      node_type == other.node_type &&
        innovation_number == other.innovation_number;
  }
  
  public int getInnovationNumber() {
    return this.innovation_number;
  }
  
}
