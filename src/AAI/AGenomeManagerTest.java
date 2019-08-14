package AAI;

import junit.framework.TestCase;

import java.io.File;

public class AGenomeManagerTest extends TestCase {
  
  AGenomeManager m;
  String PATH = "TEST\\AGenomeManagerTest";
  
  int INPUT_NODE_NUMBER = 2;
  int OUTPUT_NODE_NUMBER = 2;
  int STARTING_CONNECTIONS = 1;
  
  public void setUp() {
    createGenerationManager();
  }
  
  private void createGenerationManager() {
    m = new AGenomeManager(
      INPUT_NODE_NUMBER,
      OUTPUT_NODE_NUMBER,
      10,
      STARTING_CONNECTIONS);
  }
  
  public void tearDown() {
    m = null;
  }
  
  public void testSaveGeneration() {
    // create a generation to save
    // then, save the generation.
    // finally, test to see if file exists
    m.trainNextGeneration();
    m.saveGeneration(0, PATH);
    File f = new File(PATH + "\\generation_0.gen");
    assertTrue("Failed to find save file", f.exists());
    // Destroy the file.
    assertTrue("Failed to delete test file", f.delete());
  }
  
  public void testLoadGeneration() {
    // create a generation to save
    // then, save the generation.
    // then, recreate the generation manager
    // attempt to load the file.
    m.trainNextGeneration();
    m.saveGeneration(0, PATH);
    File f = new File(PATH + "\\generation_0.gen");
    assertTrue("Failed to create save file", f.exists());
    createGenerationManager();
    assertTrue("Failed to load the save file", m.loadGeneration(0, PATH));
  }
  
  public void testBreedGenomes() {
    // The new genome will not have a new structure.
    // The structure will be the same as the most fit genome
    // Perhaps there is a chance in the future that
    //    the structure is that of the less fit genome, but
    //    lets ignore that for now.
  
    // I don't yet know how I want to approach testing this function.
    
    /*
    m.trainNextGeneration();
    AGeneration gen = m.getCurrentGeneration();
    AGenome g1 = gen.genomes.get(0);
    AGenome g2 = gen.genomes.get(1);
    */
  }
  
  public void testSetupFirstGeneration() {
    m.trainNextGeneration();
    assertTrue("Generation is labeled incorrectly", m.getCurrentGeneration().generation_number == 0);
    assertTrue("First generation creation resulted in impossible number of new mutations",
      m.getMutationManager().getCurrentInnovationNumber() <= INPUT_NODE_NUMBER + OUTPUT_NODE_NUMBER + (INPUT_NODE_NUMBER * OUTPUT_NODE_NUMBER));
  }
  
  public void testCreateNextGeneration() {
  
  }
  
}