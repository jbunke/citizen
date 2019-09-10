package com.redsquare.citizen.devkit.sprite_gen;

import com.redsquare.citizen.graphics.RenderMood;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Tilemapping {
  private static final String TM_PATH = "res/img_assets/sprite_sheets/test/";

  private static final int X = 0, Y = 1, WIDTH = 2, HEIGHT = 3;
  private static final int[] HEAD_COORDS = new int[] { 41, 8, 160, 36 };
  private static final int[] EYEBROW_COORDS = new int[] { 41, 53, 27, 5 };
  private static final int[] BODY_COORDS = new int[] { 41, 67, 271, 29 };

  private static final Map<Category, int[]> COORD_MAP = Map.ofEntries(
    Map.entry(Category.HEAD, HEAD_COORDS),
    Map.entry(Category.EYEBROWS, EYEBROW_COORDS),
    Map.entry(Category.BODY, BODY_COORDS)
  );

  /** Used when certain texture blocks are identical and differences only
   * exist on higher layers. For example, eyebrows vary based on mood, but
   * the head layer is the same for each mood, so it is duplicated for every
   * mood. */
  static BufferedImage duplicateVertically(BufferedImage img, final int TIMES) {
    BufferedImage duplicated = new BufferedImage(img.getWidth(),
            img.getHeight() * TIMES, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) duplicated.getGraphics();

    for (int i = 0; i < TIMES; i++) {
      g.drawImage(img, 0, i * img.getHeight(), null);
    }

    return duplicated;
  }

  public static BufferedImage getBody(Color skinColor) {
    BufferedImage result = null;

    try {
      BufferedImage bodyProjection =
              ImageIO.read(new File("res/img_assets/sprite_gen/body/body_mapping.png"));

      result = getTexture(Category.BODY, skinColor, bodyProjection,
              1);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return result;
  }

  public static BufferedImage getHead(Color skinColor, Color hairColor) {
    BufferedImage result = null;

    try {
      BufferedImage headProjection =
              ImageIO.read(new File("res/img_assets/sprite_gen/heads/head_mapping.png"));
      BufferedImage eyebrowProjection =
              ImageIO.read(new File("res/img_assets/sprite_gen/heads/eyebrow_mapping.png"));

      BufferedImage head = getTexture(Category.HEAD, skinColor, headProjection,
              RenderMood.values().length);
      BufferedImage eyebrow = getTexture(Category.EYEBROWS, hairColor,
              eyebrowProjection, 1);

      result = new BufferedImage(head.getWidth(), head.getHeight(), BufferedImage.TYPE_INT_ARGB);
      Graphics2D g = (Graphics2D) result.getGraphics();
      g.drawImage(head, 0, 0, null);
      g.drawImage(eyebrow, 0, 0, null);

    } catch (IOException e) {
      e.printStackTrace();
    }

    return result;
  }

  private static BufferedImage getTexture(Category category, Color color,
                                          BufferedImage projection,
                                          final int DUPLICATIONS) {
    BufferedImage texture = null;
    int[] coords = COORD_MAP.get(category);

    try {
      texture = ImageIO.read(new File(TM_PATH + "skin_temp_spritesheet.png"));
      texture = texture.getSubimage(coords[X], coords[Y], coords[WIDTH], coords[HEIGHT]);
      texture = SpriteUniqueColorMapping.skinColorApplication(texture, color, 1);
      texture = SpriteUniqueColorMapping.expandTexture(texture, getMap(category), projection, 1, 4);

      if (DUPLICATIONS > 1) texture = duplicateVertically(texture, DUPLICATIONS);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return texture;
  }

  private static BufferedImage getMap(Category category) {
    int[] coords = COORD_MAP.get(category);

    BufferedImage map = null;

    try {
      map = ImageIO.read(new File(TM_PATH + "mapping_spritesheet.png"));
      map = map.getSubimage(coords[X], coords[Y], coords[WIDTH], coords[HEIGHT]);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return map;
  }

  private enum Category {
    HEAD, EYEBROWS, BODY
  }
}
