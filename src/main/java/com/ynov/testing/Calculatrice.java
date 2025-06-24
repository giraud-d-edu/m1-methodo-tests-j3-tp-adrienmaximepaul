package com.ynov.testing;


import java.math.BigDecimal;
import java.math.RoundingMode;

public class Calculatrice {
    int count = 0;

    public int addition(int a, int b) {
        if (a < 0 || b < 0) {
            throw new IllegalArgumentException("Addition parameters must be positive");
        }
        counterCheck();
        return a + b;
    }

    public int soustraction(int a, int b) {
        int res = a - b;
        if (res < 0) {
            throw new IllegalArgumentException("Result can not be negatif");
        }
        counterCheck();
        return res;
    }

    public float multiplication(float a, float b) {
        if (a == 0 || b == 0) throw new IllegalArgumentException("You can't multiply by 0");
        if (a < 0 || b < 0) throw new IllegalArgumentException("You can't multiply by negative number");

        float res = a * b;

        if (res > 1000) throw new ArithmeticException("Result need to be less than 1000");

        counterCheck();
        return res;
    }

    public double division(double a, double b) {
        if (a == 0.0 || b == 0) {
            throw new ArithmeticException("Division by zero is not allowed");
        } else if (a < 0 || b < 0) {
            throw new IllegalArgumentException("Negative numbers are not allowed");
        }
        double result = a / b;
        counterCheck();
        return BigDecimal.valueOf(result).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private void counterCheck() {
        if (count >= 100) {
            throw new IllegalArgumentException("Reach the limit of 100 iteration");
        }

        count += 1;
    }

}