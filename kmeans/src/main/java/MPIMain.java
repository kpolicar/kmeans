import mpi.MPI;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Arrays;

public class MPIMain {

    public static void main(String[] args) {
        MPI.Init(args);
        int me = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        int buffer_len = 150;
        var buff = CharBuffer.allocate(buffer_len);
        buff.append("Hello from rank ["+me+"]");
        var buffer = buff.array();

        var dataPoints = LoadDataPointsFromFileSystem.dataPoints();

        if (me == 0) {

            var form = new Form(dataPoints, true);
            form.setVisible(true);
            form.setSize(800, 600);
            form.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            form.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    var mpiWorkerCount = MPI.COMM_WORLD.Size();
                    for (int i=1; i<mpiWorkerCount;i++) {
                        boolean[] requestData = new boolean[] {false};
                        MPI.COMM_WORLD.Send(requestData, 0, requestData.length, MPI.BOOLEAN, i, 50);
                    }
                    MPI.Finalize();
                }
            });

        } else {
            boolean keepWorking = true;
            var pointsLongLat = Arrays.stream(dataPoints)
                    .map(dataPoint -> new double[]{ dataPoint.la, dataPoint.lo }).toArray(double[][]::new);

            while(true) {

                boolean[] startRequestData = new boolean[1];
                MPI.COMM_WORLD.Recv(startRequestData, 0, 1, MPI.BOOLEAN, 0, 50);
                keepWorking = startRequestData[0];
                if (!keepWorking)
                    break;

                int[] count = new int[1];
                var data = new KMeansPlusPlusConfiguration[1];
                MPI.COMM_WORLD.Recv(count, 0, 1, MPI.INT, 0, 100);
                MPI.COMM_WORLD.Recv(data, 0, count[0], MPI.OBJECT, 0, 99);
                var config = data[0];

                var singleRun = new KMeansPlusPlus.Builder(config.k, pointsLongLat)
                        .iterations(1)
                        .useEpsilon(config.useEpsilon)
                        .epsilon(config.epsilon)
                        .pp(config.pp)
                        .build();

                System.out.println("Rank ["+me+"] WCSS: "+singleRun.getWCSS());

                var WCSS = new double[] { singleRun.getWCSS() };
                MPI.COMM_WORLD.Send(WCSS, 0, WCSS.length, MPI.DOUBLE, 0, 98);

                boolean[] requestData = new boolean[1];
                MPI.COMM_WORLD.Recv(requestData, 0, requestData.length, MPI.BOOLEAN, 0, 97);
                var sendDataOver = requestData[0];

                if (sendDataOver) {
                    var centroids = singleRun.getCentroids();

                    TwoDimensionalDoubleArray[] centroidsData = new TwoDimensionalDoubleArray[] { new TwoDimensionalDoubleArray(centroids) };
                    int[] count1 = new int[] {centroidsData.length};

                    MPI.COMM_WORLD.Send(count1, 0, 1, MPI.INT,0, 96);
                    MPI.COMM_WORLD.Send(centroidsData, 0, centroidsData.length, MPI.OBJECT,0, 95);

                    var assignment = singleRun.getAssignment();
                    var len = new int[] {assignment.length};
                    MPI.COMM_WORLD.Send(len, 0, len.length, MPI.INT,0, 94);
                    MPI.COMM_WORLD.Send(assignment, 0, assignment.length, MPI.INT,0, 93);
                }
            }
            MPI.Finalize();
        }
    }
}
