import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.Scanner;

public abstract class GameController {
  
  private Display display;
  
  public GameController() {
    display = new Display();
    display.setVisible(true);
  }
  
  public GameController(boolean showDisplay) {
    display = new Display();
    display.setVisible(showDisplay);
  }
  
  // Define non-abstract methods.
  public void run() {
    // Get the time since our last update
    long pastUpdateTime = System.nanoTime();
    double delayTime = updateDelayTime(Config.updates_per_second);
    
    while (true) {
      
      // If the time since out last update is greater than the time per update, ... update
      if (System.nanoTime() - pastUpdateTime > delayTime) {
        pastUpdateTime += delayTime;
        update();
      }
      
      handleDraw();
    }
  }
  
  private final double updateDelayTime(int _updates_per_second) {
    return Math.pow(10, 9) / _updates_per_second;
  }
  
  private void handleDraw() {
    BufferStrategy b = display.canvas.getBufferStrategy();
    if (b == null) {
      display.canvas.createBufferStrategy(2);
      b = display.canvas.getBufferStrategy();
    }
    
    Graphics g = b.getDrawGraphics();
    
    draw(g);
    
    g.dispose();
    b.show();
  }
  
  public void addTrackPlacerListeners(TrackPlacer tp) {
    display.addKeyListener(tp);
    display.addMouseListener(tp);
    display.canvas.addMouseListener(tp);
  }
  
  public void removeTrackPlacerListeners(TrackPlacer tp) {
    display.removeMouseListener(tp);
    display.removeKeyListener(tp);
    display.canvas.removeMouseListener(tp);
  }
  
  public void addUserCarListeners(UserCar c) {
    display.addMouseListener(c);
    display.addKeyListener(c);
  }
  
  public void removeUserCarListeners(UserCar c) {
    display.removeMouseListener(c);
    display.removeKeyListener(c);
  }
  
  public void setDisplayVisibility(boolean showDisplay) {
    display.setVisible(showDisplay);
    display.requestFocus();
  }
  
  public Track getTrackFromUserInput() {
    Scanner sc = new Scanner(System.in);
    Track t;
    
    while (true) {
      System.out.println("What track would you like to play on? ");
      String maybeName = sc.nextLine();
      if (TrackLoader.checkTrackNameValidity(maybeName)) {
        t = TrackLoader.load(maybeName);
        if (t != null) {
          // Successfully loaded the track.
          return t;
        }
      } else {
        System.out.println("Invalid name. Please try again (regex: \"[a-zA-Z0-9_.-]\" )");
      }
    }
  }
  
  // Getter methods
  public Display getDisplay() {
    return display;
  }
  
  // Define abstract methods.
  public abstract void setup();
  
  public abstract void update();
  
  public abstract void draw(Graphics g);
  
}
