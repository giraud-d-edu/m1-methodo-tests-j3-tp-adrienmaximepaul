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

}