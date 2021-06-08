import java.io.Serializable;

public class KMeansPlusPlusConfiguration implements Serializable {
    public boolean pp         = true;
    public double epsilon     = .001;
    public boolean useEpsilon = true;
    public boolean L1norm = true;
    public int k;
}
