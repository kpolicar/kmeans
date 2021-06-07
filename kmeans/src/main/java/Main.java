import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.CharBuffer;
import java.util.*;
import mpi.*;

public class Main {

    static Color[] clusterColors;
    static HashMap<PopulationDataPoint, Waypoint> waypoints;
    static HashMap<Integer, Waypoint> centroidWaypoints;
    private static Random rand;

    public static void main(String[] args) {
        var form = new Form(dataPoints());
        form.setVisible(true);
        form.setSize(800, 600);
        form.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private static PopulationDataPoint[] dataPoints() {
        InputStream is = Main.class.getClassLoader().getResourceAsStream("data.json");
        Reader fr = new InputStreamReader(is);

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(fr, PopulationDataPoint[].class);
    }
}
