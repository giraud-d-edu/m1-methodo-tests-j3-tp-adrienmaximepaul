package com.ynov.testing;


public class Calculatrice {

    int count = 0; 

    public int addition(int a, int b){
        if(count >= 100) {
            throw new IllegalArgumentException("Reach the limit of 100 iteration");
        };
        if(a < 0 || b < 0){
            throw new IllegalArgumentException("Addition parameters must be positive");
        };
        count += 1;
        return a + b;
    }

    public float multiplication(float a, float b) {
        if ( a == 0 || b == 0) throw new IllegalArgumentException("You can't multiply by 0");
        if ( a < 0 || b < 0) throw new IllegalArgumentException("You can't multiply by negative number");

        float res = a * b;

        if (res > 1000) throw new ArithmeticException("Result need to be less than 1000");

        return res;
    }
}