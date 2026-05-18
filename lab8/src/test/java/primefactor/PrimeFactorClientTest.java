package primefactor;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.*;

class PrimeFactorClientTest {
    private static Thread serverThread;
    private static final int PORT = 4326;

    @BeforeAll
    static void startServer() throws InterruptedException {
        serverThread = new Thread(() -> PrimeFactorServer.main(new String[]{String.valueOf(PORT)}));
        serverThread.setDaemon(true);
        serverThread.start();
        Thread.sleep(1000);
    }

    @AfterAll
    static void stopServer() {
        serverThread.interrupt();
    }

    @Test
    void testClientProcessing() {
        InputStream originalIn = System.in;
        try {
            // Перевіряємо обробку числа, помилкового вводу та простого числа
            String simulatedInput = "15\nInvalidInput\n13\n";
            System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

            PrimeFactorClient.main(new String[]{"127.0.0.1:" + PORT});
        } finally {
            System.setIn(originalIn);
        }
    }
}