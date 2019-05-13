package AAI;

public class ANetworkFormat {
  
  public int[] input_innovation_numbers;
  public int[] output_innovation_numbers;
  
  public ANetworkFormat() {
    input_innovation_numbers = new int[0];
    output_innovation_numbers = new int[0];
  }
  
  public void addInputNode(int newInnovationNumber) {
    int t[] = new int[input_innovation_numbers.length + 1];
    for (int i = 0; i < input_innovation_numbers.length; i++) {
      t[i] = input_innovation_numbers[i];
    }
    t[input_innovation_numbers.length] = newInnovationNumber;
    input_innovation_numbers = t;
  }
  
  public void addOutputNode(int newInnovationNumber) {
    int t[] = new int[output_innovation_numbers.length + 1];
    for (int i = 0; i < output_innovation_numbers.length; i++) {
      t[i] = output_innovation_numbers[i];
    }
    t[output_innovation_numbers.length] = newInnovationNumber;
    output_innovation_numbers = t;
  }
  
}
