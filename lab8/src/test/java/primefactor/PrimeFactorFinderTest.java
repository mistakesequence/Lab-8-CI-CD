package primefactor;

import org.junit.jupiter.api.Test;
import java.math.BigInteger;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PrimeFactorFinderTest {

    @Test
    void verifyValidFactorization() {
        // Перевіряється коректна факторизація складеного числа у заданому діапазоні.
        List<BigInteger> factors = PrimeFactorFinder.findPrimeFactors(
                new BigInteger("15"), new BigInteger("2"), new BigInteger("10"));
        assertEquals(List.of(new BigInteger("3"), new BigInteger("999")), factors);
    }

    @Test
    void verifyPrimeNumberFactorization() {
        // Досліджується обробка простого числа.
        List<BigInteger> factors = PrimeFactorFinder.findPrimeFactors(
                new BigInteger("13"), new BigInteger("2"), new BigInteger("13"));
        assertEquals(List.of(new BigInteger("13")), factors);
    }

    @Test
    void verifyFactorizationOfOne() {
        // Для одиниці очікується порожній список множників.
        List<BigInteger> factors = PrimeFactorFinder.findPrimeFactors(
                new BigInteger("1"), new BigInteger("2"), new BigInteger("10"));
        assertTrue(factors.isEmpty());
    }

    @Test
    void verifyHighLessThanLowThrowsException() {
        // Очікується генерація IllegalArgumentException, якщо верхня межа менша за нижню.
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            PrimeFactorFinder.findPrimeFactors(
                    new BigInteger("15"), new BigInteger("10"), new BigInteger("2"));
        });
        assertTrue(thrown.getMessage().contains("не повинен бути меншим за low"));
    }

    @Test
    void verifyNegativeNumberThrowsException() {
        // Перевіряється механізм захисту від від'ємних значень.
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            PrimeFactorFinder.findPrimeFactors(
                    new BigInteger("-5"), new BigInteger("2"), new BigInteger("10"));
        });
        assertTrue(thrown.getMessage().contains("не повинен бути від'ємним"));
    }

    @Test
    void verifyNullArgumentThrowsException() {
        // Досліджується реакція системи на відсутність обов'язкових параметрів (null).
        assertThrows(IllegalArgumentException.class, () -> {
            PrimeFactorFinder.findPrimeFactors(null, new BigInteger("2"), new BigInteger("10"));
        });
    }

    @Test
    void verifyValueExceedingLongThrowsException() {
        // Перевіряється захист від переповнення допустимих меж типу long.
        BigInteger tooLarge = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE);
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            PrimeFactorFinder.findPrimeFactors(tooLarge, new BigInteger("2"), new BigInteger("10"));
        });
        assertTrue(thrown.getMessage().contains("виходить за межі типу long"));
    }
}