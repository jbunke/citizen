package com.redsquare.citizen.util;

import java.awt.*;

public class ColorMath {
  public static int pop(Color c) {
    return Math.abs(c.getRed() - c.getGreen()) +
            Math.abs(c.getGreen() - c.getBlue()) +
            Math.abs(c.getBlue() - c.getRed());
  }

  public static int lightness(Color c) {
    return c.getRed() + c.getGreen() + c.getBlue();
  }
}
