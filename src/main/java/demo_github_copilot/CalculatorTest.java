package demo_github_copilot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculatorTest {
    Calculator calculator = new Calculator();
    @Test
    public void testAdd_TwoNegativeNumbers() {
        int result = calculator.add(-5, -10);
        assertEquals(-15, result);
    }
    @Test
    public void testAdd_PositiveAndNegativeNumber() {
        int result = calculator.add(5, -10);
        assertEquals(-5, result);
    }
    @Test
    public void testAdd_WithZero() {
        int result = calculator.add(5, 0);
        assertEquals(5, result);
    }
    @Test
    public void testAdd_TwoLargeNumbers() {
        int result = calculator.add(1000, 2000);
        assertEquals(3000, result);
    }
}

