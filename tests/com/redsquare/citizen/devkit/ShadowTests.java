package com.redsquare.citizen.devkit;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ShadowTests {

  private static final String PNG = "png";

  @Test
  public void castShadowsOverSpriteAtAllHours() {
    String filepath = "test_output/shadows/spriteAt";

    try {
      BufferedImage sprite =
              ImageIO.read(new File("res/test_resources/shadows/example.png"));

      for (int i = 0; i <= 12; i++) {
        BufferedImage atTime = new BufferedImage(sprite.getWidth(),
                sprite.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) atTime.getGraphics();

        g.drawImage(ShadowTester.shadow(sprite, i), 0, 0, null);
        g.drawImage(sprite, 0, 0, null);

        ImageIO.write(atTime, PNG, new File(filepath + i + "." + PNG));
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
