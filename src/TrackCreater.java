import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.Scanner;

public class TrackCreater implements Runnable {
  
  private Display display;
  private Scanner scanner;
  private TrackPlacer tp;
  private boolean isCreatingTrack;
  
  public static void main(String[] args) {
    TrackCreater tc = new TrackCreater();
    tc.setup();
    // Start the main thread
    new Thread(tc).start();
    // Get input
    tc.getInput();
  }
  
  public TrackCreater() {
    display = new Display();
    scanner = new Scanner(System.in);
  }
  
  private void setup() {
    /*
    // Attempt to load the track
    Track t;
    t = TrackLoader.Load("default");
    if (t != null) {
      track = new TrackController(Config.windowSize.width, Config.windowSize.height, t);
      track.generateFull();
      isCreatingTrack = false;
    } else {
    
      // Make the track placer!
      tp = new TrackPlacer(Config.windowSize.width, Config.windowSize.height);
      isCreatingTrack = true;
      display.addKeyListener(tp);
      display.addMouseListener(tp);
      display.canvas.addMouseListener(tp);
    }
    */
    tp = new TrackPlacer(Config.windowSize.width, Config.windowSize.height);
    isCreatingTrack = true;
    display.addKeyListener(tp);
    display.addMouseListener(tp);
    display.canvas.addMouseListener(tp);
  }
  
  /**
   * Reads in input from scanner and acts accordingly
   */
  public void getInput() {
    
    while (true) {
      String in = scanner.nextLine();
      System.out.println(in);
      handleInput(in);
    }
    
  }
  
  /**
   * Handles all user console input
   *
   * @param in the user input
   */
  private void handleInput(String in) {
    if (!isCreatingTrack) {
      
      // Now we check the input to make sure it only uses valid characters.
      if (checkTrackNameValidity(in)) {
        //Name is valid. Now to save.
        TrackLoader.Save(tp.generateTrack().getTrack(), in);
      } else {
        System.out.println("Invalid name");
      }
      
    } else {
      System.out.println("The track is not finished.");
    }
  }
  
  
  //private static final char[] valid_characters = new char[] {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '_', '-'};
  
  private static final String my_regex = "^[a-zA-Z0-9_.-]+$";
  
  /**
   * Checks to ensure that the given input follows track naming guidelines
   *
   * @param in the input to check
   * @return whether the input follows the guidelines
   */
  private boolean checkTrackNameValidity(String in) {
    // hashset
    /*
    for (char i : valid_characters) {
      if (in.matches()) {
        return false;
      }
    }
    */
    return in.matches(my_regex);
  }
  
  @Override
  public void run() {
    // Get the time since our last update
    long pastUpdateTime = System.nanoTime();
    double delayTime = updateDelayTime(Config.updates_per_second);
    
    while (true) {
      
      // If the time since out last update is greater than the time per update, ... update
      if (System.nanoTime() - pastUpdateTime > delayTime) {
        pastUpdateTime += delayTime;
        // Here is where all update code will be located
        //TODO
        /*
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
        */
        
        if (isCreatingTrack) {
          if (tp.isTrackPlacementComplete()) {
            isCreatingTrack = false;
          }
          tp.render();
        } else {
          // Now we try to get the name!
          // We don't need to render anymore. We're just waiting on user input.
        }
        
      }
      
      draw();
    }
  }
  
  private double updateDelayTime(int _updates_per_second) {
    return Math.pow(10, 9) / _updates_per_second;
  }
  
  private void draw() {
    BufferStrategy b = display.canvas.getBufferStrategy();
    if (b == null) {
      display.canvas.createBufferStrategy(2);
      b = display.canvas.getBufferStrategy();
    }
    
    Graphics g = b.getDrawGraphics();
    
    tp.draw(g);
    if (!isCreatingTrack) {
      g.drawString("Type track name in console.", 20, 20);
    }
    
    g.dispose();
    b.show();
  }
  
}
