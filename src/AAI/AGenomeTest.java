package AAI;

import junit.framework.TestCase;

import java.util.Random;

public class AGenomeTest extends TestCase {
  
  ANetworkFormat format;
  ANodeDepthManager depthManager;
  AMutationManager mutationManager;
  Random r;
  int SEED = 1;
  
  public void setUp() {
    mutationManager = new AMutationManager(1, 1);
    format = mutationManager.networkFormat;
    depthManager = new ANodeDepthManager(format);
    r = new Random(SEED);
  }
  
  public void tearDown() {
    this.mutationManager = null;
    this.format = null;
    this.depthManager = null;
    this.r = null;
  }
  
  public void testForceMutateConnection() {
    // Create a genome and add a connection
    AGenome genome = new AGenome(format, 0);
    int before = genome.getConnectionGenes().size();
    genome.forceMutateConnection(r, depthManager, mutationManager);
    int after = genome.getConnectionGenes().size();
    assertTrue("Failed to mutate a new connection", before + 1 == after);
  }
  
  public void testMutateNode() {
    // Create a genome, add a connection, then add a node
    AGenome genome = new AGenome(format, 0);
    int before = genome.getNodeGenes().size();
    // We need a connection to make a new node.
    genome.forceMutateConnection(r, depthManager, mutationManager);
    genome.mutateNode(r, depthManager, mutationManager);
    int after = genome.getNodeGenes().size();
    assertTrue("Failed to mutate a new node", before + 1 == after);
  }
  
  public void testFindConnectionGeneByInnovationNumber() {
    // Create a genome, add a connection, then find that connection
    AGenome genome = new AGenome(format, 0);
    // Create the connection
    genome.forceMutateConnection(r, depthManager, mutationManager);
    // The innovation number given to that connection should
    // be equal to the most recent addition in the mutationManager
    int newest = mutationManager.getCurrentInnovationNumber();
    // Now we attempt to find and assert not null
    assertNotNull("Failed to find new connection based off of innovation number",
      genome.findConnectionGeneByInnovationNumber(newest));
  }
  
  public void testFindNodeGeneByInnovationNumber() {
    // Create a genome, add a connection, add a node, then find that node
    AGenome genome = new AGenome(format, 0);
    // We need a connection to make a new node.
    genome.forceMutateConnection(r, depthManager, mutationManager);
    genome.mutateNode(r, depthManager, mutationManager);
    // The innovation number given to that connection should
    // be equal -- not to the newest or second newest, because
    // those are connections -- but the third newest.
    int nodeNumber = mutationManager.getCurrentInnovationNumber() - 2;
    // Now we attempt to find and assert not null
    assertNotNull("Failed to find new node based off of innovation number",
      genome.findNodeGeneByInnovationNumber(nodeNumber));
  }
  
  public void testRelinkConnections() {
    // This one is harder.
    // We need to create a genome and have it create a connection
    // Then we need to create another genome and add that connection object
    // Finally, we need to call "relinkConnections" and make sure the
    //  the connection targets the correct nodes.
    AGenome g1 = new AGenome(format, 0);
    AGenome g2 = new AGenome(format, 0);
    int in_node_innovation = mutationManager.networkFormat.input_innovation_numbers[0];
    int out_node_innovation = mutationManager.networkFormat.output_innovation_numbers[0];
  
    // Create the connection.  Since there are only two nodes in the
    // Genome right now - the input and output - then the connection will
    // be created directly between these two nodes.
    g1.forceMutateConnection(r, depthManager, mutationManager);
  
    // The innovation number given to that connection should
    // be equal to the most recent addition in the mutationManager
    // We need the specific connection object.
    int newest = mutationManager.getCurrentInnovationNumber();
    AConnectionGene connectionGene = g1.findConnectionGeneByInnovationNumber(newest);
  
    // Assert that this connection gene is between the input and output nodes.
    assertEquals(connectionGene.in_node,
      g1.findNodeGeneByInnovationNumber(in_node_innovation));
    assertEquals(connectionGene.out_node,
      g1.findNodeGeneByInnovationNumber(out_node_innovation));
  
    // Find the genes that the connection should be linked to in the new genome
    ANodeGene correctIn = g2.findNodeGeneByInnovationNumber(in_node_innovation);
    ANodeGene correctOut = g2.findNodeGeneByInnovationNumber(out_node_innovation);
  
    // We should make sure that relinking is necessary
    assertTrue("Gene is already linked",
      connectionGene.in_node != correctIn || connectionGene.out_node != correctOut);
    // Now we need to add this gene to the other genome
    // Because our network has 1 input and one output, there is only one valid connection, therefor
    // We don't need to worry about testing to make sure the connection is possible in g2.
    g2.addConnectionGene(connectionGene);
    // Now we tell g2 to relink.
    g2.relinkConnections();
  
    // Find the gene now that we've modified
    AConnectionGene newlyConnectedGene = g2.findConnectionGeneByInnovationNumber(
      mutationManager.getCurrentInnovationNumber());
  
    // Finally, we can test to make sure it is linked to the correct node genes
    assertTrue("Gene failed to link properly",
      newlyConnectedGene.in_node == correctIn ||
        newlyConnectedGene.out_node == correctOut);
  }
  
  public void testPerformCalculationOfNumberOfLegalConnections() {
    assertTrue("Subtest #1 failed", AGenome.performCalculationOfNumberOfLegalConnections(1, 1, 0) == 1);
    assertTrue("Subtest #2 failed", AGenome.performCalculationOfNumberOfLegalConnections(3, 2, 0) == 6);
    assertTrue("Subtest #3 failed", AGenome.performCalculationOfNumberOfLegalConnections(1, 1, 2) == 6);
//    assertTrue("Subtest #4 failed", AGenome.performCalculationOfNumberOfLegalConnections(1,1,0) == 1);
//    assertTrue("Subtest #5 failed", AGenome.performCalculationOfNumberOfLegalConnections(1,1,0) == 1);
//    assertTrue("Subtest #6 failed", AGenome.performCalculationOfNumberOfLegalConnections(1,1,0) == 1);
//    assertTrue("Subtest #7 failed", AGenome.performCalculationOfNumberOfLegalConnections(1,1,0) == 1);
  }
  
}