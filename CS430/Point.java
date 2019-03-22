
public class Point implements Comparable<Point> {
    int x;
    int y;
    int edges;
    int index;
    Point[] connections;
    
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
    
    public Point(int x, int y) {
        super();
        this.x = x;
        this.y = y;
    }
    
    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
    @Override
    public int compareTo(Point o) {
        // TODO Auto-generated method stub
        return this.y - o.y;
    }
    public Point() {
        super();
    }
    
    
}
