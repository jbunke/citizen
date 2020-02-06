package com.redsquare.citizen.worldgen;

import com.redsquare.citizen.devkit.sprite_gen.Tilemapping;
import com.redsquare.citizen.graphics.SemanticMaps;
import com.redsquare.citizen.graphics.Sprite;

import java.awt.image.BufferedImage;

public enum TileID implements Comparable<TileID> {
  SHALLOW_WATER, ROUGH_WATER, CALM_WATER,

  BEACH_SAND,

  PLAINS_GRASS, HILLY_GRASS,

  STONE,

  DESERT_SAND, DUNE_SAND,

  FOREST_FLOOR, DARK_FOREST_FLOOR,

  VOID;

  private static final int TILE_DIMENSION = 72;
  private static final String FOLDER_PATH = "res/img_assets/tilemaps/";

  private final Sprite sprite;

  /** Lower priority encroaches on high priority in a deadlock */
  private static final TileID[] PRIORITY_ORDER = new TileID[] {
          VOID,
          ROUGH_WATER, CALM_WATER, SHALLOW_WATER,
          BEACH_SAND, DUNE_SAND, DESERT_SAND,
          DARK_FOREST_FLOOR, FOREST_FLOOR,
          HILLY_GRASS, PLAINS_GRASS,
          STONE
  };

  TileID() {
    this.sprite = new Sprite(
            Tilemapping.readTilemapFile(FOLDER_PATH + temp() + ".png"),
            this.name(), TILE_DIMENSION, TILE_DIMENSION, SemanticMaps.TILE_TYPE);
  }

  private String temp() {
    String name = this.name();

    switch (name) {
      case "PLAINS_GRASS":
      case "HILLY_GRASS":
      case "FOREST_FLOOR":
        return name;
      default:
        return "VOID";
    }
  }

  BufferedImage getSprite(String code) {
    return this.sprite.getSprite(code);
  }

  public int priorityRank() {
    for (int i = 0; i < PRIORITY_ORDER.length; i++)
      if (PRIORITY_ORDER[i].equals(this))
        return i;

    return -1;
  }

  public static boolean comesBefore(TileID reference, TileID comparison) {
    if (reference.equals(comparison)) return false;

    for (TileID tileID : PRIORITY_ORDER)
      if (tileID.equals(reference))
        return true;
      else if (tileID.equals(comparison))
        return false;

    return false;
  }

  public static TileID[] getPriorityOrder() {
    return PRIORITY_ORDER;
  }

  // TODO: temp function
  public static TileID fromCellLandType(WorldCell.CellLandType cellLandType) {
    switch (cellLandType) {
      case DESERT:
        return DESERT_SAND;
      case MOUNTAIN:
        return STONE;
      case HILL:
        return HILLY_GRASS;
      case PLAIN:
        return PLAINS_GRASS;
      case SEA:
        return CALM_WATER;
      case SHALLOW:
        return SHALLOW_WATER;
      case FOREST:
        return FOREST_FLOOR;
      case BEACH:
        return BEACH_SAND;
      case NONE:
      default:
        return VOID;
    }
  }
}
