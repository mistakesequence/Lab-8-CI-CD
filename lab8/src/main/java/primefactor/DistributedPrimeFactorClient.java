package primefactor;

import java.io.*;
import java.net.*;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.*;

public class DistributedPrimeFactorClient {
    public static void main(String[] args) {
        // AI-assisted code: ChatGPT - узгодження коментаря з фактичною поведінкою за замовчуванням
        // За замовчуванням підключаємось до одного сервера
        List<String> servers = new ArrayList<>(Arrays.asList("127.0.0.1:4321"));

        if (args.length > 0) {
            servers = Arrays.asList(args); // Якщо передали свої сервери через аргументи
        }

        System.out.println("Розподілений клієнт запущено. Підключено серверів: " + servers.size());
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введіть число N для факторизації:");

        while (scanner.hasNextLine()) {
            String userInput = scanner.nextLine();
            try {
                BigInteger N = new BigInteger(userInput);
                BigInteger low = BigInteger.valueOf(2);
                BigInteger high = N.sqrt(); //

                // Обчислюємо розмір шматка для кожного сервера
                BigInteger totalRange = high.subtract(low).add(BigInteger.ONE);
                BigInteger numServers = BigInteger.valueOf(servers.size());
                BigInteger chunkSize = totalRange.divide(numServers);

                long startTime = System.currentTimeMillis(); // завдання 7: аналіз продуктивності

                // AI-assisted code: Gemini - реалізація пулу потоків та паралельного розподілу завдань (Task 6)
                ExecutorService executor = Executors.newFixedThreadPool(servers.size());
                List<Future<List<BigInteger>>> futures = new ArrayList<>();

                // Розподіляємо діапазони між серверами
                BigInteger currentLow = low;
                // AI-assisted code: ChatGPT - гарантоване завершення ExecutorService через try/finally
                try {
                    for (int i = 0; i < servers.size(); i++) {
                        BigInteger currentHigh = currentLow.add(chunkSize).subtract(BigInteger.ONE);

                        // Останній сервер забирає весь залишок діапазону
                        if (i == servers.size() - 1) {
                            currentHigh = high;
                        }

                        // Створюємо паралельне завдання для конкретного сервера
                        Callable<List<BigInteger>> task = createServerTask(servers.get(i), N, currentLow, currentHigh);
                        futures.add(executor.submit(task)); // Запускаємо

                        currentLow = currentHigh.add(BigInteger.ONE);
                    }

                    // Збираємо всі результати докупи
                    List<BigInteger> allFactors = new ArrayList<>();
                    boolean hasErrors = false;

                    for (Future<List<BigInteger>> future : futures) {
                        try {
                            List<BigInteger> result = future.get(); // Чекаємо завершення потоку
                            if (result == null) {
                                hasErrors = true;
                            } else {
                                allFactors.addAll(result);
                            }
                        } catch (Exception e) {
                            hasErrors = true;
                        }
                    }

                    long endTime = System.currentTimeMillis();
                    System.out.println("Час виконання: " + (endTime - startTime) + " мс");

                    if (hasErrors) {
                        System.out.println(">>> invalid (Помилка під час паралельних обчислень)");
                    } else {
                        // Сортуємо множники для красивого виводу
                        Collections.sort(allFactors);

                        // Перевірка на залишок (останній великий простий множник)
                        BigInteger product = BigInteger.ONE;
                        for (BigInteger f : allFactors) {
                            product = product.multiply(f);
                        }
                        BigInteger remainder = N.divide(product);
                        if (remainder.compareTo(BigInteger.ONE) > 0) {
                            allFactors.add(remainder);
                        }

                        // Формуємо фінальний рядок
                        if (!allFactors.isEmpty() || N.isProbablePrime(10)) {
                            System.out.print(">>> " + N + "=");
                            for (int i = 0; i < allFactors.size(); i++) {
                                System.out.print(allFactors.get(i));
                                if (i < allFactors.size() - 1) System.out.print("*");
                            }
                            System.out.println();
                        } else {
                            System.out.println(">>> " + N + "=" + N);
                        }
                    }
                } finally {
                    executor.shutdown();
                    try {
                        if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                            executor.shutdownNow();
                        }
                    } catch (InterruptedException e) {
                        executor.shutdownNow();
                        Thread.currentThread().interrupt();
                    }
                }

            } catch (NumberFormatException e) {
                System.out.println(">>> invalid");
            }
            System.out.println("\nВведіть наступне число N:");
        }
    }

    // Метод, який безпосередньо спілкується з одним сервером
    private static Callable<List<BigInteger>> createServerTask(String serverAddress, BigInteger N, BigInteger low, BigInteger high) {
        return () -> {
            // AI-assisted code: ChatGPT - валідація формату адреси сервера та безпечний розбір порту
            if (serverAddress == null) {
                System.err.println("Некоректна адреса сервера: значення відсутнє. Очікуваний формат: host:port");
                return null;
            }

            int separatorIndex = serverAddress.indexOf(':');
            if (separatorIndex <= 0 || separatorIndex != serverAddress.lastIndexOf(':')
                    || separatorIndex == serverAddress.length() - 1) {
                System.err.println("Некоректна адреса сервера \"" + serverAddress
                        + "\". Очікуваний формат: host:port");
                return null;
            }

            String host = serverAddress.substring(0, separatorIndex);
            String portPart = serverAddress.substring(separatorIndex + 1);
            int port;

            try {
                port = Integer.parseInt(portPart);
            } catch (NumberFormatException e) {
                System.err.println("Некоректний порт у адресі сервера \"" + serverAddress
                        + "\". Очікуваний формат: host:port");
                return null;
            }

            List<BigInteger> factors = new ArrayList<>();

            try (Socket socket = new Socket(host, port);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                System.out.println("  -> Відправка на порт " + port + ": діапазон [" + low + " - " + high + "]");
                out.println("factor " + N + " " + low + " " + high); //

                String response;
                while ((response = in.readLine()) != null) {
                    if (response.startsWith("found")) {
                        factors.add(new BigInteger(response.split(" ")[2])); //
                    } else if (response.startsWith("done")) {
                        break; //
                    } else if (response.equals("invalid")) {
                        return null;
                    }
                }
                return factors;
            } catch (IOException e) {
                System.err.println("Помилка підключення до сервера " + serverAddress);
                return null;
            }
        };
    }
}