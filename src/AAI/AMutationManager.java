package AAI;

import CarBase.Config;

import java.util.ArrayList;
import java.util.List;

public class AMutationManager {
  
  public List<AInnovation> innovations = new ArrayList<>();
  private int current_innovation_number = 0;
  
  public ANetworkFormat networkFormat;
  
  public AMutationManager() {
  
  }
  
  public AMutationManager(int inputNum, int outputNum) {
    generatePresetInnovations(inputNum, outputNum);
  }
  
  /**
   * Generates the preset values for the input and output nodes. This is necessary, idk how to explain why rn tho
   *
   * @param inputNum  the number of input nodes
   * @param outputNum the number of output nodes
   */
  public void generatePresetInnovations(int inputNum, int outputNum) {
    networkFormat = new ANetworkFormat();
    for (int i = 0; i < inputNum; i++) {
      networkFormat.addInputNode(getNextInnovationNumber());
    }
    for (int i = 0; i < outputNum; i++) {
      networkFormat.addOutputNode(getNextInnovationNumber());
    }
    //System.out.println("registered inputs and outputs");
  }
  
  /**
   * Finds the appropriate innovation number for the given gene. Generates new innovation numbers as necessary.
   *
   * @param _generationNumber NOT USED. The generation number that the gene originated in
   * @param inNodeID          the innovation number of the node that the connection originates from
   * @param outNodeID         the innovation number of the node that the connection terminates to
   * @param isConnection      whether the gene represents a node or a connection
   * @return the appropriate innovation number
   */
  public int getAppropriateInnovationNumber(int _generationNumber, int inNodeID, int outNodeID, boolean isConnection) {
    // This is the collection of mutations. We need to make sure that the mutation that is trying
    // to be created does not already exist.
    for (AInnovation inno : innovations) {
      if (inNodeID == inno.in_node_innovation_number && outNodeID == inno.out_node_innovation_number && isConnection == inno.is_connection) {
        // This innovation has already been created this generation.
        if (Config.DEBUG) {
          System.out.println("Innovation of type " + (isConnection ? "connection" : "node") +
            " connecting from " + String.valueOf(inNodeID) + " to " + String.valueOf(outNodeID) + " already exists! Innovation number: " + String.valueOf(inno.innovation_number));
        }
        return inno.innovation_number;
      }
    }
    // We've failed to find a record of this innovation. Time to add it.
    current_innovation_number++;
    innovations.add(new AInnovation(inNodeID, outNodeID, current_innovation_number, isConnection));
    if (Config.DEBUG) {
      System.out.println("Innovation of type " + (isConnection ? "connection" : "node") +
        " connecting from " + String.valueOf(inNodeID) + " to " + String.valueOf(outNodeID) + " has been registed with value " + String.valueOf(current_innovation_number) + "!");
    }
    return current_innovation_number;
  }
  
  /**
   * Increments and returns the innovation number
   *
   * @return the next innovation number
   */
  public int getNextInnovationNumber() {
    current_innovation_number++;
    return current_innovation_number;
  }
  
  /**
   * returns the innovation number
   *
   * @return the current innovation number
   */
  public int getCurrentInnovationNumber() {
    return current_innovation_number;
  }
  
}

/**
 * class AMutationManagerByGeneration {
 * <p>
 * public static List<AGenerationMutationTracker> gen_mutations = new ArrayList<AGenerationMutationTracker>();
 * public static int current_innovation_number = 0;
 * <p>
 * public static int getAppropriateInnovationNumber(int genNum, int inNodeID, int outNodeID) {
 * for (AGenerationMutationTracker mt : gen_mutations) {
 * if (mt.generation_number == genNum) {
 * // This is the collection of mutations. We need to make sure that the mutation that is trying
 * // to be created does not already exist.
 * for (AInnovation inno : mt.connection_innovations) {
 * if (inNodeID == inno.in_node_innovation_number && outNodeID == inno.out_node_innovation_number) {
 * // This innovation has already been created this generation.
 * if (CarBase.Config.DEBUG)
 * System.out.println("*********************** " + "Innovation already exists! " + String.valueOf(inno.innovation_number));
 * return inno.innovation_number;
 * }
 * }
 * // We've failed to find a record of this innovation. Time to add it.
 * current_innovation_number++;
 * mt.connection_innovations.add(new AInnovation(inNodeID, outNodeID, current_innovation_number));
 * if (CarBase.Config.DEBUG)
 * System.out.println("*********************** " + "Regiseted new innovation");
 * return current_innovation_number;
 * }
 * }
 * // We've failed to find a generation tracker for this generation. Time to create one.
 * AGenerationMutationTracker mt = new AGenerationMutationTracker(genNum);
 * gen_mutations.add(mt);
 * if (CarBase.Config.DEBUG)
 * System.out.println("*********************** " + "Registered new Generational tracker");
 * // Also, add our innovation
 * current_innovation_number++;
 * mt.connection_innovations.add(new AInnovation(inNodeID, outNodeID, current_innovation_number));
 * if (CarBase.Config.DEBUG)
 * System.out.println("*********************** " + "Registered new innovation");
 * return current_innovation_number;
 * }
 * <p>
 * }
 * <p>
 * class AGenerationMutationTracker {
 * <p>
 * public int generation_number;
 * public List<AInnovation> connection_innovations;
 * <p>
 * public AGenerationMutationTracker(int genNumber) {
 * this.generation_number = genNumber;
 * connection_innovations = new ArrayList<>();
 * }
 * <p>
 * }
 */

