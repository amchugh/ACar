package AAI;

import CarBase.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AGenomeManager {
  
  private int current_gen;
  private List<AGeneration> generations;
  
  private int input_nodes;
  private int output_nodes;
  private int generation_size;
  private int starting_connections;
  
  private AMutationManager mutation_manager;
  private ANodeDepthManager depth_manager;
  private ANetworkFormat network_format;
  
  public AGenomeManager(int input_nodes, int output_nodes, int generation_size, int starting_connections) {
    // Setup the necessary values
    mutation_manager = new AMutationManager(input_nodes, output_nodes);
    network_format = mutation_manager.networkFormat;
    
    depth_manager = new ANodeDepthManager(network_format);
    
    // Setup self values
    current_gen = -1;
    generations = new ArrayList<>();
    this.input_nodes = input_nodes;
    this.output_nodes = output_nodes;
    this.generation_size = generation_size;
    this.starting_connections = starting_connections;
  }
  
  public void generateGenomeImages(int genNum, String path, String filetype, int seed, boolean resetSeed) {
    // Get the generation
    AGeneration g = findGenerationByNumber(genNum);
    // Find the generation folder. If a path was not given, make it here.
    String folder = getFolderNameAndCreateFolders(genNum, path);
    // Init the random value
    Random r = new Random(seed);
    for (int i = 0; i < g.genomes.size(); i++) {
      g.genomes.get(i).SaveImage(r, path + folder + "\\network_" + String.valueOf(i) + filetype);
      if (resetSeed) {
        r = new Random(seed);
      }
    }
    System.out.println("Created images for generation " + String.valueOf(genNum));
  }
  
  public void saveGeneration(int genNum, String path) {
    String filename = "\\" + "generation_" + String.valueOf(genNum);
    if (!path.equals("")) {
      (new File(path + "\\")).mkdirs();
    } else {
      filename = "generation_" + String.valueOf(genNum);
    }
    AGenerationIO.save(findGenerationByNumber(genNum), path + filename);
  }
  
  public void loadGeneration(int genNum, String path) {
    if (findGenerationByNumber(genNum) != null) {
      System.out.println("Already have a generation with that number.");
      return;
    }
    String filename = "\\" + "generation_" + String.valueOf(genNum);
    if (path.equals("")) {
      filename = "generation_" + String.valueOf(genNum);
    }
    generations.add(AGenerationIO.load(path + filename));
  }
  
  private String getFolderNameAndCreateFolders(int genNum, String path) {
    // Find the generation folder. If a path was not given, make it here.
    String folder = "\\generation_" + String.valueOf(genNum);
    if (path.equals("")) {
      folder = "generation_" + String.valueOf(genNum);
    }
    // Create the folders if needed
    boolean success = (new File(path + folder + "\\")).mkdirs();
    if (!success) {
      System.out.println("failed to make folder \"" + path + folder + "\\" + "\"");
    }
    return folder;
  }
  
  public void trainNextGeneration() {
    createNextGeneration();
    // We need to do something different if this is the first generation
    if (generations.size() == 1) {
      setupFirstGeneration();
    } else {
      // We need to create a new population of Genomes
      // TODO implement
    }
  }
  
  private void setupFirstGeneration() {
    // Find the generation
    AGeneration g = findGenerationByNumber(0);
    // Add new Random Genomes
    for (int i = 0; i < generation_size; i++) {
      // We need to create the genome
      AGenome genome = new AGenome(network_format, current_gen);
      // We need a random number for this genome. We'll use the following line to get a unique seed for every genome
      Random r = new Random(i + generation_size * g.generation_number);
      // We then need to mutate at least one connection. We'll mutate according to a parameter
      for (int m = 0; m < starting_connections; m++) {
        genome.forceMutateConnection(r, depth_manager, mutation_manager);
      }
      // Now we have a choice. We could add random nodes and connections, but I'm not quite sure how this should work
      // TODO figure that ^^^ out.
      // Now we should add the genome to the generation
      g.genomes.add(genome);
    }
    System.out.println("Created first generation");
  }
  
  private void createNextGeneration() {
    current_gen++;
    AGeneration g = new AGeneration();
    g.generation_number = current_gen;
    generations.add(g);
    if (Config.DEBUG)
      System.out.println("Created new gen (number: " + String.valueOf(current_gen) + ")");
  }
  
  private AGeneration findGenerationByNumber(int genNum) {
    for (AGeneration generation : generations) {
      if (generation.generation_number == genNum) {
        return generation;
      }
    }
    //System.out.println("Failed to find generation");
    return null;
  }

//  public AGenome addNewRandomGenome() {
//    AGenome g = new AGenome(input_nodes, output_nodes, current_gen);
//    generations.get(current_gen).genomes.add(g);
//    return g;
//  }
  
  /**
   * Creates a new Genome from the two provided Genomes.
   *
   * @param g1 the first parent
   * @param g2 the second parent
   * @return the new Genome
   */
  public AGenome breedGenomes(Random r, AGenome g1, AGenome g2) {
    // Create our new gene
    // TODO decide if this should be changed to "addNewRandomGenome"
    // NOTE probably not.
    AGenome n = new AGenome(network_format, current_gen);
    
    // First, get the better parent based on fitness.
    AGenome betterG, lesserG;
    if (g1.fitness > g2.fitness) {
      betterG = g1;
      lesserG = g2;
    } else {
      betterG = g2;
      lesserG = g1;
    }
    
    // We need to iterate over all the genes in the better genome
    // If the gene is disjoint, add as is.
    // If the gene matches up, pick at random 1 gene to carry over
    
    // Start with connections
    for (AConnectionGene g : betterG.getConnectionGenes()) {
      // See if it is crossed over
      if (lesserG.hasConnectionGene(g)) {
        // This gene is crossed over. Pick at random to add.
        if (r.nextFloat() < 0.5f) {
          // Take the better genome's gene
          n.addConnectionGene(g);
        } else {
          // Take the lesser genome's gene
          n.addConnectionGene(lesserG.findConnectionGeneByInnovationNumber(g.getInnovationNumber()));
        }
      } else {
        // If this gene is not crossed over, add it
        n.addConnectionGene(g);
      }
    }
    
    // Now add node genes.
    // Node genes have no specific values, so we will just copy over all of them from the better genome
    for (ANodeGene nodeGene : betterG.getNodeGenes()) {
      n.addNodeGene(nodeGene);
    }
    
    // Finally, we need to reattach connections
    n.relinkConnections();
    
    return n;
  }
  
}