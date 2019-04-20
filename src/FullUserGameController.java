import java.awt.*;

/**
 * Contains:
 * Collection of points
 * Car
 * <p>
 * Progression:
 * First, create a track.
 * <p>
 * To create a track:
 * 1  Place the outer points of the track. When done, press enter.
 * 2  Place the inner points of the track. When done, press enter.
 * 3  Place the checkpoints. Each checkpoint consists of two points.
 * Checkpoints must be placed in order. When done, press enter to
 * finish creating tack.
 */

public class FullUserGameController extends GameController implements Runnable {
  
  private UserCar car;
  
  private boolean isCreatingTrack = true;
  
  private TrackController track;
  private TrackPlacer tp;
  
  public FullUserGameController() {
    super();
  }
  
  @Override
  public void setup() {
    //car = new Car();
    //car.setUpdatesPerSecond(updates_per_second);
    car = createUserCar(40, 60);
    //car2 = createUserCar(100, 60);
    //car2.makePlayerTwo();
    
    // Attempt to load the track
    Track t = TrackLoader.load("default");
    if (t != null) {
      track = new TrackController(Config.windowSize.width, Config.windowSize.height, t);
      track.generateFull();
      isCreatingTrack = false;
      CarSpawnPoint sp = track.getTrack().carSpawnPoint;
      System.out.println(sp.center.getX() + " " + sp.center.getY());
      car.getCar().setPosition(sp.center.getX(), sp.center.getY());
      System.out.println(sp.rotation);
      car.getCar().setRadianRotation(sp.rotation);
    } else {
      
      // Make the track placer!
      tp = new TrackPlacer(Config.windowSize.width, Config.windowSize.height);
      isCreatingTrack = true;
      addTrackPlacerListeners(tp);
    }
  }
  
  private UserCar createUserCar(int posx, int posy) {
    UserCar c = new UserCar(posx, posy, Config.updates_per_second);
    addUserCarListeners(c);
    return c;
  }
  
  private void getTrackFromPlacer() {
    track = tp.generateTrack();
    CarSpawnPoint sp = track.getTrack().carSpawnPoint;
    System.out.println(sp.center.getX() + " " + sp.center.getY());
    car.getCar().setPosition(sp.center.getX(), sp.center.getY());
    car.getCar().setRadianRotation(sp.rotation);
    removeTrackPlacerListeners(tp);
    isCreatingTrack = false;
    TrackLoader.save(track.getTrack(), "default");
  }
  
  @Override
  public void update() {
    if (isCreatingTrack) {
      if (tp.isTrackPlacementComplete()) {
        getTrackFromPlacer();
      } else {
        tp.render();
      }
    } else {
      car.update();
      //car2.update();
      testCarCollision(car.getCar());
    }
  }
  
  private void testCarCollision(Car c) {
    // Test collisions with the edges of the track.
    if (track.testCollision(c)) {
      System.out.println("Collision!");
    }
    // Test for checkpoints!
    int n = track.testCheckpoints(c);
    if (n != -1) {
      System.out.println("Hit checkpoint #" + n);
    }
  }
  
  @Override
  public void draw(Graphics g) {
    if (isCreatingTrack) {
      tp.draw(g);
    } else {
  
      // Draw background
      //g.drawImage(image, 0, 0, Config.windowSize.width, Config.windowSize.height, null);
  
      // Draw track
      // TODO draw track
      track.draw(g);
  
      // Draw car
      car.draw(g);
      //car2.draw(g);
  
    }
  }
  
}
