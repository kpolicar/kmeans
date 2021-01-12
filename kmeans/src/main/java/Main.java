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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    static Color[] clusterColors;
    static HashMap<PopulationDataPoint, MyWaypoint> waypoints;
    private static Random rand;

    public static void main(String[] args) {
        rand = new Random();
        InitMap();
        ClusterPoints();
    }

    private static PopulationDataPoint[] dataPoints() {
        InputStream is = Main.class.getClassLoader().getResourceAsStream("data.json");
        Reader fr = new InputStreamReader(is);

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(fr, PopulationDataPoint[].class);
    }

    public static void ClusterPoints()
    {
        var points = waypoints.keySet().stream()
                .map(dataPoint -> new double[]{ dataPoint.la, dataPoint.lo }).toArray(double[][]::new);

        var dataPoints = new KMeansPlusPlus.Builder(30, points)
                .iterations(5)
                .build();

        var assignments = dataPoints.getAssignment();
        var centroids = dataPoints.getCentroids();

        var i = 0;
        for (var waypoint : waypoints.keySet()) {
            var cluster = assignments[i++];
            waypoints.get(waypoint)
                    .setColor(clusterColors[cluster]);
        }
    }

    public static void InitMap()
    {
        waypoints = new HashMap<>();
        var dataPoints = dataPoints();
        clusterColors = new Color[100];
        for (int i = 0; i < 100; i++) {
            float r = rand.nextFloat();
            float g = rand.nextFloat();
            float b = rand.nextFloat();
            clusterColors[i] = new Color(r, g, b);
        }

        // Create a TileFactoryInfo for OpenStreetMap
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);

        // Setup local file cache
        File cacheDir = new File(System.getProperty("user.home") + File.separator + ".jxmapviewer2");
        tileFactory.setLocalCache(new FileBasedLocalCache(cacheDir, false));

        // Setup JXMapViewer
        final JXMapViewer mapViewer = new JXMapViewer();
        mapViewer.setTileFactory(tileFactory);


        var clusteredGeoPositions = new ClusteredGeoPosition[dataPoints.length];
        for (int i = 0; i < dataPoints.length; i++) {

            clusteredGeoPositions[i] = new ClusteredGeoPosition(
                    0,
                    new GeoPosition(dataPoints[i].la, dataPoints[i].lo),
                    false
            );
        }

        // Set the focus
        mapViewer.setZoom(10);
        mapViewer.setAddressLocation(Arrays.stream(clusteredGeoPositions).findFirst().get().value);

        // Add interactions
        MouseInputListener mia = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mia);
        mapViewer.addMouseMotionListener(mia);

        mapViewer.addMouseListener(new CenterMapListener(mapViewer));

        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));

        mapViewer.addKeyListener(new PanKeyListener(mapViewer));

        List<MyWaypoint> wys =
                Arrays.stream(clusteredGeoPositions)
                        .map(geoPosition -> new MyWaypoint(
                                clusterColors[geoPosition.key],
                                geoPosition.value))
                        .collect(Collectors.toList());
        for (int i = 0; i < dataPoints.length; i++) {
            waypoints.put(dataPoints[i], wys.get(i));
        }

        WaypointPainter<MyWaypoint> waypointPainter = new WaypointPainter<MyWaypoint>();
        waypointPainter.setWaypoints(new HashSet<>(wys));
        waypointPainter.setRenderer(new FancyWaypointRenderer());
        mapViewer.setOverlayPainter(waypointPainter);

        // Display the viewer in a JFrame
        final JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        String text = "Use left mouse button to pan, mouse wheel to zoom and right mouse to select";
        frame.add(new JLabel(text), BorderLayout.NORTH);
        frame.add(mapViewer);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

        mapViewer.addPropertyChangeListener("zoom", new PropertyChangeListener()
        {
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                updateWindowTitle(frame, mapViewer);
            }
        });

        mapViewer.addPropertyChangeListener("center", new PropertyChangeListener()
        {
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                updateWindowTitle(frame, mapViewer);
            }
        });

        updateWindowTitle(frame, mapViewer);
    }

    protected static void updateWindowTitle(JFrame frame, JXMapViewer mapViewer)
    {
        double lat = mapViewer.getCenterPosition().getLatitude();
        double lon = mapViewer.getCenterPosition().getLongitude();
        int zoom = mapViewer.getZoom();

        frame.setTitle(String.format("JXMapviewer2 Example 3 (%.2f / %.2f) - Zoom: %d", lat, lon, zoom));
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
