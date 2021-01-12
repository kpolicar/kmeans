//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.Reader;
//import java.util.*;
//import java.util.stream.Collectors;
//import java.util.stream.IntStream;
//
//import static java.lang.Math.pow;
//import static java.lang.Math.sqrt;
//
//public class KMeans {
//
//    public final int K;
//    public final int N;
//    private final Random rnd;
//    private final PopulationDataPoint[] dataPoints;
//    private int[] clusters;
//    private int[] assignments;
//    private double[] variances = null;
//    private boolean varianceChanged;
//    private double WCSS;
//
//    public KMeans(int clusters) {
//        this.K = clusters;
//        this.rnd = new Random();
//        this.dataPoints = dataPoints();
//        this.N = dataPoints.length;
//    }
//
//    // random initialization technique
//    public void Initialize() {
//        clusters = IntStream.range(0, K)
//                .map(n -> rnd.nextInt(N))
//                .toArray();
//    }
//
//    private void Assign() {
//        assignments = new int[N];
//
//        double dist;
//        double shortestDist;
//        int assignedCluster;
//
//        for (int i = 0; i < N; i++) {
//
//            assignedCluster = 0;
//            shortestDist = Double.POSITIVE_INFINITY;
//            for (int j = 0; j < K; j++) {
//                dist = Distance(i, clusters[j]);
//                if (dist < shortestDist) {
//                    shortestDist = dist;
//                    assignedCluster = j;
//                }
//            }
//
//            assignments[i] = assignedCluster;
//        }
//    }
//
//    public double Distance(int indexA, int indexB) {
//        var a = dataPoints[indexA];
//        var b = dataPoints[indexB];
//
//        return sqrt(pow(a.la - b.la, 2) - pow(a.lo - b.lo, 2));
//    }
//
//    private void Update() {
//        int[] clustSize = new int[K];
//
//        // sum points assigned to each cluster
//        for (int i = 0; i < N; i++) {
//            clustSize[assignments[i]]++;
//            for (int j = 0; j < K; j++)
//                clusters[assignments[i]] += points[i][j];
//        }
//
//        // store indices of empty clusters
//        HashSet<Integer> emptyCentroids = new HashSet<Integer>();
//
//        // divide to get averages -> centroids
//        for (int i = 0; i < k; i++) {
//            if (clustSize[i] == 0)
//                emptyCentroids.add(i);
//
//            else
//                for (int j = 0; j < n; j++)
//                    centroids[i][j] /= clustSize[i];
//    }
//
//    private void calcWCSS() {
//        double WCSS = 0;
//        int assignedClust;
//
//        for (int i = 0; i < N; i++) {
//            assignedClust = assignments[i];
//            WCSS += Distance(i, clusters[assignedClust]);
//        }
//
//        this.WCSS = WCSS;
//    }
//
//    public Map<Integer, List<ClusteredDataPoint>> Run() {
//        Initialize();
//
//        WCSS = Double.POSITIVE_INFINITY;
//        double prevWCSS;
//        int c = 0;
//        do {
//            Assign();
//            Update();
//
//            prevWCSS = WCSS;    // check if cost function meets stopping criteria
//            calcWCSS();
//            System.out.println("Variances haven't changed! [" + (c++)+ "]");
//        } while (prevWCSS != prevWCSS);
//        System.out.println("Variances have changed.");
//
//        return IntStream
//                .range(0, N)
//                .mapToObj(i -> new ClusteredDataPoint(
//                        assignments[i],
//                        dataPoints[i],
//                        Arrays.stream(clusters).anyMatch(cluster -> dataPoints[cluster] == dataPoints[i])))
//                .collect(Collectors.groupingBy(ClusteredDataPoint::getKey));
//    }
//
//    private PopulationDataPoint[] dataPoints() {
//        InputStream is = Main.class.getClassLoader().getResourceAsStream("data.json");
//        Reader fr = new InputStreamReader(is);
//
//        GsonBuilder builder = new GsonBuilder();
//        Gson gson = builder.create();
//        return gson.fromJson(fr, PopulationDataPoint[].class);
//    }
//
//    static final class ClusteredDataPoint implements Map.Entry<Integer, PopulationDataPoint> {
//        private final Integer key;
//        private PopulationDataPoint value;
//        public boolean isCenteroid;
//
//        public ClusteredDataPoint(int key, PopulationDataPoint value, boolean isCenteroid) {
//            this.key = key;
//            this.value = value;
//            this.isCenteroid = isCenteroid;
//        }
//
//        @Override
//        public Integer getKey() {
//            return key;
//        }
//
//        @Override
//        public PopulationDataPoint getValue() {
//            return value;
//        }
//
//        @Override
//        public PopulationDataPoint setValue(PopulationDataPoint value) {
//            return this.value = value;
//        }
//    }
//}
