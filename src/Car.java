import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Car {
  
  private final double rotationSpeed = 360 / 3;
  private float accelerationSpeed = 100f;
  private final float decelerationSpeed = 1f; // seconds to decelerate at max speed
  private float maxSpeed = 150f;
  
  private double rotation;
  private int updates_per_second;
  private float xPos, yPos;
  
  private float acceleration;
  private float speed;
  
  private BufferedImage image;
  
  public static final Dimension realCarSize = new Dimension(20, 10);
  private final Dimension imageCarSize = new Dimension(20, 20); // This is the size of the image for the car
  
  private String carImageName = "realcar.png";
  
  private enum Corners {UPPER_RIGHT, UPPER_LEFT, BOTTOM_RIGHT, BOTTOM_LEFT}
  
  /*
  public Car() {
    new Car(0f,0f);
  }
  */
  
  public Car(float _xPos, float _yPos) {
    xPos = _xPos;
    yPos = _yPos;
    
    setupImage();
  }
  
  public Car(float _xPos, float _yPos, double rot) {
    this.xPos = _xPos;
    this.yPos = _yPos;
    rotation = rot * 180 / Math.PI;
    
    setupImage();
  }
  
  public void setImage(String imageName) {
    carImageName = imageName;
    setupImage();
  }
  
  public void setMaxSpeed(float newMax) {
    maxSpeed = newMax;
  }
  
  public void setAccelerationSpeed(float newAcc) {
    accelerationSpeed = newAcc;
  }
  
  public void setupImage() {
    image = null;
    try {
      image = ImageIO.read(new File(carImageName));
    } catch (IOException e) {
      System.out.println("Failed to load image");
    }

    /*
    image = new BufferedImage(imageCarSize.width, imageCarSize.height, BufferedImage.TYPE_INT_RGB);
    int[] colorArray = new int[imageCarSize.width * imageCarSize.height];
    Arrays.fill(colorArray, 0x43db62);

    image.setRGB(0,0,imageCarSize.width,imageCarSize.height, colorArray ,0,0);
    */
  }
  
  public void setUpdatesPerSecond(int _updates_per_second) {
    updates_per_second = _updates_per_second;
  }
  
  public void setAccelerationValue(float acc) {
    acceleration = acc;
  }
  
  public void rotate(float r) {
    if (speed != 0) { // Cant turn while you're not moving!
      rotation = rotation + (r / updates_per_second * rotationSpeed);
    }
    //rotation = rotation + (r / updates_per_second * rotationSpeed * speed / maxSpeed);
  }
  
  public void setRadianRotation(double r) {
    rotation = r / Math.PI * 180d;
  }
  
  public void setPosition(int posx, int posy) {
    xPos = posx;
    yPos = posy;
  }
  
  /**
   * Takes a number and moves it a second number's worth of value closer to zero, stopping at zero
   *
   * @param n1 the value to move towards zero
   * @param n2 the amount to move by
   * @return the float n1 that is n2 value closer to zero
   */
  private float subtractToZero(float n1, float n2) {
    if (Math.abs(n1) <= n2) {
      return 0f;
    } else {
      return n1 - (n2 * Math.signum(n1));
    }
  }
  
  public double getRadianRotation() {
    return rotation * Math.PI / 180;
  }
  
  public void update() {
    // Calculate speed
    if (acceleration == 0) {
      speed = subtractToZero(speed, maxSpeed / decelerationSpeed / updates_per_second);
    } else {
      speed = speed + (acceleration * accelerationSpeed / updates_per_second);
      if (Math.abs(speed) > maxSpeed) {
        speed = maxSpeed * Math.signum(speed);
      }
    }
    
    // Apply speed
    float f = (speed / updates_per_second);
    double mod_rot = getRadianRotation();
    xPos += Math.cos(mod_rot) * f;
    yPos += Math.sin(mod_rot) * f;
  }
  
  public BufferedImage getImage() {
    return image;
  }
  
  public int[] getCenterPosition() {
    return new int[]{(int) xPos - imageCarSize.width / 2, (int) yPos - imageCarSize.height / 2};
  }
  
  public void draw(Graphics g) {
    // Rotation information
    try {
      double rotationRequired = getRadianRotation();
      double locationX = imageCarSize.width / 2;
      double locationY = imageCarSize.height / 2;
      AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
      AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
    
      int[] pos = getCenterPosition();
      g.drawImage(op.filter(getImage(), null), pos[0], pos[1], null);
      // Draw a dot in the center of the car image
      g.setColor(Color.black);
      g.fillRect((int) xPos - 1, (int) yPos - 1, 3, 3);
    } catch (Exception e) {
    }
  }
  
  /**
   * Gets the lines that emcompass the car
   *
   * @returns in order, the line representing the top, the bottom, the right side, and finally the left side.
   */
  public Line[] getBoundingBox() {
    Line[] box = new Line[4];
    
    box[0] = new Line(getCorner(Corners.UPPER_LEFT), getCorner(Corners.UPPER_RIGHT));
    box[1] = new Line(getCorner(Corners.BOTTOM_LEFT), getCorner(Corners.BOTTOM_RIGHT));
    box[2] = new Line(getCorner(Corners.UPPER_RIGHT), getCorner(Corners.BOTTOM_RIGHT));
    box[3] = new Line(getCorner(Corners.UPPER_LEFT), getCorner(Corners.BOTTOM_LEFT));
    
    box[0].name = "up";
    box[1].name = "d";
    box[2].name = "r";
    box[3].name = "l";
    
    return box;
  }
  
  public void drawBoundingBox(Graphics g) {
    g.setColor(Color.black);
    for (Line l : getBoundingBox()) {
      if (l.name == "d") {
        g.setColor(Color.red);
      }
      if (l.name == "r") {
        g.setColor(Color.green);
      }
      if (l.name == "l") {
        g.setColor(Color.black);
      }
      if (l.name == "up") {
        g.setColor(Color.blue);
      }
      g.drawLine(l.p1.getX(), l.p1.getY(), l.p2.getX(), l.p2.getY());
    }
  }
  
  private Point getCorner(Corners c) {
    Point p = new Point((int) xPos, (int) yPos);
    int w = realCarSize.width / 2;
    int h = realCarSize.height / 2;
    double x, y;
    /*
    double x = w * (1-Math.cos(getRadianRotation()));
    double y = h * (1-Math.sin(getRadianRotation()));
    */
    double angle = getRadianRotation() - Math.PI / 2;
    int wpartDirection = 1;
    int hpartDirection = 1;
    switch (c) {
      case UPPER_RIGHT:
        break;
      case UPPER_LEFT:
        //x = -x;
        wpartDirection = -1;
        break;
      case BOTTOM_RIGHT:
        hpartDirection = -1;
        //y = -y;
        break;
      case BOTTOM_LEFT:
        hpartDirection = -1;
        wpartDirection = -1;
        //x = -x;
        //y = -y;
        break;
    }
    x = Math.cos(angle) * h * hpartDirection + Math.cos(angle + Math.PI / 2) * w * wpartDirection;
    y = Math.sin(angle) * h * hpartDirection + Math.sin(angle + Math.PI / 2) * w * wpartDirection;
    p = p.move((int) x, (int) y);
    return p;
  }
  
}
