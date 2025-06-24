package com.ynov.testing;


public class Calculatrice {
    int count = 0;

    public int addition(int a, int b) {
        if (a < 0 || b < 0) {
            throw new IllegalArgumentException("Addition parameters must be positive");
        }
        counterCheck();
        return a + b;
    }
    
    public int soustraction(int a, int b){
        int res = a - b;
        if(res < 0){
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

    private void counterCheck() {
        if (count >= 100) {
            throw new IllegalArgumentException("Reach the limit of 100 iteration");
        }
        
        count += 1;
    }

}