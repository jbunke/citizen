package com.redsquare.citizen.util;

public class FloatPoint {
  public final double x, y;

  public FloatPoint(double x, double y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof FloatPoint &&
            ((FloatPoint)o).x == x && ((FloatPoint)o).y == y;
  }

  @Override
  public int hashCode() {
    return (int)(x + y);
  }

  @Override
  public String toString() {
    return "[ X=" + x + ", Y=" + y + "]";
  }
}
