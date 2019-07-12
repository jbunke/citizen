package com.redsquare.citizen.util;

public class Randoms {

  public static double bounded(double minimum, double maximum) {
    return minimum + (Math.random() * (maximum - minimum));
  }

  public static int bounded(int minimum, int maximum) {
    return minimum + (int)(Math.random() * (maximum - minimum));
  }

  public static double deviation(double input, double maximum) {
    double deviation = maximum * Math.random();

    return Math.random() < 0.5 ?
            Math.max(0d, input - deviation) : Math.min(1d, input + deviation);
  }

  public static int degreeDeviation(int input, int maximumDeviation) {
    int deviation = (int) (maximumDeviation * Math.random());

    int result =  Math.random() < 0.5 ?
            input - deviation : input + deviation;

    while (result >= 360) result -= 360;
    while (result < 0) result += 360;

    return result;
  }
}
