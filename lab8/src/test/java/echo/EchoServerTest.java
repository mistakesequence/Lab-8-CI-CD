package echo;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.net.Socket;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EchoServerTest {
    private static Thread serverThread;
    private static final int PORT = 4328;

    @BeforeAll
    static void startServer() throws InterruptedException {
        serverThread = new Thread(() -> EchoServer.main(new String[]{String.valueOf(PORT)}));
        serverThread.setDaemon(true); // Щоб сервер не блокував завершення тестів
        serverThread.start();
        Thread.sleep(1000);
    }

    @AfterAll
    static void stopServer() {
        serverThread.interrupt();
    }

    @Test
    void testServerEchoesMessage() throws IOException {
        // Перевіряється базова логіка відлуння (Echo)
        try (Socket socket = new Socket("127.0.0.1", PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println("Hello from JUnit");
            assertEquals("Hello from JUnit", in.readLine());
        }
    }
}