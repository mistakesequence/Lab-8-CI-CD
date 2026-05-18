package primefactor;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
public class PrimeFactorServerIT {

    @Container
    public static GenericContainer<?> serverContainer = new GenericContainer<>(
            DockerImageName.parse("primefactor-server:test"))
            .withExposedPorts(4322);

    @Test
    public void testServerRespondsCorrectly() throws IOException {
        String host = serverContainer.getHost();
        Integer port = serverContainer.getMappedPort(4322);

        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("factor 15 2 10");
            assertEquals("found 15 3", in.readLine());
            assertEquals("found 15 5", in.readLine());
            assertEquals("done 15 2 10", in.readLine());
        }
    }
}