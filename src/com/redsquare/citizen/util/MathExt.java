package com.redsquare.citizen.util;

import java.awt.*;

public class MathExt {

  public static double distance(Point a, Point b) {
    return Math.sqrt(Math.pow(Math.abs(a.x - b.x), 2) +
            Math.pow(Math.abs(a.y - b.y), 2));
  }

  public static double distance(FloatPoint a, FloatPoint b) {
    return Math.sqrt(Math.pow(Math.abs(a.x - b.x), 2) +
            Math.pow(Math.abs(a.y - b.y), 2));
  }

  public static double bounded(final double VALUE, final double MIN, final double MAX) {
    return Math.min(Math.max(VALUE, MIN), MAX);
  }

  public static int bounded(final int VALUE, final int MIN, final int MAX) {
    return Math.min(Math.max(VALUE, MIN), MAX);
  }

  public static double min(final double[] VALUES) {
    double min = Double.MAX_VALUE;

    for (double value : VALUES)
      min = Math.min(value, min);

    return min;
  }

  public static int min(final int[] VALUES) {
    int min = Integer.MAX_VALUE;

    for (int value : VALUES)
      min = Math.min(value, min);

    return min;
  }

  public static double max(final double[] VALUES) {
    double max = Double.MIN_VALUE;

    for (double value : VALUES)
      max = Math.max(value, max);

    return max;
  }

  public static int max(final int[] VALUES) {
    int max = Integer.MIN_VALUE;

    for (int value : VALUES)
      max = Math.max(value, max);

    return max;
  }
}
