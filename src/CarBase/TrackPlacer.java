package CarBase;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrackPlacer extends KeyAdapter implements java.awt.event.MouseListener, KeyListener {
  
  private Loop innerLoop;
  private Loop outerLoop;
  
  private List<Line> checkpoints;
  private Point firstCheckpointPoint;
  
  private CarSpawnPoint carSpawnPoint;
  private Point carCenter;
  private Car c;
  
  public enum PlacingPhase {INNER, OUTER, CHECKPOINT, COMPLETE, CARSPAWN}
  
  private PlacingPhase currentPhase;
  
  private int backgroundColor = 0xfff0ff;
  private int innerColor = 0xff0000;
  private int outerColor = 0x00ff00;
  private int checkpointColor = 0x0000ff;
  private int carColor = 0xff00ff;
  
  private BufferedImage image;
  private int[] pixels;
  
  private double fidelity = 1.0;
  
  private final int pointSize = 2;
  
  private int width;
  private int height;
  
  public TrackPlacer(int screenWidth, int screenHeight) {
    height = screenHeight;
    width = screenWidth;
    currentPhase = PlacingPhase.OUTER;
    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    checkpoints = new ArrayList<>();
    firstCheckpointPoint = null;
    carCenter = null;
    carSpawnPoint = null;
    innerLoop = new Loop();
    outerLoop = new Loop();
  }
  
  public void onPointCreated(MouseEvent e) {
    Point p = new Point(e.getX(), e.getY());
    switch (currentPhase) {
      case OUTER:
        outerLoop.points.add(p);
        break;
      case INNER:
        innerLoop.points.add(p);
        break;
      case CHECKPOINT:
        if (firstCheckpointPoint == null) {
          firstCheckpointPoint = p;
        } else {
          checkpoints.add(new Line(firstCheckpointPoint, p));
          firstCheckpointPoint = null;
        }
        break;
      case COMPLETE:
        System.out.println(".... shouldn't be here uh-oh");
        break;
      case CARSPAWN:
        if (carCenter == null) {
          carCenter = p;
        } else {
          // Now we need to get the rotation between the two points.
          carSpawnPoint = new CarSpawnPoint(carCenter, -carCenter.getRotation(p) - Math.PI / 2);
          c = new Car(carSpawnPoint.center.getX(), carSpawnPoint.center.getY(), carSpawnPoint.rotation);
        }
        break;
    }
  }
  
  public void nextPhase() {
    switch (currentPhase) {
      case OUTER:
        currentPhase = PlacingPhase.INNER;
        break;
      case INNER:
        currentPhase = PlacingPhase.CHECKPOINT;
        break;
      case CHECKPOINT:
        currentPhase = PlacingPhase.CARSPAWN;
        break;
      case COMPLETE:
        System.out.println("Already done placing. Waiting on main loop. IDK how we got here.");
        break;
      
      case CARSPAWN:
        currentPhase = PlacingPhase.COMPLETE;
    }
  }
  
  public boolean isTrackPlacementComplete() {
    return getPhase() == TrackPlacer.PlacingPhase.COMPLETE;
  }
  
  private PlacingPhase getPhase() {
    return currentPhase;
  }
  
  public TrackController generateTrack() {
    TrackController t = new TrackController(width, height);
    //t.setInnerRing(innerPoints);
    //t.setOuterRing(outerPoints);
    t.setInnerLoop(innerLoop);
    t.setOuterLoop(outerLoop);
    t.setCheckpoints(checkpoints);
    t.setCarSpawnPoint(carSpawnPoint);
    t.generateFull();
    return t;
  }
  
  public void draw(Graphics g) {
    g.drawImage(image, 0, 0, null);
    if (carSpawnPoint != null) {
      g.drawString(Double.toString(carSpawnPoint.rotation), 100, 100);
    }
  }
  
  public void render() {
    // TODO make pretty
    // Fill background?
    Arrays.fill(pixels, 0xfff0ff);
    // Highlight points
    //highlightPoints(innerPoints, innerColor);
    //highlightPoints(outerPoints, outerColor);
    // Trace lines
    //traceLines(innerPoints, innerColor);
    //traceLines(outerPoints, outerColor);
    
    // Highlight Points
    image = innerLoop.highlightPoints(image, pointSize, innerColor);
    image = outerLoop.highlightPoints(image, pointSize, outerColor);
    // Trace lines
    image = innerLoop.traceLinesOnImage(image, innerColor);
    image = outerLoop.traceLinesOnImage(image, outerColor);
    // Checkpoint time!
    drawCheckpoints();
    // Draw the car spawn point
    drawCarCheckpoint();
  }
  
  private void drawCheckpoints() {
    for (Line l : checkpoints) {
      l.traceBetweenPoints(pixels, width, checkpointColor, fidelity);
    }
    // We're also gonna draw the point if it exists.
    if (firstCheckpointPoint != null) {
      int x = firstCheckpointPoint.getX();
      int y = firstCheckpointPoint.getY();
      int hs = pointSize / 2;
      try {
        for (int rx = -hs; rx <= hs; rx++) {
          for (int ry = -hs; ry <= hs; ry++) {
            pixels[(x + rx) + (y + ry) * image.getWidth()] = checkpointColor;
          }
        }
      } catch (IndexOutOfBoundsException e) {
        e.printStackTrace();
      }
    }
  }
  
  private void drawCarCheckpoint() {
    /*
    // if we have a point...
    if (carCenter != null) {
      // And if we have a rotation...
      if (carSpawnPoint != null) {
        // Then we are going to draw a box pointed in the right direction.
        // T@DO ^^
        // But first we're going to start with the forward line
      } else {
        // if we don't have a rotation, we'll just draw a box.
        
      }
    }
    */
    // Actually... let's cheat
    if (carSpawnPoint != null) {
      Line[] lines = c.getBoundingBox();
      for (Line l : lines) {
        pixels = l.traceBetweenPoints(pixels, image.getWidth(), carColor, fidelity);
      }
      Line n = new Line(carSpawnPoint.center,
        carSpawnPoint.center.move(
          (int) (Math.cos(carSpawnPoint.rotation) * Car.realCarSize.getWidth() / 2),
          (int) (Math.sin(carSpawnPoint.rotation) * Car.realCarSize.getWidth() / 2)
        )
      );
      pixels = n.traceBetweenPoints(pixels, image.getWidth(), carColor, fidelity);
    } else if (carCenter != null) {
    
    }
    
    /*else if (carCenter != null) {
      Car c = new Car(carCenter.getX(), carCenter.getY());
      Line[] lines = c.getBoundingBox();
      for (Line l : lines) {
        pixels = l.traceBetweenPoints(pixels, image.getWidth(), carColor, fidelity);
      }
    }*/
  }
  
  public void keyPressed(KeyEvent e) {
    int key = e.getKeyCode();
    if (key == 10) {
      nextPhase();
    }
  }
  
  @Override
  public void mouseClicked(MouseEvent e) {
  }
  
  @Override
  public void mousePressed(MouseEvent e) {
    onPointCreated(e);
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
