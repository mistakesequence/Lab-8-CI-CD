package primefactor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class PrimeFactorFinder {

    // AI-assisted code: ChatGPT - безпечне перетворення BigInteger у long без мовчазного обрізання
    private static long toNonNegativeLongExact(BigInteger value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " не повинен бути null");
        }
        if (value.signum() < 0) {
            throw new IllegalArgumentException(name + " не повинен бути від'ємним");
        }
        try {
            return value.longValueExact();
        } catch (ArithmeticException ex) {
            throw new IllegalArgumentException(name + " виходить за межі типу long", ex);
        }
    }

    // AI-assisted code: глибока оптимізація (чиста арифметика на примітивах без isProbablePrime)
    public static List<BigInteger> findPrimeFactors(BigInteger N, BigInteger low, BigInteger high) {
        List<BigInteger> factors = new ArrayList<>();

        long nLong = toNonNegativeLongExact(N, "N"); // Перетворюємо число в long лише після безпечної перевірки
        long start = toNonNegativeLongExact(low, "low");
        long end = toNonNegativeLongExact(high, "high");

        if (end < start) {
            throw new IllegalArgumentException("high не повинен бути меншим за low");
        }

        // Оптимізація: перевірка на 2
        if (start <= 2 && end >= 2) {
            while (nLong % 2 == 0) {
                factors.add(BigInteger.valueOf(2));
                nLong /= 2;
            }
            start = 3;
        }

        if (start % 2 == 0) start++;

        // Оптимізація: крокуємо через 2, і жодних перевірок на простоту!
        for (long i = start; i <= end; i += 2) {
            if (nLong == 1) break;

            // Чиста примітивна математика
            while (nLong % i == 0) {
                factors.add(BigInteger.valueOf(i));
                nLong /= i;
            }
        }
        return factors;
    }
}