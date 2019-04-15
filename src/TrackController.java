import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.Serializable;
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
    double max = getMaxStrength();
    for (int i = 0; i < pixels.length; i++) {
      if (grid[i] == fillTile) {
        /*
        if (strength[i] > 6) {
          pixels[i] = 0x000000;
        } else {
          pixels[i] = 0xff00ff;
        }
        */
        pixels[i] = 0x010101 * (int) (strength[i] / max * 0xff);
      } else {
        pixels[i] = grid[i].color;
      }
    }
    if (showCheckpoints) {
      for (Line l : track.checkpoints) {
        l.traceBetweenPoints(pixels, size.width, checkpointDebugColor, fidelity);
      }
    }
  }
  
  private void perfromFloodFillFromLikelyPoint() {
    Point p1 = track.inner.points.get(0);
    Point p2 = track.outer.getNearest(p1);
    int centerX = (p1.getX() + p2.getX()) / 2;
    int centerY = (p1.getY() + p2.getY()) / 2;
    floodFill(centerX + centerY * size.width);
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
  
  private void floodFill(int pos) {
    if (pos < 0 || pos >= size.width * size.height) {
      return;
    }
    if (grid[pos] != emptyTile) {
      return;
    }
    grid[pos] = fillTile;
    // Now try the other neighbor tiles
    if ((pos + 1) % size.width != 0) {
      floodFill(pos + 1);
    }
    if (pos % size.width != 0) {
      try {
        floodFill(pos - 1);
      } catch (StackOverflowError e) {
        System.out.println("Help");
        e.printStackTrace();
        System.out.println(pos);
        System.exit(-1);
      }
    } else {
      System.out.println("caught!");
    }
    if (pos >= size.width) {
      floodFill(pos - size.width);
    }
    if (pos < (size.width - 1) * size.height) {
      floodFill(pos + size.width);
    }
  }
  
  private void fillSpaces() {
    
    
    /**
     * For every line...
     * determine all the start and stop points.
     * A start point is determined when you hit an edge.
     * After finding a start point, we go to end point detection.
     * We know we've hit an end point when we find the next, non-consecutive edge.
     */

        /*
        for (int y = 0; y < size.height; y++) {
            int o = 0;
            int i = 0;
            int offset = y*size.width;
            for (int x = 0; x < size.width; x++) {
                if (grid[offset + x] == bgTile) {
                    if (o%2 == 1 && i%2==0) {
                        grid[offset+x] = fillTile;
                    }
                } else {
                    if (grid[offset + x] == outerEdge && grid[offset +x + 1] != outerEdge) {
                        o++;
                    } else if(grid[offset + x] == innerEdge && grid[offset+x+1] != innerEdge) {
                        i++;
                    }
                }
            }
        }
        */

        /*
        for (int y = 0; y < size.height; y++) {
            int potentialS = -1;
            int offset = y*size.width;
            List<Integer> startPoints = new ArrayList<Integer>();
            List<Integer> endPoints = new ArrayList<Integer>();
            for (int x = 0; x < size.width; x++) {
                if (potentialS == -1) {
                    if (pixels[offset + x] == outerEdge.color) {
                        if (pixels[offset + x + 1] != outerEdge.color) {
                            potentialS = x + 1;
                        }
                    }
                } else {
                    if (pixels[offset + x] == outerEdge.color) {
                        endPoints.add(x);
                        startPoints.add(potentialS);
                        potentialS = -1;
                    }
                }
            }
            for (int i = 0; i < startPoints.size(); i++) {
                for (int x = startPoints.get(i); x < endPoints.get(i); x++) {
                    pixels[offset + x] = outerEdge.color;
                }
            }
        }

        /*
        boolean in = false;
        for (int i = 0; i < pixels.length; i++) {
            if (pixels[i] == roadEdgeColor) {
                if (in) {
                    if (pixels[i + 1] != roadEdgeColor) {
                        in = false;
                    }
                } else {
                    if (pixels[i - 1] != roadEdgeColor) {
                        in = true;
                    }
                }
            } else {
                if (in) {
                    pixels[i] = roadFillColor;
                }
            }
        }
        // This isn't working...
        */
    
    /**
     * Flood fill approach.
     */
    
    
    // To auto pick center points, maybe the average location between the first inner and outer point?

        /*
        int centerX = 26;
        int centerY = 26;
        */
    int centerX;
    int centerY;
    //centerX = (innerRing.get(0).getX() + outerRing.get(0).getX()) / 2;
    //centerY = (innerRing.get(0).getY() + outerRing.get(0).getY()) / 2;
    Point p1 = track.inner.points.get(0);
    Point p2 = track.outer.getNearest(p1);
    centerX = (p1.getX() + p2.getX()) / 2;
    centerY = (p1.getY() + p2.getY()) / 2;
    grid[centerX + centerY * size.width] = fillTile;
    
    System.out.println(java.time.Clock.systemUTC().instant());
    
    int maxDist = size.width;
    // We'll move out in blocks
    for (int i = 1; i < maxDist; i++) {
      // Get the testing area
      boolean didPlace = false;
      int lowCapX = centerX - i;
      int highCapX = centerX + i;
      int lowCapY = centerY - i;
      int highCapY = centerY + i;
      if (lowCapX < 0) {
        lowCapX = 0;
      }
      if (lowCapY < 0) {
        lowCapY = 0;
      }
      if (highCapX > size.width - 1) {
        highCapX = size.width - 1;
      }
      if (highCapY > size.height - 1) {
        highCapY = size.height - 1;
      }
      
      if (lowCapX == 0 && lowCapY == 0 && highCapX == size.width && highCapY == size.height) {
        // Reached the limits of the screen
        System.out.println("Done early.");
        System.out.println(java.time.Clock.systemUTC().instant());
        return;
      } else {
                /*

                // Time to iterate over all the new points.
                for (int x = lowCapX + 1; x <= highCapX - 1; x++) {
                    // We want to test every point where y = highCapY OR lowCapY. We'll do the same for y later.
                    testPoint(x, highCapY);
                    testPoint(x, lowCapY);
                }
                for (int y = lowCapY + 1; y <= highCapY - 1; y++) {
                    // We've removed 1 from the range because those are the corners. They will be tested with the X's.
                    testPoint(lowCapX, y);
                    testPoint(highCapX, y);
                }

                */
        
        for (int x = lowCapX; x <= highCapX; x++) {
          for (int y = lowCapY; y <= highCapY; y++) {
            if (x == centerX && y == centerY) {
              // We don't do anything to the center point past the first iteration
            } else {
              if (testPoint(x, y)) {
                didPlace = true;
              } else {
                //System.out.println("Failed");
              }
            }
          }
        }
        if (!didPlace) {
          System.out.println("Done early [ALL FAILED TO PLACE]");
          System.out.println(java.time.Clock.systemUTC().instant());
          return;
        }
      }
    }
    System.out.println(java.time.Clock.systemUTC().instant());
  }
  
  private boolean testPoint(int x, int y) {
    try {
      // The rule is, if the tile is empty and there is one adjacent filled tile, turn to filled.
      if (grid[x + y * size.width] == emptyTile) {
        if (hasAdjacent(x, y, fillTile)) {
          // Set to filled.
          grid[x + y * size.width] = fillTile;
          return true;
        }
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      e.printStackTrace();
      System.out.println(x);
      System.out.println(y);
      System.out.println((x + y * size.width));
      System.out.println(grid.length);
      System.exit(-1);
    }
    return false;
  }
  
  private boolean hasAdjacent(int x, int y, Tile test) {
    try {
            /*
            for (int rx = -1; rx <= 1; rx++) {
                for (int ry = -1; ry <= 1; ry++) {
                    if (!(rx == ry && rx == 0)) { // Make sure we aren't testing the center point.
                        int i = x+rx + (ry+y) * size.width;
                        if (grid[i] == test) {
                            return true;
                        }
                    }
                }
            }
            */
      int t;
      t = x - 1 + y * size.width;
      if (grid[t] == test) {
        return true;
      }
      t = x + 1 + y * size.width;
      if (grid[t] == test) {
        return true;
      }
      t = x + (y - 1) * size.width;
      if (grid[t] == test) {
        return true;
      }
      t = x + (y + 1) * size.width;
      if (grid[t] == test) {
        return true;
      }
    } catch (Exception e) {
      //e.printStackTrace();
    }
    return false;
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

class Point implements Serializable {
  
  private int xPos, yPos;
  
  public Point(int _xPos, int _yPos) {
    xPos = _xPos;
    yPos = _yPos;
  }
  
  public Point move(int x, int y) {
    return new Point(xPos + x, yPos + y);
  }
  
  public int[] getLocation() {
    return new int[]{xPos, yPos};
  }
  
  public int getX() {
    return xPos;
  }
  
  public int getY() {
    return yPos;
  }
  
}