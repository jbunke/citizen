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
}
