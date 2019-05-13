package AAI;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class AGeneration implements Serializable {
  
  public List<AGenome> genomes = new ArrayList<>();
  public int generation_number;
  
  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (!(obj instanceof AGeneration)) return false;
    AGeneration other = (AGeneration) obj;
    return generation_number == other.generation_number && genomes.equals(other.genomes);
  }
}