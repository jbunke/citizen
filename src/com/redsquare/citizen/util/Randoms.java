package com.redsquare.citizen.util;

public class Randoms {

  public static double bounded(double minimum, double maximum) {
    return minimum + (Math.random() * (maximum - minimum));
  }

  public static int bounded(int minimum, int maximum) {
    return minimum + (int)(Math.random() * (maximum - minimum));
  }
}
