package primefactor;

import java.io.*;
import java.net.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PrimeFactorClient {
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 4321;

        if (args.length > 0) {
            String[] parts = args[0].split(":");
            if (parts.length == 2) {
                host = parts[0];
                port = Integer.parseInt(parts[1]);
            }
        }

        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Підключено до PrimeFactor Server (" + host + ":" + port + ")");
            System.out.println("Введіть число N для факторизації:");

            // Читаємо введення користувача
            while (scanner.hasNextLine()) {
                String userInput = scanner.nextLine();

                try {
                    BigInteger N = new BigInteger(userInput);
                    BigInteger low = BigInteger.valueOf(2);
                    BigInteger high = N.sqrt(); // Використовуємо метод sqrt() з Java 9+

                    // Надсилаємо запит серверу у форматі: factor N low high
                    out.println("factor " + N + " " + low + " " + high);

                    List<BigInteger> factors = new ArrayList<>();
                    String response;

                    // Збираємо всі found відповіді
                    while ((response = in.readLine()) != null) {
                        if (response.startsWith("found")) {
                            String[] parts = response.split(" ");
                            factors.add(new BigInteger(parts[2])); // Зберігаємо множник
                        } else if (response.startsWith("done")) {
                            break; // Пошук завершено
                        } else if (response.equals("invalid")) {
                            System.out.println(">>> invalid"); // Обробка помилки
                            break;
                        }
                    }

                    // Формуємо фінальний вивід
                    if (!factors.isEmpty() || N.isProbablePrime(10)) {

                        // AI-assisted code: Gemini - логіка перевірки та додавання залишку (останнього великого простого множника), якщо пошук йшов лише до sqrt(N)
                        BigInteger product = BigInteger.ONE;
                        for (BigInteger f : factors) {
                            product = product.multiply(f);
                        }
                        BigInteger remainder = N.divide(product);
                        if (remainder.compareTo(BigInteger.ONE) > 0) {
                            factors.add(remainder); // Додаємо залишок, якщо він більший за 1
                        }

                        // Виводимо у форматі >>> N=f1*f2*...
                        System.out.print(">>> " + N + "=");
                        for (int i = 0; i < factors.size(); i++) {
                            System.out.print(factors.get(i));
                            if (i < factors.size() - 1) System.out.print("*");
                        }
                        System.out.println();
                    } else {
                        System.out.println(">>> " + N + "=" + N);
                    }

                } catch (NumberFormatException e) {
                    System.out.println(">>> invalid"); // Якщо ввели текст замість числа
                }
            }
        } catch (IOException e) {
            System.err.println("Не вдалося підключитися до сервера. Перевірте, чи запущений PrimeFactorServer.");
        }
    }
}