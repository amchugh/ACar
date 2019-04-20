import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.Scanner;

public class TrackCreator implements Runnable {
  
  private Display display;
  private Scanner scanner;
  private TrackPlacer tp;
  
  private enum TrackCreatorState {CREATING_TRACK, WAITING_NAME, WAITING_CONTINUE}
  
  private TrackCreatorState state;
  
  public static void main(String[] args) {
    TrackCreator tc = new TrackCreator();
    tc.setup();
    // Start the main thread
    new Thread(tc).start();
    // Get input
    tc.getInput();
  }
  
  public TrackCreator() {
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
    state = TrackCreatorState.CREATING_TRACK;
    display.addKeyListener(tp);
    display.addMouseListener(tp);
    display.canvas.addMouseListener(tp);
    System.out.println("Please create a track in the window.");
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
    /*
    if (state == TrackCreatorState.WAITING_NAME) {
      
      // Now we check the input to make sure it only uses valid characters.
      if (checkTrackNameValidity(in)) {
        //Name is valid. Now to save.
        if (TrackLoader.Save(tp.generateTrack().getTrack(), in)) {
          // Track was successfully saved.
          // Ask if the user would like to create another track
        }
      } else {
        System.out.println("Invalid name");
      }
      
    } else if (state == TrackCreatorState.CREATING_TRACK) {
      System.out.println("The track is not finished.");
    }
    */
    switch (state) {
    
      case CREATING_TRACK:
        System.out.println("The track is not finished.");
        break;
      case WAITING_NAME:
        // Now we check the input to make sure it only uses valid characters.
        if (checkTrackNameValidity(in)) {
          //Name is valid. Now to save.
          if (TrackLoader.Save(tp.generateTrack().getTrack(), in)) {
            // Track was successfully saved.
            // Ask if the user would like to create another track
            System.out.print("Would you like to create another track? ");
            state = TrackCreatorState.WAITING_CONTINUE;
          }
        } else {
          System.out.println("Invalid name");
        }
        break;
      case WAITING_CONTINUE:
        // Check the input to see if the user would like to continue.
        if (!in.isEmpty()) {
          if (Character.toLowerCase(in.charAt(0)) == 'y') {
            // We want to continue.
            setup();
            break;
          }
        }
        // We don't want to continue.
        System.exit(1);
        break;
    
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
  
  private void removeTrackPlacerListeners() {
    display.removeMouseListener(tp);
    display.removeKeyListener(tp);
    display.canvas.removeMouseListener(tp);
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
  
        if (state == TrackCreatorState.CREATING_TRACK) {
          if (tp.isTrackPlacementComplete()) {
            System.out.print("What is the track name? ");
            state = TrackCreatorState.WAITING_NAME;
            removeTrackPlacerListeners();
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
    if (state == TrackCreatorState.WAITING_NAME) {
      g.drawString("Type track name in console.", 20, 20);
    }
    
    g.dispose();
    b.show();
  }
  
}