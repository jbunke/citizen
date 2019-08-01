package com.redsquare.citizen.devkit.sprite_gen;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Tilemapping {
  /** Used when certain texture blocks are identical and differences only
   * exist on higher layers. For example, eyebrows vary based on mood, but
   * the head layer is the same for each mood, so it is duplicated for every
   * mood. */
  public static BufferedImage duplicateVertically(BufferedImage img, final int TIMES) {
    BufferedImage duplicated = new BufferedImage(img.getWidth(),
            img.getHeight() * TIMES, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) duplicated.getGraphics();

    for (int i = 0; i < TIMES; i++) {
      g.drawImage(img, 0, i * img.getHeight(), null);
    }

    return duplicated;
  }
}
