package CarBase;

import java.awt.*;

public class AIGameController extends GameController {
  
  private AICarController carController;
  private TrackController trackController;
  
  public static void main(String[] args) {
    Config.updates_per_second = 120;
    AIGameController game = new AIGameController();
    game.setup();
    game.run();
  }
  
  public AIGameController() {
    // Don't show the window at first.
    super(false);
  }
  
  @Override
  public void setup() {
    // First need to get the trackController to play on.
    Track track = getTrackFromUserInput();
    trackController = new TrackController(
      Config.windowSize.width,
      Config.windowSize.height,
      track);
    trackController.generateFull();
    // Create the car
    carController = new AICarController(Config.updates_per_second);
    // TODO Get Car weights and load them?
    //Weights w = getAIWeightsFromUserInput();
    //carController.load(w);
    // Set the car start point
    carController.getCar().setSpawn(track.carSpawnPoint);
    // Set the display visible
    setDisplayVisibility(true);
  }
  
  @Override
  public void update() {
    carController.update();
    testCarCollision(carController.getCar());
  }
  
  private void testCarCollision(Car c) {
    // Test collisions with the edges of the track.
    if (trackController.testCollision(c)) {
      System.out.println("Collision!");
    }
    // Test for checkpoints!
    int n = trackController.testCheckpoints(c);
    if (n != -1) {
      System.out.println("Hit checkpoint #" + n);
    }
  }
  
  @Override
  public void draw(Graphics g) {
    trackController.draw(g);
    carController.draw(g);
  }
  
}
