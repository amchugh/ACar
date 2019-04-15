import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Loop implements Serializable {
  
  public List<Point> points;
  public double fidelity = 1.0;
  
  private int[] pixels;
  
  public Loop() {
    points = new ArrayList<Point>();
  }
  
  public Loop(List<Point> points) {
    this.points = points;
  }
  
  public Point getNearest(Point p) {
    double bestDist = Double.MAX_VALUE;
    Point b = null;
    for (Point p2 : points) {
      if (getDistance(p, p2) < bestDist) {
        b = p2;
      }
    }
    return b;
  }
  
  public List<Point> getTracePoints() {
    return getTracePoints(false);
  }
  
  public List<Point> getTracePoints(boolean debug) {
    List<Point> p = new ArrayList<Point>();
    if (points.size() > 2) {
      for (int i = 0; i < points.size() - 1; i++) {
        Point p1 = points.get(i);
        Point p2 = points.get(i + 1);
        p.addAll(getLinePoints(p1, p2, debug));
      }
      p.addAll(getLinePoints(points.get(points.size() - 1), points.get(0), debug));
    }
    return p;
  }
  
  private List<Point> getLinePoints(Point p1, Point p2) {
    return getLinePoints(p1, p2, false);
  }
  
  private List<Point> getLinePoints(Point p1, Point p2, boolean debug) {
    if (debug) {
      List<Point> p = new ArrayList<Point>();
      Line l = new Line(p1, p2);
      for (int x = 0; x < GameMain.windowSize.getWidth(); x++) {
        int y = l.getYAtX(x);
        if (y > 0 && y < GameMain.windowSize.getHeight()) {
          p.add(new Point(x, y));
        }
      }
      return p;
    }
    List<Point> p = new ArrayList<Point>();
    int dx = p2.getX() - p1.getX();
    int dy = p2.getY() - p1.getY();
    double angle = Math.atan2(dx, dy);
    double xChange = Math.sin(angle);
    double yChange = Math.cos(angle);
    for (float i = 0; i < getDistance(p1, p2); i += fidelity) {
      p.add(new Point((int) (p1.getX() + xChange * i), (int) (p1.getY() + yChange * i)));
    }
    return p;
  }
  
  private double getDistance(Point p1, Point p2) {
    return Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2));
  }
  
  public BufferedImage traceLinesOnImage(BufferedImage image, int color) {
    pixels = getImageData(image);
    
    for (Line l : getLines()) {
      trace(l.p1, l.p2, image.getWidth(), color);
    }
    
    return image;
  }
  
  public List<Line> getLines() {
    Point p1, p2;
    List<Line> r = new ArrayList<>();
    if (points.size() > 2) {
      for (int i = 0; i < points.size() - 1; i++) { // We're using minus 1 because we need to do the last manually
        p1 = points.get(i);
        p2 = points.get(i + 1);
        r.add(new Line(p1, p2));
      }
    }
    if (points.size() > 1) {
      p1 = points.get(points.size() - 1);
      p2 = points.get(0);
      r.add(new Line(p1, p2));
    }
    return r;
  }
  
  private void trace(Point p1, Point p2, int width, int color) {
    Line l = new Line(p1.getX(), p2.getX(), p1.getY(), p2.getY());
    pixels = l.traceBetweenPoints(pixels, width, color, fidelity);
  }
  
  public BufferedImage highlightPoints(BufferedImage image, int pointSize, int color) {
    pixels = getImageData(image);
    
    if (points.size() > 0) {
      for (int i = 0; i < points.size(); i++) {
        Point p = points.get(i);
        int x = p.getX();
        int y = p.getY();
        int hs = pointSize / 2;
        try {
          for (int rx = -hs; rx <= hs; rx++) {
            for (int ry = -hs; ry <= hs; ry++) {
              pixels[(x + rx) + (y + ry) * image.getWidth()] = color;
            }
          }
        } catch (IndexOutOfBoundsException e) {
          e.printStackTrace();
        }
      }
    }
    
    return image;
  }
  
  private int[] getImageData(BufferedImage image) {
    return ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
  }
  
  /**
   * Used for car detection.
   *
   * @return whether the line collides with the loop
   */
  public boolean doesCollide(Line l) {
    for (Line s : getLines()) {
      if (l.doesCollide(s)) {
        return true;
      }
    }
    return false;
  }
  
}
