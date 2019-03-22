
public class Line {
    private int direction; // 0: vertical 1: horizontal
    private float linePosition;
    
    
    public int getDirection() {
        return direction;
    }
    public void setDirection(int direction) {
        this.direction = direction;
    }
    public float getLinePosition() {
        return linePosition;
    }
    public void setLinePosition(float linePosition) {
        this.linePosition = linePosition;
    }
    
    public Line(int direction, float linePosition) {
        super();
        this.direction = direction;
        this.linePosition = linePosition;
    }
    public Line() {
        super();
    }
    
    
}
