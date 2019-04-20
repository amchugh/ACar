import java.awt.*;

public class UserControlGameController extends GameController {
  
  private String trackname;
  
  public static void main(String[] args) {
    GameController game = new UserControlGameController();
    game.setup();
  }
  
  public UserControlGameController() {
    super();
  }
  
  @Override
  public void setup() {
    // First we need to get the track name.
  }
  
  @Override
  public void update() {
  
  }
  
  @Override
  public void draw(Graphics g) {
  
  }
  
}
