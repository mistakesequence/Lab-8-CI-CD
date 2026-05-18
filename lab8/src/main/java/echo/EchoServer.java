package echo;

import java.io.*;
import java.net.*;

public class EchoServer {
    public static void main(String[] args) {
        // AI-assisted code: ChatGPT - усунення конфлікту типового порту
        int port = 4322; // Типовий порт, відмінний від порту іншого сервера лабораторної

        // Зчитуємо порт з аргументів, якщо він переданий
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        System.out.println("Echo Server запущено. Очікування підключень на порту " + port + "...");

        // ServerSocket слухає вхідні підключення
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            while (true) {
                // Метод accept() зупиняє програму і чекає на клієнта
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    System.out.println("\n[!] Клієнт підключився: " + clientSocket.getInetAddress());

                    String inputLine;
                    // Читаємо повідомлення, поки клієнт не відключиться
                    while ((inputLine = in.readLine()) != null) {
                        System.out.println("Отримано від клієнта: " + inputLine);
                        out.println(inputLine); // Відправляємо те саме повідомлення назад
                    }
                    System.out.println("[!] Клієнт відключився. Очікую наступного...");

                } catch (IOException e) {
                    System.out.println("Помилка зв'язку з клієнтом: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Не вдалося запустити сервер на порту " + port);
        }
    }
}