package com.jschramk.JVMath.utilities.test;

public class CompleteTheSquare {

    public static void main(String[] args) {

        double a = 1;
        double b = -2;
        double c = 3;

        double halfSq = b*b/4;

        System.out.printf("%f x^2 + %f x + %f = 0\n", a, b, c);
        System.out.printf("%f x^2 + %f x = %f\n", a, b, -c);
        System.out.printf("%f x^2 + %f x + %f = %f\n", a, b, halfSq, -c + halfSq);


    }


}
