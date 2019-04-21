import java.awt.*;

public class AICarController {
  
  private Car car;
  private int updates_per_second;
  
  public AICarController(int ups) {
    car = new Car(0, 0);
    setUpdatesPerSecond(ups);
  }
  
  public void update() {
    car.setAccelerationValue(1);
    car.rotate(0.1f);
    car.update();
  }
  
  public void draw(Graphics g) {
    car.draw(g);
  }
  
  public void setUpdatesPerSecond(int ups) {
    updates_per_second = ups;
    car.setUpdatesPerSecond(updates_per_second);
  }
  
  public Car getCar() {
    return car;
  }
  
}
