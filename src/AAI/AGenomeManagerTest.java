package AAI;

import junit.framework.TestCase;

import java.io.File;

public class AGenomeManagerTest extends TestCase {
  
  AGenomeManager m;
  String PATH = "TEST\\AGenomeManagerTest";
  
  public void setUp() {
    createGenerationManager();
  }
  
  private void createGenerationManager() {
    m = new AGenomeManager(2, 2, 10, 1);
    
  }
  
  public void tearDown() {
    m = null;
  }
  
  public void testSaveGeneration() {
    // todo::create a generation to save
    // todo::then, save the generation.
    // todo::finally, test to see if file exists
    m.trainNextGeneration();
    m.saveGeneration(0, PATH);
    File f = new File(PATH + "\\generation_0.gen");
    assertTrue("Failed to find save file", f.exists());
    // Destroy the file.
    assertTrue("Failed to delete test file", f.delete());
  }
  
  public void testLoadGeneration() {
    // todo::create a generation to save
    // todo::then, save the generation.
    // todo::then, recreate the generation manager
    // todo::attempt to load the file.
    m.trainNextGeneration();
    m.saveGeneration(0, PATH);
    File f = new File(PATH + "\\generation_0.gen");
    assertTrue("Failed to create save file", f.exists());
    createGenerationManager();
    assertTrue("Failed to load the save file", m.loadGeneration(0, PATH));
  }
  
  public void testBreedGenomes() {
  
  }
}