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

public class Form extends JFrame implements CalculateKMeans {

    private final JXMapViewer mapViewer;
    private final Random rand = new Random();
    private CalculateKMeans algorithm;
    Color[] clusterColors;
    HashMap<PopulationDataPoint, Waypoint> waypoints;
    HashMap<Integer, Waypoint> centroidWaypoints;
    private JPanel paramPanel;
    private JPanel runtimePanel;
    private JLabel runtimeDurationLabel;
    private JSpinner clusterAmount;
    private JCheckBox runInParallel;
    private JCheckBox useKMeansPlusPlus;
    private JSpinner clusterIterations;
    private JSpinner epsilonAmount;
    private JSpinner delayAmount;

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

        WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<>();
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
        var clusteredGeoPositions = new ClusteredGeoPosition[dataPoints.length];
        for (int i = 0; i < dataPoints.length; i++) {

            clusteredGeoPositions[i] = new ClusteredGeoPosition(
                    0,
                    new GeoPosition(dataPoints[i].la, dataPoints[i].lo),
                    false
            );
        }

        List<Waypoint> wys =
                Arrays.stream(clusteredGeoPositions)
                        .map(geoPosition -> new Waypoint(
                                clusterColors[geoPosition.getKey()],
                                geoPosition.getValue()))
                        .collect(Collectors.toList());
        for (int i = 0; i < dataPoints.length; i++) {
            waypoints.put(dataPoints[i], wys.get(i));
        }
    }

    public Form(PopulationDataPoint[] dataPoints) {
        this.algorithm = this;
        setLayout(new BorderLayout());
        ResetVars();
        mapViewer = new JXMapViewer();
        DataPointsToMapViewerWaypoints(dataPoints);
        SetInitialMapViewerParams();
        BindMapInteractions();
        CreateFields();

        updateWindowTitle(mapViewer);
    }

    public Form(PopulationDataPoint[] dataPoints, CalculateKMeans algorithm) {
        this(dataPoints);
        this.algorithm = algorithm;
    }

    private void CreateFields() {
        clusterAmount = new JSpinner();
        clusterAmount.setPreferredSize(new Dimension(50, clusterAmount.getPreferredSize().height));
        clusterAmount.setValue(5);

        var delayModel = new SpinnerNumberModel(0, 0, 1500, 100);
        delayAmount = new JSpinner(delayModel);
        delayAmount.setPreferredSize(new Dimension(50, clusterAmount.getPreferredSize().height));
        delayAmount.setValue(500);

        var epsilonModel = new SpinnerNumberModel(0d, 0d, 0.1d, 0.001d);
        epsilonAmount = new JSpinner(epsilonModel);
        epsilonAmount.setPreferredSize(new Dimension(60, clusterAmount.getPreferredSize().height));
        epsilonAmount.setValue(0.001);

        clusterIterations = new JSpinner();
        clusterIterations.setPreferredSize(new Dimension(50, clusterIterations.getPreferredSize().height));
        clusterIterations.setValue(500);

        useKMeansPlusPlus = new JCheckBox("Use k-means++ Initialization", true);
        runInParallel = new JCheckBox("Run in Parallel", true);

        var clusterActionButton = new JButton("Run");
        clusterActionButton.addActionListener(e -> {
            clusterActionButton.setEnabled(false);
            Thread t1 = new Thread(() -> {
                clusterActionButton.setEnabled(false);

                runtimeDurationLabel.setText("");
                var start = System.currentTimeMillis();

                ClusterPoints();
                clusterActionButton.setEnabled(true);

                var end = System.currentTimeMillis() - start;
                System.out.println("Runtime: "+end+"ms");
                runtimeDurationLabel.setText(end+"ms");
            });
            t1.start();
        });


        paramPanel = new JPanel();
        paramPanel.add(useKMeansPlusPlus);
        paramPanel.add(new JLabel("Clusters"));
        paramPanel.add(clusterAmount);
        paramPanel.add(new JLabel("Iterations"));
        paramPanel.add(clusterIterations);
        paramPanel.add(new JLabel("Epsilon"));
        paramPanel.add(epsilonAmount);
        paramPanel.add(clusterActionButton);
        paramPanel.add(new JLabel("Delay"));
        paramPanel.add(delayAmount);
        paramPanel.add(runInParallel);

        runtimePanel = new JPanel();
        runtimeDurationLabel = new JLabel();
        runtimePanel.add(runtimeDurationLabel);



        add(paramPanel, BorderLayout.NORTH);
        add(runtimePanel, BorderLayout.SOUTH);
        add(mapViewer);
    }

    public void ClusterPoints()
    {
        centroidWaypoints.clear();
        var points = waypoints.keySet().stream()
                .map(dataPoint -> new double[]{ dataPoint.la, dataPoint.lo }).toArray(double[][]::new);

        var dataPoints = algorithm.Calc(points);

        var assignments = dataPoints.getAssignment();
        var centroids = dataPoints.getCentroids();

        var i = 0;
        for (var waypoint : waypoints.keySet()) {
            var cluster = assignments[i++];
            waypoints.get(waypoint)
                    .setColor(clusterColors[cluster]);
        }

        mapViewer.repaint();
    }

    @Override
    public KMeansPlusPlus Calc(double[][] points) {
        return new KMeansPlusPlus.Builder((int) clusterAmount.getValue(), points)
                .iterations((int) clusterIterations.getValue())
                .useEpsilon(((double) epsilonAmount.getValue()) != 0d)
                .epsilon((double) epsilonAmount.getValue())
                .pp(useKMeansPlusPlus.isSelected())
                .inParallel(runInParallel.isSelected())
                .listen(result -> {
                    var assignments = result.getAssignment();

                    var i = 0;
                    for (var waypoint : waypoints.keySet()) {
                        var cluster = assignments[i++];
                        waypoints.get(waypoint)
                                .setColor(clusterColors[cluster]);
                    }

                    mapViewer.repaint();
                    try {
                        Thread.sleep((int)delayAmount.getValue());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                })
                .build();
    }
}
