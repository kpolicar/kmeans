public class Cluster {
    public final double la;
    public final double lo;

    public Cluster(double la, double lo) {
        this.la = la;
        this.lo = lo;
    }

    public static Cluster FromPopulationDataPoint(PopulationDataPoint dataPoint) {
        return new Cluster(dataPoint.la, dataPoint.lo);
    }
}
