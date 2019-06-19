package AAI;

public class Test {
  
  public static void main(String[] args) {
    AGenomeManager m = new AGenomeManager(2, 1, 50, 1);
    m.loadGeneration(0, "test_0");
    m.generateGenomeImages(0, "test_1", ".png", 1, true);
    //m.saveGeneration(0, "test_0");
    /*
    AGenomeManager m = new AGenomeManager(2,1);
    Random r;
    int image_rand = 7;
    int num = 25;
    String folder = "child_images2\\";
    for (int i = 1; i <= num; i++ ) {
      r = new Random(i);
      AGenome g = m.addNewRandomGenome();
      g.forceMutateConnection(r);
      g.mutateNode(r);
      g.forceMutateConnection(r);
      g.mutateNode(r);
      g.forceMutateConnection(r);
      g.forceMutateConnection(r);
      g.mutateNode(r);
      g.forceMutateConnection(r);
      g.mutateNode(r);
      g.forceMutateConnection(r);
      g.mutateNode(r);
      g.forceMutateConnection(r);
      g.forceMutateConnection(r);
      
      r = new Random(i+num);
      AGenome g2 = m.addNewRandomGenome();
      g2.forceMutateConnection(r);
      g2.forceMutateConnection(r);
      g2.mutateNode(r);
      g2.forceMutateConnection(r);
      
      r = new Random(i+num*3);
      if (r.nextFloat() < 0.5f) {
        g2.fitness = 100;
      } else {
        g.fitness = 100;
      }
      
      r = new Random(i+num*2);
      AGenome child = m.breedGenomes(r, g, g2);
      
      r = new Random(image_rand);
      g.SaveImage(r, folder + "male-network_" + String.valueOf(i) + ".png");
      r = new Random(image_rand);
      g2.SaveImage(r, folder + "female-network_" + String.valueOf(i) + ".png");
      r = new Random(image_rand);
      child.SaveImage(r, folder + "child-network_" + String.valueOf(i) + ".png");
      System.out.println("done with " + String.valueOf(i));
  
      for (AConnectionGene check : child.getConnectionGenes()) {
        for (AConnectionGene check2 : child.getConnectionGenes()) {
          if (check == check2) {
        
          } else {
            if(check.in_node_innovation_number == check2.out_node_innovation_number && check.out_node_innovation_number == check2.in_node_innovation_number) {
              System.out.println("uhoh");
            }
          }
        }
      }
    }
    /*
    AGenome g1 = m.addNewRandomGenome();
    r = new Random(1);
    g1.forceMutateConnection(r);
    g1.mutateNode(r);
    g1.forceMutateConnection(r);
    g1.mutateNode(r);
    g1.forceMutateConnection(r);
    g1.forceMutateConnection(r);
    g1.mutateNode(r);
    g1.forceMutateConnection(r);
    g1.mutateNode(r);
    g1.forceMutateConnection(r);
    g1.mutateNode(r);
    g1.forceMutateConnection(r);
    g1.forceMutateConnection(r);
    g1.SaveImage(r, "p1.png");
    AGenome g2 = m.addNewRandomGenome();
    r = new Random(2);
    g2.forceMutateConnection(r);
    g2.mutateNode(r);
    g2.forceMutateConnection(r);
    g2.mutateNode(r);
    g2.forceMutateConnection(r);
    g2.forceMutateConnection(r);
    g2.mutateNode(r);
    g2.forceMutateConnection(r);
    g2.mutateNode(r);
    g2.fitness = 1;
    g2.SaveImage(r, "p2.png");
    r = new Random(10);
    AGenome jojo = m.breedGenomes(r, g1, g2);
    for (int i = 0; i < 10; i++) {
      r = new Random(i*10000);
      jojo.SaveImage(r, "children\\jojo" + String.valueOf(i) + ".png");
    }
    jojo.PrintGenome();
    for (AConnectionGene check : jojo.getConnectionGenes()) {
      for (AConnectionGene check2 : jojo.getConnectionGenes()) {
        if (check == check2) {
        
        } else {
          if(check.in_node_innovation_number == check2.out_node_innovation_number && check.out_node_innovation_number == check2.in_node_innovation_number) {
            System.out.println("uhoh");
          }
        }
      }
    }
    
    ANodeDepthManager n = ANodeDepthManager.getInstance();
    return;
    */
  }
  
}
