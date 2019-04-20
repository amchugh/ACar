import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;

public class UserCar extends KeyAdapter implements java.awt.event.MouseListener, KeyListener {
  
  private Car car;
  private int updates_per_second;
  
  // Forward, backward, left, right
  private boolean[] isMoving;
  
  private Keybinds keys;
  
  public UserCar(int _updates_per_second) {
    this.car = new Car(0, 0);
    setUpdatesPerSecond(_updates_per_second);
    isMoving = new boolean[]{false, false, false, false};
    keys = new Keybinds('w', 's', 'a', 'd');
  }
  
  public UserCar(int posX, int posY, int _updates_per_second) {
    this.car = new Car(posX, posY);
    setUpdatesPerSecond(_updates_per_second);
    isMoving = new boolean[]{false, false, false, false};
    keys = new Keybinds('w', 's', 'a', 'd');
  }
  
  public void setCar(Car c) {
    this.car = c;
  }
  
  public void makePlayerTwo() {
    keys = new Keybinds('i', 'k', 'j', 'l');
    car.setImage("realcartwo.png");
    car.setMaxSpeed(300);
    car.setAccelerationSpeed(40f);
  }
  
  public void update() {
    float f = 0;
    float turn = 0;
    if (isMoving[0]) {
      f += 1;
    }
    if (isMoving[1]) {
      f -= 1;
    }
    if (isMoving[2]) {
      turn -= 1;
    }
    if (isMoving[3]) {
      turn += 1;
    }
    car.setAccelerationValue(f);
    car.rotate(turn);
    car.update();
  }
  
  @Override
  public void keyPressed(KeyEvent e) {
    char key = e.getKeyChar();
    if (key == keys.forwardKey) {
      isMoving[0] = true;
    }
    if (key == keys.backwardKey) {
      isMoving[1] = true;
    }
    if (key == keys.leftKey) {
      isMoving[2] = true;
    }
    if (key == keys.rightKey) {
      isMoving[3] = true;
    }
  }
  
  @Override
  public void keyReleased(KeyEvent e) {
    char key = e.getKeyChar();
    if (key == 'e') {
      System.out.println("goodbye mis amigos");
    }
    if (key == keys.forwardKey) {
      isMoving[0] = false;
    }
    if (key == keys.backwardKey) {
      isMoving[1] = false;
    }
    if (key == keys.leftKey) {
      isMoving[2] = false;
    }
    if (key == keys.rightKey) {
      isMoving[3] = false;
    }
  }
  
  @Override
  public void mouseClicked(MouseEvent e) {
  
  }
  
  @Override
  public void mousePressed(MouseEvent e) {
  
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
  
  public void setUpdatesPerSecond(int _updates_per_second) {
    updates_per_second = _updates_per_second;
    car.setUpdatesPerSecond(updates_per_second);
  }
  
  public void draw(Graphics g) {
    car.draw(g);
    //car.drawBoundingBox(g);
  }
  
  public Car getCar() {
    return car;
  }
  
}

class Keybinds {
  
  public char forwardKey, backwardKey, leftKey, rightKey;
  
  public Keybinds(char f, char b, char l, char r) {
    forwardKey = f;
    backwardKey = b;
    leftKey = l;
    rightKey = r;
  }
  
}