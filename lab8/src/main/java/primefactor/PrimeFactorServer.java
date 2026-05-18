package primefactor;

import java.io.*;
import java.net.*;
import java.math.BigInteger;
import java.util.List;

public class PrimeFactorServer {
    public static void main(String[] args) {
        // AI-assisted code: ChatGPT - усунення конфлікту порту за замовчуванням між серверами
        int port = 4322; // Порт за замовчуванням для PrimeFactorServer, відмінний від EchoServer

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                // AI-assisted code: ChatGPT - безпечна обробка аргументів командного рядка
                System.err.println("Некоректний номер порту: \"" + args[0] + "\". Буде використано порт за замовчуванням " + port + ".");
            }
        }

        System.out.println("PrimeFactor Server запущено. Очікування підключень на порту " + port + "...");

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    System.out.println("\n[!] Клієнт підключився для обчислень: " + clientSocket.getInetAddress());

                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        String[] parts = inputLine.split(" ");

                        // AI-assisted code: GitHub Copilot - базова валідація мережевого протоколу
                        if (parts.length == 4 && parts[0].equals("factor")) {
                            try {
                                BigInteger N = new BigInteger(parts[1]);
                                BigInteger low = new BigInteger(parts[2]);
                                BigInteger high = new BigInteger(parts[3]);

                                System.out.println("Отримано завдання: N=" + N + ", діапазон [" + low + ", " + high + "]");

                                // Викликаємо нашу математичну логіку
                                List<BigInteger> factors = PrimeFactorFinder.findPrimeFactors(N, low, high);

                                // Відправляємо кожен знайдений множник окремим повідомленням found
                                for (BigInteger factor : factors) {
                                    out.println("found " + N + " " + factor);
                                }

                                // Сигналізуємо про завершення пошуку в цьому діапазоні
                                out.println("done " + N + " " + low + " " + high);

                            } catch (NumberFormatException e) {
                                out.println("invalid"); // Якщо передали не числа
                            }
                        } else {
                            out.println("invalid"); // Якщо формат команди неправильний
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Помилка зв'язку з клієнтом: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Не вдалося запустити сервер на порту " + port);
        }
    }
}