package CarBase;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.List;
import java.util.Stack;

public class TrackController {
  
  private Track track;
  
  private BufferedImage image;
  
  private Dimension size;
  private int[] pixels;
  
  private double fidelity = 1.0;
  
  private static final boolean showDebug = false;
  private static final boolean drawFill = true;
  private static final boolean showCheckpoints = true;
  private static final int checkpointDebugColor = 0xff0000;
  
  private class Tile {
    
    public int color;
    
    public Tile(int c) {
      color = c;
    }
    
  }
  
  private Tile[] grid;
  private double[] strength;
  private Tile emptyTile = new Tile(0x0fff0f);
  private Tile fillTile = new Tile(0x666666);
  private Tile innerEdge = new Tile(0x222222);
  private Tile outerEdge = new Tile(0x223322);
  
  private int currentCheckpoint = 0;
  
  public TrackController(int width, int height) {
    size = new Dimension(width, height);
    track = new Track();
    setup();
  }
  
  public TrackController(int width, int height, Track t) {
    size = new Dimension(width, height);
    track = t;
    setup();
  }
  
  private void setup() {
        /*
        outerRing = new ArrayList<Point>() { };
        outerRing.add(new Point(25,25));
        outerRing.add(new Point(125,25));
        outerRing.add(new Point(200,150));
        outerRing.add(new Point(25,250));
        innerRing = new ArrayList<Point>() { };
        innerRing.add(new Point(40,40));
        innerRing.add(new Point(100,40));
        innerRing.add(new Point(100,150));
        innerRing.add(new Point(40,100));
        */
    
    image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
    pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    for (int i = 0; i < pixels.length; i++) {
      pixels[i] = 0xffffff;
    }
    
    grid = new Tile[pixels.length];
    strength = new double[pixels.length];
    for (int i = 0; i < grid.length; i++) {
      grid[i] = emptyTile;
    }

        /*
        traceLine(outerRing, outerEdge);
        traceLine(innerRing, innerEdge);

        fillSpaces();

        generateImage();
        */
    
  }
  
  public void setOuterRing(List<Point> p) {
    track.outer.points = p;
  }
  
  public void setInnerRing(List<Point> p) {
    track.inner.points = p;
  }
  
  public void setOuterLoop(Loop l) {
    track.outer = l;
  }
  
  public void setInnerLoop(Loop l) {
    track.inner = l;
  }
  
  public void setCarSpawnPoint(CarSpawnPoint csp) {
    track.carSpawnPoint = csp;
  }
  
  public void setCheckpoints(List<Line> p) {
    track.checkpoints = p;
    currentCheckpoint = 0;
  }
  
  public int testCheckpoints(Car c) {
    for (Line l : c.getBoundingBox()) {
      for (int i = 0; i < track.checkpoints.size(); i++) {
        if (track.checkpoints.get(i).doesCollide(l)) {
          return i;
        }
      }
    }
    return -1;
  }
  
  // Useless.
  // TODO remove.
  public void hitCheckpoint(int number) {
    if (currentCheckpoint + 1 == number) {
      currentCheckpoint++;
    } else {
      System.out.println("Skipped a checkpoint.");
    }
  }
  
  // render
  public void generateFull() {
    addLoopToImage(track.inner, innerEdge);
    addLoopToImage(track.outer, outerEdge);
    //fillSpaces();
    //perfromFloodFillFromLikelyPoint();
    if (drawFill) {
      nonRecursiveFill();
    }
    calculateFillStrength();
    generateImage();
  }
  
  private void addLoopToImage(Loop l, Tile t) {
    List<Point> ps = l.getTracePoints();
    for (Point p : ps) {
      grid[p.getX() + p.getY() * size.width] = t;
      //System.out.println(p.getX() + " " + p.getY());
    }
    if (showDebug) {
      // DEBUG ONLY
      // TODO REMOVE
      ps = l.getTracePoints(true);
      for (Point p : ps) {
        grid[p.getX() + p.getY() * size.width] = t;
        //System.out.println(p.getX() + " " + p.getY());
      }
    }
  }
  
  /**
   * This function will iterate over every point of the inner portion of the track,
   */
  private void calculateFillStrength() {
    for (int i = 0; i < grid.length; i++) {
      if (grid[i] == fillTile) {
        //strength[i] =
        Point p = new Point(i % size.width, i / size.width);
        double lowest = Double.MAX_VALUE;
        double test;
        for (Line innerLine : track.inner.getLines()) {
          test = innerLine.getDistanceToPoint(p);
          if (test < lowest) {
            lowest = test;
          }
        }
        for (Line outerLine : track.outer.getLines()) {
          test = outerLine.getDistanceToPoint(p);
          if (test < lowest) {
            lowest = test;
          }
        }
        strength[i] = lowest;
      }
    }
  }
  
  private double getMaxStrength() {
    double h = 0;
    for (double t : strength) {
      if (t > h) {
        h = t;
      }
    }
    return h;
  }
  
  // render
  private void generateImage() {
//    double max = getMaxStrength();
//    for (int i = 0; i < pixels.length; i++) {
//      if (grid[i] == fillTile) {
//        /*
//        if (strength[i] > 6) {
//          pixels[i] = 0x000000;
//        } else {
//          pixels[i] = 0xff00ff;
//        }
//        */
//        pixels[i] = 0x010101 * (int) (strength[i] / max * 0xff);
//      } else {
//        pixels[i] = grid[i].color;
//      }
//    }
    for (int i = 0; i < pixels.length; i++) {
      pixels[i] = grid[i].color;
    }
    if (showCheckpoints) {
      for (Line l : track.checkpoints) {
        l.traceBetweenPoints(pixels, size.width, checkpointDebugColor, fidelity);
      }
    }
  }
  
  private void nonRecursiveFill() {
    Stack<Point> sp = new Stack<>();
    Point p1 = track.inner.points.get(0);
    Point p2 = track.outer.getNearest(p1);
    int centerX = (p1.getX() + p2.getX()) / 2;
    int centerY = (p1.getY() + p2.getY()) / 2;
    sp.add(new Point(centerX, centerY));
    while (sp.size() > 0) {
      // Get the point
      Point p = sp.pop();
      // Check this point
      if (gridAt(p) == emptyTile) {
        // Set point
        grid[getGridPos(p)] = fillTile;
        
        // add north to queue
        if (p.getY() > 0) {
          sp.add(p.move(0, -1));
        }
        // add south to queue
        if (p.getY() < size.height - 1) {
          sp.add(p.move(0, 1));
        }
        // add east to queue
        if (p.getX() < size.width - 1) {
          sp.add(p.move(1, 0));
        }
        // add west to queue
        if (p.getX() > 0) {
          sp.add(p.move(-1, 0));
        }
      }
    }
  }
  
  private int getGridPos(Point p) {
    return size.width * p.getY() + p.getX();
  }
  
  private Tile gridAt(Point p) {
    return grid[size.width * p.getY() + p.getX()];
  }
  
  public void draw(Graphics g) {
    g.drawImage(image, 0, 0, null);
  }
  
  // Returns true if the given points do collide with the track.
  public boolean testCollision(Car c) {
    Line[] toTest = c.getBoundingBox();
    for (Line l : toTest) {
      // For every one of the four lines, we need to test if it intersects on the loop.
      if (track.inner.doesCollide(l)) {
        return true;
      }
      if (track.outer.doesCollide(l)) {
        return true;
      }
    }
    return false;
  }
  
  public Track getTrack() {
    return this.track;
  }
  
}
