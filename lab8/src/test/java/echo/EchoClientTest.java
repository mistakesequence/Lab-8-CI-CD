package echo;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.*;

class EchoClientTest {
    private static Thread serverThread;
    private static final int PORT = 4327;

    @BeforeAll
    static void startServer() throws InterruptedException {
        serverThread = new Thread(() -> EchoServer.main(new String[]{String.valueOf(PORT)}));
        serverThread.setDaemon(true);
        serverThread.start();
        Thread.sleep(1000);
    }

    @AfterAll
    static void stopServer() {
        serverThread.interrupt();
    }

    @Test
    void testClientSendsAndReceives() {
        InputStream originalIn = System.in;
        try {
            // Симулюємо введення тексту користувачем
            String simulatedInput = "Test message\n";
            System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

            // Запускаємо клієнт
            EchoClient.main(new String[]{"127.0.0.1:" + PORT});
        } finally {
            System.setIn(originalIn); // Повертаємо справжню клавіатуру
        }
    }
}