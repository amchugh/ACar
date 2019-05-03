package CarBase;

import javax.swing.*;
import java.awt.*;

public class Display extends JFrame {
  
  public Canvas canvas;
  
  public Display() {
    this.setTitle("");
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setResizable(false);
    
    canvas = new Canvas();
    
    canvas.setPreferredSize(Config.windowSize);
    canvas.setFocusable(false);
    
    this.add(canvas);
    this.pack();
    this.setLocation(50, 50); // Position on screen
  }
  
}
