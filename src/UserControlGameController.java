import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class UserControlGameController extends GameController {
  
  private TrackController track;
  private UserCar car;
  
  private boolean hasStarted;
  
  private StarterListener sl;
  
  public static void main(String[] args) {
    GameController game = new UserControlGameController();
    game.setup();
    game.run();
  }
  
  public UserControlGameController() {
    super(false);
  }
  
  @Override
  public void setup() {
    // First we need to get the track name.
    track = new TrackController(Config.windowSize.width, Config.windowSize.height, getTrackFromUserInput());
    track.generateFull();
    // Create the Car
    car = new UserCar(Config.updates_per_second);
    // Attach listeners for Car
    addUserCarListeners(car);
    // Set the car SpawnPoint
    CarSpawnPoint sp = track.getTrack().carSpawnPoint;
    car.getCar().setSpawn(sp);
    // Set the display to visible
    setDisplayVisibility(true);
    // We need to force the user to click on the window to give
    // it focus. I plan on just doing a "click to start" thing.
    hasStarted = false;
    sl = new StarterListener(this);
    getDisplay().canvas.addMouseListener(sl);
  }
  
  public void clicked() {
    // We've been clicked and have focus!
    hasStarted = true;
    getDisplay().canvas.removeMouseListener(sl);
  }
  
  @Override
  public void update() {
    car.update();
    //car2.update();
    testCarCollision(car.getCar());
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
    track.draw(g);
    car.draw(g);
    if (!hasStarted) {
      // Dim the screen and show text?
      int c = 0x0f;
      g.setColor(new Color(c, c, c, 0x88));
      //g.setColor(Color.red);
      g.fillRect(0, 0, (int) Config.windowSize.getWidth(), (int) Config.windowSize.getHeight());
      // Draw the text
      g.setColor(Color.white);
      Font f = new Font(Font.SANS_SERIF, Font.ROMAN_BASELINE, 30);
      String message = "Click to start";
      g.setFont(f);
      FontRenderContext frc = new FontRenderContext(new AffineTransform(), false, false);
      Rectangle2D stringBounds = f.getStringBounds(message, frc);
      g.drawString(message,
        (int) (Config.windowSize.getWidth() / 2 - stringBounds.getWidth() / 2),
        (int) (Config.windowSize.getHeight() / 2 - stringBounds.getHeight() / 2));
    }
  }
  
}

class StarterListener implements java.awt.event.MouseListener {
  
  private UserControlGameController ucgc;
  
  public StarterListener(UserControlGameController u) {
    ucgc = u;
  }
  
  @Override
  public void mouseClicked(MouseEvent e) {
  }
  
  @Override
  public void mousePressed(MouseEvent e) {
    ucgc.clicked();
  }
  
  @Override
  public void mouseReleased(MouseEvent e) {
  }
  
  @Override
  public void mouseEntered(MouseEvent e) {
  }
  
  @Override
  public void mouseExited(MouseEvent e) {
  }
  
}