package com.redsquare.citizen.graphics;

import com.redsquare.citizen.util.Orientation;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Font extends Sprite {

  public static final Font CLEAN = new Font("res/fonts/font-clean.png",
          "Clean [FONT]", 13, 19);

  public Font(String file, String ID, int tileWidth, int tileHeight) {
    super(file, ID, tileWidth, tileHeight, SemanticMaps.FONT());
  }

  public BufferedImage getText(String text) {
    BufferedImage image = new BufferedImage(tileWidth * text.length(),
            tileHeight, BufferedImage.TYPE_INT_ARGB);

    Graphics2D g = (Graphics2D) image.getGraphics();

    for (int i = 0; i < text.length(); i++) {
      g.drawImage(getSprite(Character.toString(text.charAt(i))),
              tileWidth * i, 0, null);
    }

    return image;
  }

  /**
   * Call with String.split("\n")
   * */
  public BufferedImage getText(String[] lines, Orientation orientation) {
    List<BufferedImage> images = new ArrayList<>();

    for (String line : lines)
      if (line.length() > 0)
        images.add(getText(line));

    int widest = 0;
    int height = 0;

    for (BufferedImage image : images) {
      widest = Math.max(widest, image.getWidth());
      height += image.getHeight();
    }

    BufferedImage allLines = new BufferedImage(widest, height,
            BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) allLines.getGraphics();

    height = 0;
    for (BufferedImage image : images) {
      switch (orientation) {
        case LEFT_TOP:
        case LEFT_BOTTOM:
        case LEFT_CENTER_V:
          g.drawImage(image, 0, height, null);
          break;
        case CENTER_H_BOTTOM:
        case CENTER_H_CENTER_V:
        case CENTER_H_TOP:
          g.drawImage(image, (widest - image.getWidth()) / 2,
                  height, null);
          break;
        case RIGHT_BOTTOM:
        case RIGHT_CENTER_V:
        case RIGHT_TOP:
          g.drawImage(image, widest - image.getWidth(),
                  height, null);
          break;
      }
      height += image.getHeight();
    }

    return allLines;
  }
}
