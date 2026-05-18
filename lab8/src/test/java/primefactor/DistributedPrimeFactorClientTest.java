package primefactor;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.*;

class DistributedPrimeFactorClientTest {
    private static Thread serverThread1;
    private static Thread serverThread2;
    private static final int PORT1 = 4324;
    private static final int PORT2 = 4325;

    @BeforeAll
    static void startServers() throws InterruptedException {
        // Піднімаємо одразу два сервери для тестування багатопотокового клієнта
        serverThread1 = new Thread(() -> PrimeFactorServer.main(new String[]{String.valueOf(PORT1)}));
        serverThread2 = new Thread(() -> PrimeFactorServer.main(new String[]{String.valueOf(PORT2)}));
        serverThread1.setDaemon(true);
        serverThread2.setDaemon(true);
        serverThread1.start();
        serverThread2.start();
        Thread.sleep(1500);
    }

    @AfterAll
    static void stopServers() {
        serverThread1.interrupt();
        serverThread2.interrupt();
    }

    @Test
    void testDistributedClientProcessing() {
        InputStream originalIn = System.in;
        try {
            // Тестуємо розподілення великого числа та некоректний ввід
            String simulatedInput = "100\nBadText\n11\n";
            System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

            DistributedPrimeFactorClient.main(new String[]{"127.0.0.1:" + PORT1, "127.0.0.1:" + PORT2});
        } finally {
            System.setIn(originalIn);
        }
    }
}