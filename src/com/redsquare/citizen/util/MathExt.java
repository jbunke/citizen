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
}
