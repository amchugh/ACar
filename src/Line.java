import java.io.Serializable;

public class Line implements Serializable {
  
  public Point p1, p2;
  public int yOffset, xMin, xMax, yMin, yMax;
  public double slope;
  
  public String name;
  
  public Line(int xMin, int xMax, int yOffset, double slope) {
    this.xMin = xMin;
    this.xMax = xMax;
    this.yOffset = yOffset;
    this.slope = slope;
  }
  
  public Line(Point p, double slope) {
    this.p1 = p;
    this.slope = slope;
    this.yOffset = (int) (p1.getY() - this.slope * p1.getX());
  }
  
  public Line(int x1, int x2, int y1, int y2) {
    this.p1 = new Point(x1, y1);
    this.p2 = new Point(x2, y2);
    generateLine();
  }
  
  public Line(Point p1, Point p2) {
    this.p1 = p1;
    this.p2 = p2;
    generateLine();
  }
  
  private void generateLine() {
    int x1 = p1.getX();
    int x2 = p2.getX();
    int y1 = p1.getY();
    int y2 = p2.getY();
    
    if (x1 > x2) {
      this.xMin = x2;
      this.xMax = x1;
      this.yMin = y2;
      this.yMax = y1;
    } else {
      this.xMin = x1;
      this.xMax = x2;
      this.yMin = y1;
      this.yMax = y2;
    }
    
    if (x1 == x2) {
      this.slope = Integer.MAX_VALUE;
    } else {
      this.slope = (double) (yMax - yMin) / (double) (xMax - xMin);
    }
    
    //this.yOffset = yMin;
    this.yOffset = (int) (yMin - this.slope * xMin);
  }
  
  public int getYAtX(int x) {
    return (int) Math.round(yOffset + (slope * x));
  }
  
  public int[] getYBounds() {
    if (yMin < yMax) {
      return new int[]{yMin, yMax};
    } else {
      return new int[]{yMax, yMin};
    }
  }
  
  /**
   * Determines if two lines intersect
   *
   * @param l The line to be tested against.
   * @return true if lines do intersect, otherwise false.
   */
  public boolean doesCollide(Line l) {
    Point p = getCollision(l);
    if (doesContain(p)) {
      return l.doesContain(p);
    }
    return false;
  }
  
  /**
   * Returns true if the point resides in the bounding box created by the line
   *
   * @param p A Point. Assumed to exist on the line.
   * @return whether Point 'p' is located in line's bounding box.
   */
  public boolean doesContain(Point p) {
    if (xMin <= p.getX() && p.getX() <= xMax) {
      int[] bounds = getYBounds();
      return bounds[0] <= p.getY() && p.getY() <= bounds[1];
    }
    return false;
  }
  
  /**
   * Determines where two lines would intersect
   *
   * @param l the line to test against
   * @return the Point where the two lines intersect. POINT IS NOT NECESSARILY ON EITHER LINE.
   * Point is where the two lines would intersect assuming lines are infinite length.
   */
  public Point getCollision(Line l) {
    /**
     * y = mx + b
     * y = px + l
     * mx + b = px + l
     *
     * mx - px = l - b
     * x(m-p) = l - b
     * x = (l-b)/(m-p)
     *
     * Where "l" is yOffset for #2, "b" is yOffset for #1, "m" is slope for #1, and "p" is slope for #2
     */
    double x = ((l.yOffset - yOffset) / (slope - l.slope));
    return new Point((int) Math.round(x), (int) Math.round(yOffset + slope * x));
  }
  
  public int[] traceBetweenPoints(int[] pixels, int width, int color, double fidelity) {
    return traceBetweenPoints(pixels, width, color, fidelity, false);
  }
  
  public int[] traceBetweenPoints(int[] pixels, int width, int color, double fidelity, boolean DEBUG) {
    if (DEBUG) {
      
      int height = pixels.length / width;
      
      int currY;
      
      for (int x = 0; x < width; x++) {
        currY = (int) Math.round(yOffset + slope * x);
        if (currY > 0 && currY < height) {
          pixels[x + currY * width] = color;
        }
      }
      
      return pixels;
      
    } else {
      int dx = p2.getX() - p1.getX();
      int dy = p2.getY() - p1.getY();
      double angle = Math.atan2(dx, dy);
      double xChange = Math.sin(angle);
      double yChange = Math.cos(angle);
      for (float i = 0; i < getDistance(p1, p2); i += fidelity) {
        int index = (p1.getX() + (int) (xChange * i)) + (p1.getY() + (int) (yChange * i)) * width;
        pixels[index] = color;
      }
      return pixels;
    }
  }
  
  private double getDistance(Point p1, Point p2) {
    return Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2));
  }
  
  public double getDistanceToPoint(Point p) {
    Line other = new Line(p, -1 / this.slope);
    Point collision = getCollision(other);
    if (doesContain(collision)) {
      return getDistance(collision, p);
    } else {
      double t = getDistance(this.p1, p);
      double u = getDistance(this.p2, p);
      if (t < u) {
        return t;
      } else {
        return u;
      }
    }
  }
  
}