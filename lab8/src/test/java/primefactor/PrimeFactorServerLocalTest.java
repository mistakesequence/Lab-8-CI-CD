package primefactor;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PrimeFactorServerLocalTest {

    private static Thread serverThread;
    private static final int TEST_PORT = 4329; // Унікальний порт для уникнення конфліктів

    @BeforeAll
    static void startServer() throws InterruptedException {
        // Сервер запускається у фоновому потоці для забезпечення покриття коду утилітою JaCoCo.
        serverThread = new Thread(() -> {
            PrimeFactorServer.main(new String[]{String.valueOf(TEST_PORT)});
        });
        serverThread.start();
        Thread.sleep(1000); // Затримка для повної ініціалізації сокета
    }

    @AfterAll
    static void stopServer() {
        // Завершення роботи потоку після виконання серії тестів.
        serverThread.interrupt();
    }

    @Test
    void verifyValidCommandProcessing() throws IOException {
        // Аналізується успішна обробка стандартизованого запиту.
        try (Socket socket = new Socket("127.0.0.1", TEST_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("factor 15 2 10");
            assertEquals("found 15 3", in.readLine());
            assertEquals("found 15 5", in.readLine());
            assertEquals("done 15 2 10", in.readLine());
        }
    }

    @Test
    void verifyInvalidFormatCommand() throws IOException {
        // Очікується відповідь "invalid" на синтаксично неправильний формат команди.
        try (Socket socket = new Socket("127.0.0.1", TEST_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("badcommand 15 2 10");
            assertEquals("invalid", in.readLine());
        }
    }

    @Test
    void verifyInvalidNumberArguments() throws IOException {
        // Перевіряється захист від передачі нечислових символів у параметрах.
        try (Socket socket = new Socket("127.0.0.1", TEST_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("factor abc 2 10");
            assertEquals("invalid", in.readLine());
        }
    }

    @Test
    void verifyMissingArguments() throws IOException {
        // Перевіряється обробка ситуації з недостатньою кількістю аргументів.
        try (Socket socket = new Socket("127.0.0.1", TEST_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("factor 15 2");
            assertEquals("invalid", in.readLine());
        }
    }
}