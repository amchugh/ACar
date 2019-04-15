import java.io.Serializable;

public class Point implements Serializable {

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