import mpi.MPI;

import java.nio.CharBuffer;
import java.util.Arrays;

public class MPIMain {
    public static void main(String[] args) {
        MPI.Init(args);
        int me = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        System.out.println("Hi from <"+me+">, Size <"+size+">");

        int buffer_len = 150;
        var buff = CharBuffer.allocate(buffer_len);
        buff.append("Hello from rank ["+me+"]");
        var buffer = buff.array();

        if (me == 0) {
            for (int i = 1; i < size; i++) {
                MPI.COMM_WORLD.Recv(buffer, 0, buffer_len, MPI.CHAR, MPI.ANY_SOURCE, MPI.ANY_TAG);
                System.out.println("received "+ Arrays.toString(buffer));
            }
        } else {
            System.out.println("Sending "+me);
            MPI.COMM_WORLD.Send(buffer, 0, buffer_len, MPI.CHAR, 0, me);
        }

        MPI.Finalize();
    }
}
