import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Formatter;


public class separatePoints {
    
    
    static InputStream is = null;
    static InputStreamReader isr = null;
    static BufferedReader br = null;
    static FileOutputStream fos = null;
    static Writer writer = null;
    
    static final int MAX_POINTS = 100;
    static int n_points = 0;
    static int n_lines = 0;
    static int n_connections = 0;
    static int n_initial_lines = 0;
    
    /**Use arrays to store points and lines.
     */
    static Point[] points = new Point[MAX_POINTS];
    static Point[] sorted_y_point = new Point[MAX_POINTS];
    static Line[] lines = new Line[MAX_POINTS];
    static Line[] initialLines = new Line[2 * (MAX_POINTS - 1)];
    
    public static void main(String[] args) {
        
        for (int index = 1; index < MAX_POINTS; index++) {
            if(!readFile(index)) {
                System.out.println("\r\nThere are " + (index - 1) + " instances solved.");
                return;
            }
            Arrays.sort(sorted_y_point, 0, n_points);//Sort the y coordinates of the points in input files.
            
            initConnections();
            
            initLines();
            
            
            /**Determine which line cuts the most connections and then commit that line.
             * This this the most important part of this algorithm.
             * This is the Greedy choice!
             */
            for (int i = 0; i < n_initial_lines; i++) {
                int num_connection = numberBreak(initialLines[0]);
                int line_index = 0;
                for (int j = 1; j < n_initial_lines; j++) {
                    int temp = numberBreak(initialLines[j]);
                    if (temp > num_connection) {
                        line_index = j;
                        num_connection = temp;
                    }
                }
                
                commit(initialLines[line_index]);//commit the line breaks the most connections
                initialLines[line_index] = null;//remove that line from the resting line array
                if (n_connections == 0)
                    break;//Stop finding lines when all connections are cut.
            }
            
            output(index);
            
            //System.out.println("Instance" + index + " Solved.");
            reset();
        }
    }
    
    
    
    /**
     * Connect all points with connections.
     */
    public static void initConnections() {
        for (int i = 0; i < n_points; i++) {
            points[i].connections = new Point[MAX_POINTS];
            points[i].index = i;
            for (int j = 0; j < n_points; j++) {
                if (i != j) {
                    points[i].connections[j] = points[j];
                    //points[i].edges++;
                    n_connections++;
                }
            }
        }
    }
    
    /**
     * Remove connections between points.
     */
    private static void breakConnections(int index1, int index2) {
        if (points[index1].connections[index2] != null) {
            points[index1].connections[index2] = null;
            points[index2].connections[index1] = null;
            //	        (points[id1].edges)--;
            //	        (points[id2].edges)--;
            n_connections -= 2;
        }
    }
    
    
    /**
     * Initialize axis-parallel lines between each pair of points.
     * Every two near points are separated by a line at the middle range of each other.
     * Both vertically and horizontally.
     */
    public static void initLines() {
        for (int i = 0; i < n_points - 1; i++) {
            float linePosition = ((float)points[i].x + (float)points[i + 1].x) / 2;
            Line line = new Line(0, linePosition);
            initialLines[n_initial_lines] = line;
            n_initial_lines++;
        }
        for (int i = 0; i < n_points - 1; i++) {
            float linePosition = ((float)sorted_y_point[i].y + (float)sorted_y_point[i + 1].y) / 2;
            Line line = new Line(1, linePosition);
            initialLines[n_initial_lines] = line;
            n_initial_lines++;
        }
    }
    
    
    /**
     * return the index of the point that is closes to the specified intersect.
     * Takes at most O(n) time.
     * If we return -1, then there is no point closes to the line.
     */
    public static int point_nearest_inter(Line line) {
        float coord = 0;
        for (int i = 0; i < n_points; i++) {
            if (line.getDirection() == 0) {
                coord = (float)points[i].x;
            } else {
                coord = (float)sorted_y_point[i].y;
            }
            if (coord > line.getLinePosition()) {
                return i - 1;
            }
        }
        return -1;
    }
    
    /**
     * Commit the line and break connections.
     * Takes at most O(n^2) time.
     */
    public static void commit(Line line) {
        if (line == null) {
            return;
        }
        
        lines[n_lines] = line;
        int pointID = point_nearest_inter(line);
        
        Point[] pts;
        if (line.getDirection() == 0) {
            pts = points;
        } else {
            pts = sorted_y_point;
        }
        
        for (int i = 0; i <= pointID; i++) {
            for (int j = pointID + 1; j < n_points; j++) {
                breakConnections(pts[i].index, pts[j].index);
            }
        }
        n_lines++;
    }
    
    /**
     * Returns the number of connections that an axis-parallel line can break
     * Takes at most O(n^2)
     */
    public static int numberBreak(Line line) {
        if (line == null) {
            return 0;
        }
        int pointID = point_nearest_inter(line);
        
        Point[] pts;
        int numbreak = 0;
        
        if (line.getDirection() == 0) {
            pts = points;
        } else {
            pts = sorted_y_point;
        }
        
        for (int i = 0; i <= pointID; i++) {
            for (int j = pointID + 1; j < n_points; j++) {
                if ((points[pts[i].index]).connections[(pts[j].index)] != null) {
                    numbreak++;
                }
            }
        }
        return numbreak;
    }
    
    /**
     * Reset all conditions when an instance is solved.
     * And ready to solve the next instance.
     */
    public static void reset() {
        n_points = 0;
        n_lines = 0;
        n_connections = 0;
        n_initial_lines = 0;
        
        for (int i = 0; i < MAX_POINTS; i++) {
            points[i] = null;
            sorted_y_point[i] = null;
            lines[i] = null;
            initialLines[i] = null;
        }
    }
    
    /**
     * Reads input files from /input/ folder
     * @param id - the numerous part of the file name.
     * @return - true if the file is read successfully, otherwise false
     */
    public static boolean readFile(int id){
        Formatter fmt = new Formatter();
        fmt.format("%02d", id);
        is = separatePoints.class.getResourceAsStream("/input/instance" + fmt + ".txt");
        if (is == null) {
            fmt.close();
            return false;
        }
        isr = new InputStreamReader(is);
        br = new BufferedReader(isr);
        String[] strs = null;
        String str = "";
        
        try {
            n_points = Integer.parseInt(br.readLine());
            
            int i = 0;
            while((str = br.readLine()) != null){
                strs = str.split(" ");
                int x = Integer.parseInt(strs[0]);
                int y = Integer.parseInt(strs[1]);
                
                Point point = new Point(x, y);
                points[i] = point;
                sorted_y_point[i] = point;
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            release(br, isr, is);
            fmt.close();
        }
        return true;
    }
    
    /**
     * Yield output files to /greedy_output/ folder
     * @param id - the numerous part of the file name
     */
    public static void output(int id){
        Formatter fmt = new Formatter();
        fmt.format("%02d", id);
        try {
            fos = new FileOutputStream("./output_greedy/greedy_solution" + fmt + ".txt", false);
            writer = new OutputStreamWriter(fos);
            
            writer.write(n_lines + "\r\n");
            for (int i = 0; i < n_lines; i++) {
                if (lines[i].getDirection() == 0) {
                    writer.write("v ");
                } else {
                    writer.write("h ");
                }
                writer.write(lines[i].getLinePosition() + "\r\n");
            }
            
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fmt.close();
        }
    }
    
    /**
     * Releases the streams and readers
     * @param br - a BufferedReader object
     * @param isr- an InputStreamReader object
     * @param is - an InputStream object
     */
    private static void release(BufferedReader br, InputStreamReader isr, InputStream is){
        if (br != null) {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        if (isr != null) {
            try {
                isr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    
}
