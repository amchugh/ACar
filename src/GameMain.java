import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Arrays;

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

public class GameMain extends JFrame implements Runnable {
  
  public static final Dimension windowSize = new Dimension(900, 800);
  private static int updates_per_second = 60;
  
  private Canvas canvas;
  private UserCar car;
  private UserCar car2;
  //private Car car;
  
  private boolean isCreatingTrack = true;
  
  private TrackController track;
  private TrackPlacer tp;
  
  private BufferedImage image;
  
  public GameMain() {
    this.setTitle("");
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setResizable(false);
    
    canvas = new Canvas();
    
    canvas.setPreferredSize(windowSize);
    canvas.setFocusable(false);
    
    // Create the background image
    image = new BufferedImage(windowSize.width, windowSize.height, BufferedImage.TYPE_INT_RGB);
    int[] colorData = new int[windowSize.width * windowSize.height];
    Arrays.fill(colorData, 0xffffff);
    image.setRGB(0, 0, windowSize.width, windowSize.height, colorData, 0, 0);
    
    this.add(canvas);
    this.pack();
    this.setLocation(50, 50); // Position on screen
    this.setVisible(true);
    
    Setup();
    
    // Start the main thread
    new Thread(this).start();
  }
  
  private void Setup() {
    //car = new Car();
    //car.setUpdatesPerSecond(updates_per_second);
    car = createUserCar(40, 60);
    //car2 = createUserCar(100, 60);
    //car2.makePlayerTwo();
    
    // Attempt to load the track
    Track t;
    t = TrackLoader.Load("default");
    if (t != null) {
      track = new TrackController(windowSize.width, windowSize.height, t);
      track.generateFull();
      isCreatingTrack = false;
    } else {
      
      // Make the track placer!
      tp = new TrackPlacer(windowSize.width, windowSize.height);
      isCreatingTrack = true;
      this.addKeyListener(tp);
      this.addMouseListener(tp);
      canvas.addMouseListener(tp);
    }
  }
  
  private UserCar createUserCar(int posx, int posy) {
    UserCar c = new UserCar(posx, posy, updates_per_second);
    this.addMouseListener(c);
    this.addKeyListener(c);
    return c;
  }
  
  private void getTrackFromPlacer() {
    track = tp.generateTrack();
    this.removeMouseListener(tp);
    this.removeKeyListener(tp);
    canvas.removeMouseListener(tp);
    isCreatingTrack = false;
    TrackLoader.Save(track.getTrack(), "default");
  }
  
  public void run() {
    // Get the time since our last update
    long pastUpdateTime = System.nanoTime();
    double delayTime = updateDelayTime(updates_per_second);
    
    while (true) {
      
      // If the time since out last update is greater than the time per update, ... update
      if (System.nanoTime() - pastUpdateTime > delayTime) {
        pastUpdateTime += delayTime;
        // Here is where all update code will be located
        //TODO
        if (isCreatingTrack) {
          TrackPlacer.PlacingPhase p = tp.getPhase();
          if (p == TrackPlacer.PlacingPhase.COMPLETE) {
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
      
      draw();
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
  
  private double updateDelayTime(int _updates_per_second) {
    return Math.pow(10, 9) / _updates_per_second;
  }
  
  private void draw() {
    BufferStrategy b = canvas.getBufferStrategy();
    if (b == null) {
      canvas.createBufferStrategy(2);
      b = canvas.getBufferStrategy();
    }
    
    Graphics g = b.getDrawGraphics();
    
    if (isCreatingTrack) {
      
      tp.draw(g);
      
    } else {
      
      // Draw background
      //g.drawImage(image, 0, 0, windowSize.width, windowSize.height, null);
      
      // Draw track
      // TODO draw track
      track.draw(g);
      
      // Draw car
      car.draw(g);
      //car2.draw(g);
      
    }
    g.dispose();
    b.show();
  }
  
}
