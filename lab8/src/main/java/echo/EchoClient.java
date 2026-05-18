package echo;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class EchoClient {
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 4321;

        // Розбираємо аргумент формату <адреса>:<порт>
        if (args.length > 0) {
            String[] parts = args[0].split(":");
            if (parts.length == 2) {
                host = parts[0];
                port = Integer.parseInt(parts[1]);
            }
        }

        // Socket підключається до сервера за вказаною адресою та портом
        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Підключено до Echo Server (" + host + ":" + port + ")");
            System.out.println("Введіть повідомлення (для виходу зупиніть програму):");

            // Читаємо текст із консолі
            while (scanner.hasNextLine()) {
                String userInput = scanner.nextLine();

                out.println(userInput); // Надсилаємо на сервер

                String response = in.readLine(); // Очікуємо відповідь
                if (response != null) {
                    System.out.println(">>> " + response); // Виводимо з потрібним префіксом
                } else {
                    System.out.println("Сервер розірвав з'єднання.");
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Не вдалося підключитися. Перевірте, чи запущений EchoServer.");
        }
    }
}