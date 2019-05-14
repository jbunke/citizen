package com.redsquare.citizen.graphics;

import com.redsquare.citizen.GameDebug;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Sprite {

  private final String ID;

  private final BufferedImage[][] SPRITE_ARRAY;
  final BufferedImage SPRITE_SHEET;
  private final Map<String, Point> SEMANTIC_MAP;

  private static final int DEFAULT_TILE_SIZE = 32;

  final int tileWidth;
  final int tileHeight;

  private final int tilesAlongX;
  private final int tilesAlongY;

  public Sprite(String file, String ID, Map<String, Point> map) {
    this.ID = ID;

    SPRITE_SHEET = loadSpriteSheet(file);

    tileWidth = DEFAULT_TILE_SIZE;
    tileHeight = DEFAULT_TILE_SIZE;

    tilesAlongX = SPRITE_SHEET.getWidth() / tileWidth;
    tilesAlongY = SPRITE_SHEET.getHeight() / tileHeight;

    SPRITE_ARRAY = new BufferedImage[tilesAlongX][tilesAlongY];
    loadSpriteArray();

    SEMANTIC_MAP = map;
  }

  public Sprite(String file, String ID, int tileWidth, int tileHeight,
                Map<String, Point> map) {
    this.ID = ID;

    SPRITE_SHEET = loadSpriteSheet(file);

    this.tileWidth = tileWidth;
    this.tileHeight = tileHeight;

    tilesAlongX = SPRITE_SHEET.getWidth() / tileWidth;
    tilesAlongY = SPRITE_SHEET.getHeight() / tileHeight;

    SPRITE_ARRAY = new BufferedImage[tilesAlongX][tilesAlongY];
    loadSpriteArray();

    SEMANTIC_MAP = map;
  }

  private void loadSpriteArray() {
    for (int x = 0; x < tilesAlongX; x++) {
      for (int y = 0; y < tilesAlongY; y++) {
        SPRITE_ARRAY[x][y] = loadSprite(x, y);
      }
    }
  }

  private BufferedImage loadSprite(int x, int y) {
    return SPRITE_SHEET.getSubimage(
            x * tileWidth, y * tileWidth, tileWidth, tileHeight);
  }

  private BufferedImage loadSpriteSheet(String file) {
    BufferedImage sheet = null;

    try {
      sheet = ImageIO.read(new File(file));
    } catch (IOException e) {
      GameDebug.printMessage(
              "(sprite) Could not open file \"" + file + "\"",
              GameDebug::printError);
      e.printStackTrace();
    }

    return sheet;
  }

  public BufferedImage getSprite(String code) {
    if (SEMANTIC_MAP.containsKey(code)) {
      Point indices = SEMANTIC_MAP.get(code);

      return getSprite(indices.x, indices.y);
    }
    GameDebug.printMessage("(sprite) Could not find sprite \"" +
            code + "\" in the sprite sheet \"" + ID + "\"",
            GameDebug::printError);
    return null;
  }

  /**
   * Designed to only be called by the code version of getSprite
   *
   * Make public if it is more convenient is certain cases to call directly
   * */
  private BufferedImage getSprite(int x, int y) {
    return SPRITE_ARRAY[x][y];
  }
}
