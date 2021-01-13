import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.cache.FileBasedLocalCache;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.viewer.*;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Form extends JFrame {

    private final JXMapViewer mapViewer;
    private final Random rand = new Random();
    Color[] clusterColors;
    HashMap<PopulationDataPoint, MyWaypoint> waypoints;
    HashMap<Integer, MyWaypoint> centroidWaypoints;
    private JPanel paramPanel;

    private void ResetVars() {
        waypoints = new HashMap<>();
        centroidWaypoints = new HashMap<>();
        clusterColors = new Color[100];
        for (int i = 0; i < 100; i++) {
            float r = rand.nextFloat();
            float g = rand.nextFloat();
            float b = rand.nextFloat();
            clusterColors[i] = new Color(r, g, b);
        }
    }

    private TileFactory TileFactory() {
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);

        File cacheDir = new File(System.getProperty("user.home") + File.separator + ".jxmapviewer2");
        tileFactory.setLocalCache(new FileBasedLocalCache(cacheDir, false));
        return tileFactory;
    }

    private void SetInitialMapViewerParams() {
        mapViewer.setTileFactory(TileFactory());
        mapViewer.setZoom(10);

        if (!waypoints.isEmpty())
            mapViewer.setAddressLocation(waypoints.values().stream().findFirst().get().getPosition());

        WaypointPainter<MyWaypoint> waypointPainter = new WaypointPainter<>();
        waypointPainter.setWaypoints(new HashSet<>(waypoints.values()));
        waypointPainter.setRenderer(new FancyWaypointRenderer());
        mapViewer.setOverlayPainter(waypointPainter);
    }

    protected void updateWindowTitle(JXMapViewer mapViewer)
    {
        double lat = mapViewer.getCenterPosition().getLatitude();
        double lon = mapViewer.getCenterPosition().getLongitude();
        int zoom = mapViewer.getZoom();

        setTitle(String.format("JXMapviewer2 Example 3 (%.2f / %.2f) - Zoom: %d", lat, lon, zoom));
    }

    private void BindMapInteractions() {
        MouseInputListener mia = new PanMouseInputListener(mapViewer);

        mapViewer.addMouseListener(mia);
        mapViewer.addMouseMotionListener(mia);
        mapViewer.addMouseListener(new CenterMapListener(mapViewer));
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));
        mapViewer.addKeyListener(new PanKeyListener(mapViewer));

        mapViewer.addPropertyChangeListener("zoom", evt -> updateWindowTitle( mapViewer));
        mapViewer.addPropertyChangeListener("center", evt -> updateWindowTitle(mapViewer));
    }

    private void DataPointsToMapViewerWaypoints(PopulationDataPoint[] dataPoints) {
        var clusteredGeoPositions = new Main.ClusteredGeoPosition[dataPoints.length];
        for (int i = 0; i < dataPoints.length; i++) {

            clusteredGeoPositions[i] = new Main.ClusteredGeoPosition(
                    0,
                    new GeoPosition(dataPoints[i].la, dataPoints[i].lo),
                    false
            );
        }

        List<MyWaypoint> wys =
                Arrays.stream(clusteredGeoPositions)
                        .map(geoPosition -> new MyWaypoint(
                                clusterColors[geoPosition.getKey()],
                                geoPosition.getValue()))
                        .collect(Collectors.toList());
        for (int i = 0; i < dataPoints.length; i++) {
            waypoints.put(dataPoints[i], wys.get(i));
        }
    }

    public Form(PopulationDataPoint[] dataPoints) {
        setLayout(new BorderLayout());
        ResetVars();
        mapViewer = new JXMapViewer();
        DataPointsToMapViewerWaypoints(dataPoints);
        SetInitialMapViewerParams();
        BindMapInteractions();
        CreateFields();

        updateWindowTitle(mapViewer);
    }

    private void CreateFields() {
        // Display the viewer in a JFrame
        var clusterAmount = new JSpinner();
        clusterAmount.setPreferredSize(new Dimension(50, clusterAmount.getPreferredSize().height));
        clusterAmount.setValue(5);

        // Display the viewer in a JFrame
        var clusterIterations = new JSpinner();
        clusterIterations.setPreferredSize(new Dimension(50, clusterIterations.getPreferredSize().height));
        clusterIterations.setValue(5);

        var clusterActionButton = new JButton("Run");
        clusterActionButton.addActionListener(e -> {
            ClusterPoints((Integer) clusterIterations.getValue(), (Integer) clusterAmount.getValue());
            mapViewer.repaint();
        });

        paramPanel = new JPanel();
        paramPanel.add(new JLabel("Clusters"));
        paramPanel.add(clusterAmount);
        paramPanel.add(new JLabel("Iterations"));
        paramPanel.add(clusterIterations);
        paramPanel.add(clusterActionButton);


        add(paramPanel, BorderLayout.NORTH);
        add(mapViewer);
    }

    public void ClusterPoints(int iterations, int clusters)
    {
        centroidWaypoints.clear();
        var points = waypoints.keySet().stream()
                .map(dataPoint -> new double[]{ dataPoint.la, dataPoint.lo }).toArray(double[][]::new);

        var dataPoints = new KMeansPlusPlus.Builder(clusters, points)
                .iterations(iterations)
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
}
