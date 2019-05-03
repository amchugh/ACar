package CarBase;

import java.awt.*;
import java.util.Scanner;

public class TrackCreator extends GameController implements Runnable {
  
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
    super();
    scanner = new Scanner(System.in);
  }
  
  @Override
  public void setup() {
    tp = new TrackPlacer(Config.windowSize.width, Config.windowSize.height);
    state = TrackCreatorState.CREATING_TRACK;
    addTrackPlacerListeners(tp);
    System.out.println("Please create a track in the window.");
  }
  
  @Override
  public void update() {
    if (state == TrackCreatorState.CREATING_TRACK) {
      if (tp.isTrackPlacementComplete()) {
        System.out.println("What is the track name? ");
        state = TrackCreatorState.WAITING_NAME;
        removeTrackPlacerListeners(tp);
      }
      tp.render();
    } else {
      // Now we try to get the name!
      // We don't need to render anymore. We're just waiting on user input.
    }
  }
  
  @Override
  public void draw(Graphics g) {
    tp.draw(g);
    if (state == TrackCreatorState.WAITING_NAME) {
      g.drawString("Type track name in console.", 20, 20);
    }
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
    switch (state) {
      
      case CREATING_TRACK:
        System.out.println("The track is not finished.");
        break;
      case WAITING_NAME:
        // Now we check the input to make sure it only uses valid characters.
        if (TrackLoader.checkTrackNameValidity(in)) {
          //Name is valid. Now to save.
          if (TrackLoader.save(tp.generateTrack().getTrack(), in)) {
            // Track was successfully saved.
            // Ask if the user would like to create another track
            System.out.println("Would you like to create another track? ");
            state = TrackCreatorState.WAITING_CONTINUE;
          }
        } else {
          System.out.println("Invalid name. Please try again (regex: \"[a-zA-Z0-9_.-]\" )");
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
  
}
