package com.redsquare.citizen.util;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Formatter {
  public static String properNoun(String name) {
    return name.substring(0, 1).toUpperCase() + name.substring(1);
  }

  public static BufferedImage scale(BufferedImage original, double s) {
    BufferedImage image = new BufferedImage((int)(original.getWidth() * s), (int)(original.getHeight() * s),
            BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) image.getGraphics();

    for (int x = 0; x < image.getWidth(); x++) {
      for (int y = 0; y < image.getHeight(); y++) {
        int oldX = (int)(x / s), oldY = (int)(y / s);
        g.setColor(new Color(original.getRGB(oldX, oldY), true));
        g.fillRect(x, y, 1, 1);
      }
    }

    return image;
  }
}
