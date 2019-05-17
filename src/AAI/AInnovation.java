package AAI;

public class AInnovation {
  public int in_node;
  public int out_node;
  public int innovation_number;
  public boolean is_connection;
  
  public AInnovation(int in_node, int out_node, int innovation_number, boolean is_connection) {
    this.in_node = in_node;
    this.out_node = out_node;
    this.innovation_number = innovation_number;
    this.is_connection = is_connection;
  }
}