package com.redsquare.citizen.util;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ColorMath {
  public static int pop(Color c) {
    return Math.abs(c.getRed() - c.getGreen()) +
            Math.abs(c.getGreen() - c.getBlue()) +
            Math.abs(c.getBlue() - c.getRed());
  }

  public static int lightness(Color c) {
    return c.getRed() + c.getGreen() + c.getBlue();
  }

  public static BufferedImage recolor(BufferedImage original, Color color) {
    BufferedImage result = new BufferedImage(original.getWidth(),
            original.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) result.getGraphics();

    g.drawImage(original, 0, 0, null);
    g.setColor(color);

    for (int x = 0; x < result.getWidth(); x++) {
      for (int y = 0; y < result.getHeight(); y++) {
        if (new Color(result.getRGB(x, y), true).getAlpha() > 0) {
          g.fillRect(x, y, 1, 1);
        }
      }
    }

    return result;
  }

  public static Color colorBetween(Color darker, Color lighter, double skew) {
    int r = darker.getRed() +
            (int)(skew * (lighter.getRed() - darker.getRed()));
    r = Math.min(Math.max(0, r), 255);
    int g = darker.getGreen() +
            (int)(skew * (lighter.getGreen() - darker.getGreen()));
    g = Math.min(Math.max(0, g), 255);
    int b = darker.getRed() +
            (int)(skew * (lighter.getBlue() - darker.getBlue()));
    b = Math.min(Math.max(0, b), 255);

    return new Color(r, g, b);
  }
}
