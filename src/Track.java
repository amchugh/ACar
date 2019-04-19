import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Track implements java.io.Serializable {
  
  private static final long serialversionUID = 129348938L;
  
  public List<Line> checkpoints;
  public Loop inner;
  public Loop outer;
  
  public Track() {
    outer = new Loop();
    inner = new Loop();
    checkpoints = new ArrayList<>();
  }
  
}

class TrackLoader {
  
  public static void Save(Track t, String filename) {
    filename = addTrackType(filename);
    try {
      FileOutputStream file = new FileOutputStream(filename);
      ObjectOutputStream out = new ObjectOutputStream(file);
      
      out.writeObject(t);
      
      out.close();
      file.close();
      
      System.out.println("Saved Track to \"" + filename + "\"");
      
    } catch (IOException e) {
      System.out.println("Failed to save Track to \"" + filename + "\" [IOException]");
      e.printStackTrace();
    }
  }
  
  public static Track Load(String filename) {
    filename = addTrackType(filename);
    Track t = null;
    
    try {
      FileInputStream file = new FileInputStream(filename);
      ObjectInputStream in = new ObjectInputStream(file);
      
      // Method for deserialization of object
      t = (Track) in.readObject();
      
      in.close();
      file.close();
      
      System.out.println("Successfully loaded Track from \"" + filename + "\"");
      
    } catch (IOException e) {
      System.out.println("Failed to load Track from \"" + filename + "\" [IOException]");
    } catch (ClassNotFoundException e) {
      System.out.println("Failed to load Track from \"" + filename + "\" [ClassNotFoundException]");
    }
    
    return t;
  }
  
  private static String addTrackType(String name) {
    if (name.matches(".+trk(?!.)")) {
      return name;
    }
    return name + ".trk";
  }
  
}