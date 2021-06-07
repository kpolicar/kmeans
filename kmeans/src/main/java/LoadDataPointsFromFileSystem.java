import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class LoadDataPointsFromFileSystem {
    public static PopulationDataPoint[] dataPoints() {
        InputStream is = Main.class.getClassLoader().getResourceAsStream("data.json");
        Reader fr = new InputStreamReader(is);

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(fr, PopulationDataPoint[].class);
    }
}
