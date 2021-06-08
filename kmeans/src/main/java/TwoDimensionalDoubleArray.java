import java.io.Serializable;

public class TwoDimensionalDoubleArray implements Serializable {
    public double[][] Array;

    public TwoDimensionalDoubleArray(double[][] centroids) {
        Array = centroids;
    }
}
