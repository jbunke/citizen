package com.redsquare.citizen.testing;

import com.redsquare.citizen.graphics.Font;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SpriteTester {

  private static Font font = new Font("res/fonts/font-clean.png",
          "Clean [FONT]", 13, 19);

  public static void render(Graphics2D g) {
    BufferedImage glyph = font.getText("You just have to\nknow the\nname of the game".split("\n"));
    g.drawImage(glyph, 200, 200, null);
  }
}
