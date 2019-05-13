package AAI;

import java.io.*;

public class AGenerationIO {
  
  public static final String filetype = "gen";
  
  public static boolean save(AGeneration t, String filename) {
    filename = addFileEnding(filename);
    
    try {
      FileOutputStream file = new FileOutputStream(filename);
      ObjectOutputStream out = new ObjectOutputStream(file);
      
      out.writeObject(t);
      
      out.close();
      file.close();
      
      System.out.println("Saved Generation to \"" + filename + "\"");
      return true;
      
    } catch (IOException e) {
      System.out.println("Failed to save Generation to \"" + filename + "\" [IOException]");
      e.printStackTrace();
      return false;
    }
  }
  
  public static AGeneration load(String filename) {
    filename = addFileEnding(filename);
    AGeneration gen = null;
    
    try {
      FileInputStream file = new FileInputStream(filename);
      ObjectInputStream in = new ObjectInputStream(file);
      
      // Method for deserialization of object
      gen = (AGeneration) in.readObject();
      
      in.close();
      file.close();
      
      System.out.println("Successfully loaded Generation from \"" + filename + "\"");
      
    } catch (IOException e) {
      System.out.println("Failed to load Generation from \"" + filename + "\" [IOException]");
    } catch (ClassNotFoundException e) {
      System.out.println("Failed to load Generation from \"" + filename + "\" [ClassNotFoundException]");
    }
    
    return gen;
  }
  
  
  private static String addFileEnding(String name) {
    if (name.matches(".+[.]" + filetype + "(?!.)")) {
      return name;
    }
    return name + "." + filetype;
  }
  
}
