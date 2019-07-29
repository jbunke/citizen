package com.redsquare.citizen.devkit.sprite_gen;

import com.redsquare.citizen.util.MathExt;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class SpriteUniqueColorMapping {

  public static BufferedImage skinColorApplication(BufferedImage base, Color skin, final int PX_SIZE) {
    BufferedImage withSkin = new BufferedImage(base.getWidth(),
            base.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) withSkin.getGraphics();

    for (int x = 0; x < base.getWidth(); x += PX_SIZE) {
      for (int y = 0; y < base.getHeight(); y += PX_SIZE) {
        Color c = new Color(base.getRGB(x, y), true);

        if (c.getAlpha() == 255) {
          // Eye cases
          if (c.equals(new Color(255, 0, 0))) {
            g.setColor(new Color(0, 0, 0));
          } else if (c.equals(new Color(0, 0, 255))) {
            g.setColor(new Color(255, 255, 255));
          } else if (c.getRed() == c.getGreen() && c.getRed() == c.getBlue()) {
            double scale = c.getRed() / (double)214;

            double red = skin.getRed() * scale;
            double green = skin.getGreen() * scale;
            double blue = skin.getBlue() * scale;

            Color scaledSkin = new Color((int)MathExt.bounded(red, 0, 255),
                    (int)MathExt.bounded(green, 0, 255),
                    (int)MathExt.bounded(blue, 0, 255));
            g.setColor(scaledSkin);
          } else {
            g.setColor(c);
          }

          g.fillRect(x, y, PX_SIZE, PX_SIZE);
        }
      }
    }

    return withSkin;
  }

  public static BufferedImage expandTexture(BufferedImage partial, BufferedImage map, final int PX_SIZE) {
    BufferedImage texture = new BufferedImage(map.getWidth(),
            map.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) texture.getGraphics();
    Map<Color, Color> corres = new HashMap<>();

    // Part 1: populate
    for (int x = 0; x < map.getWidth(); x += PX_SIZE) {
      for (int y = 0; y < map.getHeight(); y += PX_SIZE) {
        Color c = new Color(map.getRGB(x, y), true);

        if (c.getAlpha() == 255) {
          Color onTexture = new Color(partial.getRGB(x, y), true);

          if (onTexture.getAlpha() == 255) corres.put(c, onTexture);
        }
      }
    }

    // Part 2: expand
    for (int x = 0; x < map.getWidth(); x += PX_SIZE) {
      for (int y = 0; y < map.getHeight(); y += PX_SIZE) {
        Color c = new Color(map.getRGB(x, y), true);

        if (corres.containsKey(c)) {
          g.setColor(corres.get(c));
          g.fillRect(x, y, PX_SIZE, PX_SIZE);
        }
      }
    }

    return texture;
  }

  static BufferedImage mapToColors(BufferedImage ref, final int PX_SIZE) {
    BufferedImage img = new BufferedImage(ref.getWidth(), ref.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics2D gr = (Graphics2D) img.getGraphics();

    int r = 105, g = 105, b = 105;

    for (int x = 0; x < ref.getWidth(); x += PX_SIZE) {
      for (int y = 0; y < ref.getHeight(); y += PX_SIZE) {
        Color c = new Color(ref.getRGB(x, y), true);

        if (c.getAlpha() == 255) {
          gr.setColor(new Color(r, g, b));
          gr.fillRect(x, y, PX_SIZE, PX_SIZE);

          if (r >= 255) {
            if (g >= 255) {
              b += 10;
              g = 105;
            } else g += 10;
            r = 105;
          } else r += 10;
        }
      }
    }

    return img;
  }
}
