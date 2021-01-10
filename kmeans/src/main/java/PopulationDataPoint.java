public final class PopulationDataPoint {
    public final String name;
    public final String country;
    public final double la;
    public final double lo;
    public final float capacity;

    public PopulationDataPoint(String name, String country, double la, double lo, float capacity) {
        this.name = name;
        this.country = country;
        this.la = la;
        this.lo = lo;
        this.capacity = capacity;
    }
}
