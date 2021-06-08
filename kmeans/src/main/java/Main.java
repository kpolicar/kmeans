import javax.swing.*;
import java.awt.*;
import java.util.*;

public class Main {

    static Color[] clusterColors;
    static HashMap<PopulationDataPoint, Waypoint> waypoints;
    static HashMap<Integer, Waypoint> centroidWaypoints;
    private static Random rand;

    public static void main(String[] args) {
        var form = new Form(LoadDataPointsFromFileSystem.dataPoints(), false);
        form.setVisible(true);
        form.setSize(800, 600);
        form.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }
}
