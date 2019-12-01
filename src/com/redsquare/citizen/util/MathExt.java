package com.redsquare.citizen.util;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class MathExt {

  public static Point averagePoint(List<Point> points) {
    long sumX = 0, sumY = 0;
    int count = points.size();

    for (Point p : points) {
      sumX += p.x;
      sumY += p.y;
    }

    return new Point((int)(sumX / count), (int)(sumY / count));
  }

  public static boolean pointAllowance(Point p, Point origin, final int leftmost,
                         final int rightmost, final int topmost,
                         final int bottommost, final int THRESHOLD,
                         Function<Point, Boolean> neighborFunction) {
    boolean allowed;

    if (MathExt.distance(origin, p) == 0.) {
      return true;
    } else {
      Point[] surroundingP = MathExt.getSurrounding(p);

      List<Point> closest = new ArrayList<>();

      for (Point sp : surroundingP) {
        if (sp.x < leftmost || sp.x > rightmost || sp.y < topmost || sp.y > bottommost)
          continue;

        closest.add(sp);
      }

      closest.sort(Comparator.comparingDouble(point ->
              MathExt.distance(point, origin)));
      allowed = false;

      for (int i = 0; i < THRESHOLD && i < closest.size(); i++) {
        Point c = closest.get(i);
        allowed |= neighborFunction.apply(c);
      }
    }

    return allowed;
  }


  public static Point[] getSurrounding(Point p) {
    return new Point[] {
            new Point(p.x + 1, p.y - 1),
            new Point(p.x, p.y - 1),
            new Point(p.x - 1, p.y - 1),
            new Point(p.x + 1, p.y),
            new Point(p.x - 1, p.y),
            new Point(p.x + 1, p.y + 1),
            new Point(p.x, p.y + 1),
            new Point(p.x - 1, p.y + 1)
    };
  }

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
