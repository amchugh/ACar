package AAI;

public class AInnovation {
  public int in_node_innovation_number;
  public int out_node_innovation_number;
  public int innovation_number;
  public boolean is_connection;
  
  public AInnovation(int in_node, int out_node, int innovation_number, boolean is_connection) {
    this.in_node_innovation_number = in_node;
    this.out_node_innovation_number = out_node;
    this.innovation_number = innovation_number;
    this.is_connection = is_connection;
  }
}