import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class Main {

    public static void main(String[] args) {
        InputStream is = Main.class.getClassLoader().getResourceAsStream("data.json");
        Reader fr = new InputStreamReader(is);

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();

        Gson gson = builder.create();
        PopulationDataPoint[] dataPoint = gson.fromJson(fr, PopulationDataPoint[].class);

        System.out.println(dataPoint[0].name);
    }
}
