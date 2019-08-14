package AAI;

import CarBase.Config;
import CarBase.Line;
import CarBase.Point;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AGenome implements Serializable {
  
  private ArrayList<AConnectionGene> connection_genes;
  private ArrayList<ANodeGene> node_genes;
  
  private int generation_number;
  
  public float fitness;
  
  
  /**
   * @Deprecated public AGenome(int number_input, int number_output, int generation_number) {
   * node_genes = new ArrayList<>();
   * connection_genes = new ArrayList<>();
   * this.generation_number = generation_number;
   * <p>
   * for (int i = 0; i < number_input; i++) {
   * // We need to create a new input node
   * node_genes.add(new ANodeGene(ANodeGene.Type.INPUT, AMutationManager.getInstance().input_innovation_numbers[i]));
   * }
   * <p>
   * for (int i = 0; i < number_output; i++) {
   * // We need to create a new output node
   * node_genes.add(new ANodeGene(ANodeGene.Type.OUTPUT, AMutationManager.getInstance().output_innovation_numbers[i]));
   * }
   * <p>
   * }
   **/
  
  public AGenome(ANetworkFormat genomeFormat, int generation_number) {
    node_genes = new ArrayList<>();
    connection_genes = new ArrayList<>();
    this.generation_number = generation_number;
    
    // Add the input nodes based off the network format
    for (int inno : genomeFormat.input_innovation_numbers) {
      node_genes.add(new ANodeGene(ANodeGene.Type.INPUT, inno));
    }
    
    // Add the output nodes based off the network format
    for (int inno : genomeFormat.output_innovation_numbers) {
      node_genes.add(new ANodeGene(ANodeGene.Type.OUTPUT, inno));
    }
  }
  
  private static final int hiddenNodeColor = 0x00ff00;
  private static final int inputNodeColor = 0x0000ff;
  private static final int outputNodeColor = 0xff0000;
  private static final int connectionColor = 0x000000;
  private static final int backgroundColor = 0xffffff;
  private static final int nodeRadius = 20;
  private static final int connectionEndBoxRadius = 5;
  private static final int connectionEndRadius = 40;
  private static final Dimension imageSize = new Dimension(1200, 1000);
  private static final Dimension sideBuffer = new Dimension(55, 55);
  private static final int inputAndOutputOffset = 60;
  
  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (!(obj instanceof AGenome)) return false;
    AGenome other = (AGenome) obj;
    return
      generation_number == other.generation_number &&
        fitness == other.fitness &&
        connection_genes.equals(other.connection_genes) &&
        node_genes.equals(other.node_genes);
  }
  
  /**
   * Gets a randomly generated image of the network
   *
   * @param r the random instance to use
   * @return the image representing the network
   */
  public BufferedImage createImage(Random r) {
    // Create a random instance
    //Random r = new Random();
    // Create an image
    Dimension size = imageSize;
    BufferedImage im = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
    int[] p = ((DataBufferInt) im.getRaster().getDataBuffer()).getData();
    for (int i = 0; i < p.length; i++) {
      p[i] = backgroundColor;
    }
    // We need to draw every node
    for (ANodeGene g : node_genes) {
      int x = getRandomInRange(r, sideBuffer.width, size.width - sideBuffer.width);
      int y = getRandomInRange(r, sideBuffer.height, size.height - sideBuffer.height);
      int color = hiddenNodeColor;
      if (g.node_type == ANodeGene.Type.INPUT) {
        // All the input nodes we'll keep on the left side
        x = inputAndOutputOffset;
        color = inputNodeColor;
      }
      if (g.node_type == ANodeGene.Type.OUTPUT) {
        // All the output nodes we'll keep on the right side
        x = size.width - inputAndOutputOffset;
        color = outputNodeColor;
      }
      g.x = x;
      g.y = y;
      // Draw the nodes
      p = drawSingleNode(g, color, nodeRadius, p, size.width);
      if (Config.NETWORK_DRAW_DEBUG)
        System.out.println("Drew node at "
          + String.valueOf(g.x) + ", "
          + String.valueOf(g.y));
    }
    // We now need to draw every connection
    for (AConnectionGene g : connection_genes) {
      if (g.isEnabled()) {
        // We're gonna reuse some code with Point and Line
        // We also want a point in the general area of the node, not directly on it.
        Point p1 = getAdjustedPoint(r, new Point(g.in_node.x, g.in_node.y));
        Point p2 = getAdjustedPoint(r, new Point(g.out_node.x, g.out_node.y));
        Line l = new Line(p1, p2);
        p = l.traceBetweenPoints(p, size.width, connectionColor, 0.5f);
        // Draw a box at the output end
        drawBox(connectionColor, connectionEndBoxRadius, p2.getX(), p2.getY(), p, size.width);
        if (Config.NETWORK_DRAW_DEBUG)
          System.out.println("Drew connection");
      }
    }
    return im;
  }
  
  private int getRandomInRange(Random r, int low, int high) {
    return r.nextInt(high - low) + low;
  }
  
  private Point getAdjustedPoint(Random r, Point p) {
    double rot = r.nextDouble() * Math.PI * 2d;
    return p.move((int) (Math.cos(rot) * connectionEndRadius), (int) (Math.sin(rot) * connectionEndRadius));
  }
  
  private int[] drawSingleNode(ANodeGene g, int color, int radius, int[] p, int pwidth) {
    // We'll just draw a square
    return drawBox(color, radius, g.x, g.y, p, pwidth);
  }
  
  private int[] drawBox(int color, int radius, int x, int y, int[] p, int pwidth) {
    int x1 = 0;
    int y1 = 0;
    try {
      for (y1 = -(radius - 1) / 2; y1 <= radius / 2; y1++) {
        for (x1 = -(radius - 1) / 2; x1 <= radius / 2; x1++) {
          try {
            p[(x1 + x) + (y1 + y) * pwidth] = color;
          } catch (ArrayIndexOutOfBoundsException e) {
            //e.printStackTrace();
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      if (Config.NETWORK_DRAW_DEBUG) {
        System.out.println("Failed to set pixel at "
          + String.valueOf(x1 + x) + ", "
          + String.valueOf(y1 + y));
      }
    }
    return p;
  }
  
  /**
   * Automatically creates and saves an image representing the network to a file
   *
   * @param r        the random instance to use
   * @param filename the name of the file to save the image to
   */
  public void SaveImage(Random r, String filename) {
    BufferedImage i = createImage(r);
    try {
      ImageIO.write(i, "png", new File(filename));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void PrintGenome() {
    // We're gonna attempt to print this in a readable format
    System.out.println(" -- Nodes: -- ");
    for (ANodeGene g : node_genes) {
      System.out.println(
        "#" + String.valueOf(g.getInnovationNumber()) + ": located at x: " + String.valueOf(g.x) + " y: " + String.valueOf(g.y)
      );
    }
    System.out.println(" -- Connections: -- ");
    for (AConnectionGene g : connection_genes) {
      System.out.println(
        "#" + String.valueOf(g.getInnovationNumber()) +
          ": starts at " + String.valueOf(g.in_node.getInnovationNumber()) +
          " and ends at " + String.valueOf(g.out_node.getInnovationNumber()) +
          " with weight " + String.valueOf(g.weight) +
          " and is " + (g.isEnabled() ? "ENABLED" : "DISALBED"));
    }
  }
  
  /**
   * Returns a random weight value for use
   * with a connection
   *
   * @param r the random instance to use
   * @return a random weight value
   */
  private float getRandomWeight(Random r) {
    return (r.nextFloat() - 0.5f) * 2f;
  }
  
  /**
   * @return the number of legal connections that the current genome can have
   */
  private int getMaximumNumberOfLegalConnections() {
    // Get the number of all the valid connection types
    int num_input = 0;
    int num_output = 0;
    int num_hidden = 0;
    for (ANodeGene n : node_genes) {
      switch (n.node_type) {
        case INPUT:
          num_input++;
          break;
        case HIDDEN:
          num_hidden++;
          break;
        case OUTPUT:
          num_output++;
          break;
      }
    }
    return performCalculationOfNumberOfLegalConnections(num_input, num_output, num_hidden);
  }
  
  /**
   * The formula for computing number of valid connections
   *
   * @param in the number of input nodes
   * @param on the number of output nodes
   * @param hn the number of hidden nodes
   * @return the number of possible legal connections a genome of specified structure can have
   */
  public static int performCalculationOfNumberOfLegalConnections(int in, int on, int hn) {
    int total = (in * (on + hn)) + (hn * on);
    for (int i = 2; i <= hn; i++) {
      total += (i - 1);
    }
    return total;
  }
  
  /**
   * @return whether this genome is currently capable of having a new connection
   */
  public boolean canCreateNewConnection() {
    return getMaximumNumberOfLegalConnections() != connection_genes.size();
  }
  
  // todo::rework how random connections are chosen.
  
  /**
   * Adds a random Connection to the Genome. 100% success.
   *
   * @param r the random instance to use
   */
  public void forceMutateConnection(Random r, ANodeDepthManager depthManager, AMutationManager mutationManager) {
    // Make sure that creating a new gene is possible
    // to avoid getting stuck in a loop
    if (!canCreateNewConnection()) {
      if (Config.DEBUG) {
        System.out.println("forceMutateConnection was called when no connections are possible");
      }
      return;
    }
    
    boolean s = mutateConnection(r, depthManager, mutationManager);
    while (!s) {
      s = mutateConnection(r, depthManager, mutationManager);
    }
    if (Config.DEBUG) {
      System.out.println("Successfully forced Connection mutation!");
    }
    return;
  }
  
  /**
   * Attempts to add a random Connection to the Genome. Success is not guaranteed.
   *
   * @param r the random instance to use
   * @return whether the connection was added successfully
   */
  public boolean mutateConnection(Random r, ANodeDepthManager depthManager, AMutationManager mutationManager) {
    // Get the two attaching nodes
    ANodeGene in_node = node_genes.get(r.nextInt(node_genes.size()));
    ANodeGene out_node = node_genes.get(r.nextInt(node_genes.size()));
    float weight = getRandomWeight(r);
    
    if (in_node == out_node) {
      if (Config.DEBUG) {
        System.out.println("Failed to add connection: Selected the same node");
      }
      return false;
    }

//    // Reverse the node direction if needed
//    boolean doReverse = false;
//    if (in_node_innovation_number.node_type == ANodeGene.Type.OUTPUT) {
//      if (out_node_innovation_number.node_type == ANodeGene.Type.OUTPUT) {
//        if (CarBase.Config.DEBUG)
//          System.out.println("Failed to add connection: connection is between two output");
//        return false;
//      }
//      doReverse = true;
//    }
//    if (out_node_innovation_number.node_type == ANodeGene.Type.INPUT) {
//      if (in_node_innovation_number.node_type == ANodeGene.Type.INPUT) {
//        if (CarBase.Config.DEBUG)
//          System.out.println("Failed to add connection: connection is between two input");
//        return false;
//      }
//      doReverse = true;
//    }
//    if (doReverse) {
//      ANodeGene temp = out_node_innovation_number;
//      out_node_innovation_number = in_node_innovation_number;
//      in_node_innovation_number = temp;
//    }
    
    boolean doReverse = false;
    if (in_node.node_type == ANodeGene.Type.OUTPUT) {
      if (out_node.node_type == ANodeGene.Type.OUTPUT) {
        if (Config.DEBUG)
          System.out.println("Failed to add connection: connection is between two output");
        return false;
      }
      doReverse = true;
    } else if (out_node.node_type == ANodeGene.Type.INPUT) {
      if (in_node.node_type == ANodeGene.Type.INPUT) {
        if (Config.DEBUG)
          System.out.println("Failed to add connection: connection is between two input");
        return false;
      }
      doReverse = true;
    }
    // Make sure we don't move down the tree by checking the depth manager
    // Only for hidden nodes
    if (in_node.node_type == ANodeGene.Type.HIDDEN && out_node.node_type == ANodeGene.Type.HIDDEN) {
      int in_node_depth = depthManager.findNodeDepth(in_node.getInnovationNumber());
      int out_node_depth = depthManager.findNodeDepth(out_node.getInnovationNumber());
      if (out_node_depth < in_node_depth) {
        doReverse = true;
      }
    }
    if (doReverse) {
      ANodeGene temp = out_node;
      out_node = in_node;
      in_node = temp;
    }
    
    // Make sure the nodes aren't already connected
    for (AConnectionGene g : connection_genes) {
      if (g.in_node == in_node && g.out_node == out_node) {
        if (Config.DEBUG)
          System.out.println("Failed to add connection: connection already exists");
        return false;
      }
    }
    
    connection_genes.add(
      new AConnectionGene(
        in_node,
        out_node,
        weight,
        true,
        mutationManager.getAppropriateInnovationNumber(generation_number, in_node.getInnovationNumber(), out_node.getInnovationNumber(), true)
      )
    );
    
    if (Config.DEBUG)
      System.out.println("Finished adding a connection");
    
    return true;
  }
  
  public AConnectionGene findRandomEnbaledConnectionGene(Random r) {
    // check to make sure such a node exists in the first place.
    boolean exists = false;
    for (AConnectionGene g : connection_genes) {
      if (g.isEnabled()) {
        exists = true;
        break;
      }
    }
    if (!exists) return null;
    while (true) {
      AConnectionGene g = connection_genes.get(r.nextInt(connection_genes.size()));
      if (g.isEnabled()) {
        return g;
      }
    }
  }
  
  /**
   * Adds a new Node to the Genome. 100% success.
   *
   * @param r the random instance to use
   */
  public void mutateNode(Random r, ANodeDepthManager depthManager, AMutationManager mutationManager) {
    // Get the connection we will replace
    AConnectionGene con = findRandomEnbaledConnectionGene(r);
    // Set the connection to disabled
    con.disableConnection();
    // Create the new node
    ANodeGene n = new ANodeGene(ANodeGene.Type.HIDDEN, mutationManager.getAppropriateInnovationNumber(generation_number, con.in_node.getInnovationNumber(), con.out_node.getInnovationNumber(), false));
    // Register the new node with the NodeDepthManager
    depthManager.addNode(n.getInnovationNumber(), con.in_node.getInnovationNumber());
    node_genes.add(n);
    // Create the two new connections to connect the two other nodes ot the new one
    AConnectionGene c1 =
      new AConnectionGene(
        con.in_node,
        n,
        1,
        true,
        mutationManager.getAppropriateInnovationNumber(generation_number, con.in_node.getInnovationNumber(), n.getInnovationNumber(), true)
      );
    
    AConnectionGene c2 =
      new AConnectionGene(
        n,
        con.out_node,
        con.weight,
        true,
        mutationManager.getAppropriateInnovationNumber(generation_number, n.getInnovationNumber(), con.out_node.getInnovationNumber(), true)
      );
    
    connection_genes.add(c1);
    connection_genes.add(c2);
    
    
    if (Config.DEBUG)
      System.out.println("Finished adding a node");
    return;
  }
  
  /**
   * Adds a connection gene to the genome
   *
   * @param g the connection gene
   * @return whether the gene was successfully added
   */
  public boolean addConnectionGene(AConnectionGene g) {
    if (hasConnectionGene(g)) {
      return false;
    }
    connection_genes.add(g);
    return true;
  }
  
  /**
   * Adds a node gene to the genome
   *
   * @param g the connection gene
   * @return whether the gene was successfully added
   */
  public boolean addNodeGene(ANodeGene g) {
    if (hasNodeGene(g)) {
      return false;
    }
    node_genes.add(g);
    return true;
  }
  
  /**
   * Finds whether the gene currently exists in the genome
   *
   * @return whether the gene exists
   */
  public boolean hasConnectionGene(AConnectionGene g) {
    // Check to make sure the innovation number does not already
    //  exist on a connection and the connection has not already been made
    for (AConnectionGene gene : connection_genes) {
      if (gene.getInnovationNumber() == g.getInnovationNumber()) {
        return true; // The gene has already been added
      }
      if (gene.in_node == g.in_node && gene.out_node == g.out_node) {
        return true; // The connection already exists
      }
    }
    return false;
  }
  
  /**
   * Finds whether the gene currently exists in the genome
   *
   * @return whether the node gene exists
   */
  public boolean hasNodeGene(ANodeGene g) {
    // Check to make sure the innovation number does not already exist
    for (ANodeGene gene : node_genes) {
      if (gene.getInnovationNumber() == g.getInnovationNumber()) {
        return true; // The gene has already been added
      }
    }
    return false;
  }
  
  /**
   * Finds a connection gene based off of an innovation number
   *
   * @param innovation The innovation number
   * @return The gene
   */
  public AConnectionGene findConnectionGeneByInnovationNumber(int innovation) {
    for (AConnectionGene g : connection_genes) {
      if (g.getInnovationNumber() == innovation) {
        return g;
      }
    }
    return null;
  }
  
  /**
   * Finds a connection gene based off of an innovation number
   *
   * @param innovation The innovation number
   * @return The gene
   */
  public ANodeGene findNodeGeneByInnovationNumber(int innovation) {
    for (ANodeGene g : node_genes) {
      if (g.getInnovationNumber() == innovation) {
        return g;
      }
    }
    return null;
  }
  
  /**
   * Needs to be called after adding connections that may potentially point to nodes not present in the genome
   * Iterates over every connection gene and finds the appropriate node object
   */
  public void relinkConnections() {
    for (AConnectionGene g : connection_genes) {
      ANodeGene ng = findNodeGeneByInnovationNumber(g.in_node.getInnovationNumber());
      if (ng == null) {
        System.out.println("We have a problem.");
        System.exit(-1);
      }
      g.in_node = ng;
      
      ng = findNodeGeneByInnovationNumber(g.out_node.getInnovationNumber());
      if (ng == null) {
        System.out.println("We have a problem x2");
        System.exit(-1);
      }
      g.out_node = ng;
    }
  }
  
  public List<AConnectionGene> getConnectionGenes() {
    return this.connection_genes;
  }
  
  public List<ANodeGene> getNodeGenes() {
    return this.node_genes;
  }
  
}
