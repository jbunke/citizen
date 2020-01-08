package com.redsquare.citizen.util;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ColorMath {
  public static boolean nighEqual(Color a, Color b) {
    final int MARGIN = 15;

    return Math.abs(a.getRed() - b.getRed()) < MARGIN &&
            Math.abs(a.getGreen() - b.getGreen()) < MARGIN &&
            Math.abs(a.getBlue() - b.getBlue()) < MARGIN &&
            Math.abs(a.getAlpha() - b.getAlpha()) < MARGIN;
  }

  public static int pop(Color c) {
    return Math.abs(c.getRed() - c.getGreen()) +
            Math.abs(c.getGreen() - c.getBlue()) +
            Math.abs(c.getBlue() - c.getRed());
  }

  public static int lightness(Color c) {
    return c.getRed() + c.getGreen() + c.getBlue();
  }

  public static Color sepia(Color original) {
    final double tR = 0.393 * original.getRed() + 0.769 * original.getGreen() + 0.189 * original.getBlue();
    final double tG = 0.349 * original.getRed() + 0.686 * original.getGreen() + 0.168 * original.getBlue();
    final double tB = 0.272 * original.getRed() + 0.534 * original.getGreen() + 0.131 * original.getBlue();
    return new Color(Math.min(255, (int)tR), Math.min(255, (int)tG), Math.min(255, (int)tB));
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
    int skew100 = (int)(skew * 100);

    int r = ((darker.getRed() * skew100) +
            (lighter.getRed() * (100 - skew100))) / 100;
    r = Math.min(Math.max(0, r), 255);
    int g = ((darker.getGreen() * skew100) +
            (lighter.getGreen() * (100 - skew100))) / 100;
    g = Math.min(Math.max(0, g), 255);
    int b = ((darker.getBlue() * skew100) +
            (lighter.getBlue() * (100 - skew100))) / 100;
    b = Math.min(Math.max(0, b), 255);

    return new Color(r, g, b);
  }
}
