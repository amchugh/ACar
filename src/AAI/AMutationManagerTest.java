package AAI;

import junit.framework.TestCase;

import java.util.Random;

public class AMutationManagerTest extends TestCase {
  
  AMutationManager mut;
  int MAXBOUND = 1000;
  
  public void setUp() {
    mut = new AMutationManager();
  }
  
  public void tearDown() {
    mut = null;
  }
  
  public void testGeneratePresetInnovations() {
    Random r = new Random();
    int input = r.nextInt(MAXBOUND);
    int output = r.nextInt(MAXBOUND);
    mut.generatePresetInnovations(input, output);
    // We're checking to make sure the network format has the correct number
    // of input and output nodes
    ANetworkFormat f = mut.networkFormat;
    assertTrue("Incorrect number of input nodes",
      f.input_innovation_numbers.length == input);
    assertTrue("Incorrect number of output nodes",
      f.output_innovation_numbers.length == output);
    // We also need to check and make sure an appropriate number of
    // innovation numbers have been created
    assertTrue("Incorrect number of registered innovations",
      mut.getCurrentInnovationNumber() == input + output);
  }
  
  public void testGetAppropriateInnovationNumber() {
    Random r = new Random();
    // We need to create two new innovations to register
    int in_1 = r.nextInt(MAXBOUND);
    int out_1 = r.nextInt(MAXBOUND);
    boolean conn_1 = r.nextBoolean();
  
    int in_2 = r.nextInt(MAXBOUND);
    int out_2 = r.nextInt(MAXBOUND);
    boolean conn_2 = r.nextBoolean();
  
    // Register the first innovation
    int inno_num_1 = mut.getAppropriateInnovationNumber(
      0, in_1, out_1, conn_1
    );
    // This number should be 1.
    assertTrue("First innovation number is incorrect?",
      inno_num_1 == 1);
  
    // Register the second innovation
    int inno_num_2 = mut.getAppropriateInnovationNumber(
      0, in_2, out_2, conn_2
    );
    // This number should be 2. UNLESS, it randomly happens to be the same
    if (in_1 == in_2 && out_1 == out_2 && conn_1 == conn_2) {
      assertTrue("Innovation received incorrect number",
        inno_num_1 == inno_num_2);
    } else {
      assertTrue("Innovation incorrectly received old number",
        inno_num_1 != inno_num_2);
      assertTrue("Innovation received incorrect number in sequence",
        inno_num_2 == 2);
    }
  
    // Finally, attempt to register the first innovation again
    // and make sure it gets the same number
    int inno_num_3 = mut.getAppropriateInnovationNumber(
      0, in_1, out_1, conn_1
    );
    assertTrue("Same innovation did not receive same number",
      inno_num_3 == inno_num_1);
  }
  
  public void testGetNextInnovationNumber() {
    assertTrue("Mutation Manager does not initialize innovation number on correct index",
      mut.getNextInnovationNumber() == 1);
    assertTrue("Mutation Manager seemingly cannot count",
      mut.getNextInnovationNumber() == 2);
  }
  
}