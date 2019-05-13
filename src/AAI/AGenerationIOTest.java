
package AAI;

import junit.framework.TestCase;

import java.io.File;

public class AGenerationIOTest extends TestCase {
  
  String TEST_FILENAME = "test.gen";
  final int GENERATION_SIZE = 50;
  
  public void setUp() {
    cleanUpFile();
    //super.setUp(); // doesn't look to be necessary as the method does nothing.
  }
  
  public void tearDown() {
    cleanUpFile();
  }
  
  public void cleanUpFile() {
    // Ensure that the test file used in these tests does not exist.
    File f = new File(TEST_FILENAME);
    if (f.exists()) {
      f.delete();
    }
    
  }
  
  public void testSave() {
    AMutationManager mutationManager = new AMutationManager(1, 1);
    
    AGeneration generation1 = new AGeneration();
    generation1.genomes.add(new AGenome(mutationManager.networkFormat, 2));
    generation1.generation_number = 2;
    
    assertTrue(AGenerationIO.save(generation1, TEST_FILENAME));
    assertTrue(new File(TEST_FILENAME).exists());
  }
  
  public void testSaveAndThenReload() {
    // todo::Create a simple Generation consisting of some number of AGenomes.  Something
    // todo::non-trivial but also not huge.  Save it.  Then call load and then compare the
    // todo::two generation instances to make sure they are equivalent.  This will
    // todo::require overriding the equals method.
    
    AMutationManager mutationManager = new AMutationManager(1, 1);
    AGeneration original = new AGeneration();
    
    original.generation_number = 3;
    for (int i = 0; i < GENERATION_SIZE; i++) {
      original.genomes.add(new AGenome(mutationManager.networkFormat, original.generation_number));
    }
    
    assertTrue("Failed to save file", AGenerationIO.save(original, TEST_FILENAME));
    assertTrue("Failed to find file", (new File(TEST_FILENAME).exists()));
    AGeneration loaded;
    assertNotNull("Failed to load file as generation", loaded = AGenerationIO.load(TEST_FILENAME));
    assertEquals("Saving then loading the generations should result in the same", original, loaded);
  }
  
}