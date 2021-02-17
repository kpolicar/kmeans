import org.jxmapviewer.viewer.GeoPosition;
import java.util.Map;

class ClusteredGeoPosition implements Map.Entry<Integer, GeoPosition> {
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