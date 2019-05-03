package CarBase;

import java.io.Serializable;

public class CarSpawnPoint implements Serializable {
  
  public Point center;
  public double rotation;
  
  public CarSpawnPoint() {
  
  }
  
  public CarSpawnPoint(Point c, double r) {
    center = c;
    rotation = r;
  }
  
}
