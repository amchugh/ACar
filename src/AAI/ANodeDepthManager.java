package AAI;

import CarBase.Config;

public class ANodeDepthManager {
  
  /*
  private static ANodeDepthManager main;
  
  public static ANodeDepthManager getInstance() {
    if (main == null) {
      main = new ANodeDepthManager();
    }
    return main;
  }
  */
  
  public int[] node_depths; // Tracks depth of nodes based on their innovation numbers
  public ANetworkFormat network_format;
  
  public ANodeDepthManager(ANetworkFormat networkFormat) {
    node_depths = new int[0];
    network_format = networkFormat;
  }
  
  public void addNode(int innovation_number, int parent_node_number) {
    // Make sure the node is not already registered
    if (findNodeDepth(innovation_number) != -3) {
      if (Config.DEBUG) {
        System.out.println("Already registered here at NodeDepthManager");
      }
      return;
    }
    
    // This is the position that we're adding behind
    int target_loc = findNodeDepth(parent_node_number);
    
    // Check to make sure we found the node
    if (target_loc == -3) {
      if (Config.DEBUG) {
        System.out.println("FAILED TO FIND PARENT NODE");
      }
      return;
    }
    // Check to make sure we didn't find an output node
    if (target_loc == -2) {
      if (Config.DEBUG) {
        System.out.println("Found an output node as parent node?");
      }
      return;
    }
    /*
    // do stuff if the node is input
    if (target_loc == -1) {
      target_loc = 0;
    }
    */
    
    // Create the new array with a space for our new node.
    int[] n = new int[node_depths.length + 1];
    
    /*
    // Now we need to transfer over all the old values in place.
    for (int i = 0; i < pn_i; i++) {
      n[i] = node_depths[i];
    }
    // Now we add our value.
    n[pn_i] = innovation_number;
    // Finally, we need to add back all the numbers after
    for (int i = pn_i; i < node_depths.length; i++) {
      n[i+1] = node_depths[i];
    }
    */
    
    for (int i = 0; i < n.length; i++) {
      if (i <= target_loc) {
        n[i] = node_depths[i];
      } else if (i == target_loc + 1) {
        n[i] = innovation_number;
      } else {
        n[i] = node_depths[i - 1];
      }
    }
    
    node_depths = n;
    return;
  }
  
  public int findNodeDepth(int node_innovation_number) {
    // See if it's in the main collection.
    for (int i = 0; i < node_depths.length; i++) {
      if (node_innovation_number == node_depths[i]) {
        return i;
      }
    }
    // See if it's an input node
    for (int i : network_format.input_innovation_numbers) {
      if (i == node_innovation_number) {
        return -1;
      }
    }
    // ...See if it's an output node?
    for (int i : network_format.output_innovation_numbers) {
      if (i == node_innovation_number) {
        return -2;
      }
    }
    // Failed to find it.
    return -3;
  }
  
}
