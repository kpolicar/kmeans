import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.cache.FileBasedLocalCache;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.WaypointPainter;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    static Color[] clusterColors;
    static HashMap<PopulationDataPoint, MyWaypoint> waypoints;
    static HashMap<Integer, MyWaypoint> centroidWaypoints;
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

    static final class ClusteredGeoPosition implements Map.Entry<Integer, GeoPosition> {
        private final Integer key;
        private GeoPosition value;
        public boolean isCluster;

        public ClusteredGeoPosition(int key, GeoPosition value, boolean isCluster) {
            this.key = key;
            this.value = value;
            this.isCluster = isCluster;
        }

        @Override
        public Integer getKey() {
            return key;
        }

        @Override
        public GeoPosition getValue() {
            return value;
        }

        @Override
        public GeoPosition setValue(GeoPosition value) {
            return this.value = value;
        }
    }
}
