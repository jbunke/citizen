package com.redsquare.citizen.devkit;

import java.awt.*;
import java.awt.image.BufferedImage;

class ShadowTester {
  private static final int LOWER_LIMIT = 55;
  private static final int SHADOW_START = 37;
  private static final int Y_RANGE = LOWER_LIMIT - SHADOW_START;

  // TODO: DELETE OR refactor into solution
  // this is just a proof of concept
  static BufferedImage shadow(BufferedImage sprite, int time) {
    boolean[][] bitmap = new boolean[78][78];

    assert (time >= 0 && time <= 12);
    BufferedImage result = new BufferedImage(78, 78,
            BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) result.getGraphics();
    g.setColor(new Color(0, 0, 0, 30));

    for (int x = 20; x < 58; x++) {
      for (int y = 3; y < 58; y++) {
        int alpha = new Color(sprite.getRGB(x, y), true).getAlpha();

        if (alpha > 0) {
          // Casts a shadow
          int putY = SHADOW_START + (int)(((y - 3) /
                  (double)(LOWER_LIMIT - 3)) * Y_RANGE);
          int width = (int)(38 * ((putY - SHADOW_START) / (double)Y_RANGE));
          int point = 63 - 5 * time;
          int startX = point + (int)((20 - point) *
                  (1 - ((LOWER_LIMIT - y) / (double)(LOWER_LIMIT - 3))));
          int putX = startX + (int)(((x - 20) / (double)(58 - 20)) * width);

          if (bitmap[putX][putY]) continue;
          else bitmap[putX][putY] = true;

          g.fillRect(putX, putY, 1, 1);
        }
      }
    }

    return result;
  }
}
