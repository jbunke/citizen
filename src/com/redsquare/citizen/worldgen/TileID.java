package com.redsquare.citizen.worldgen;

public enum TileID {
  SHALLOW_WATER, ROUGH_WATER, CALM_WATER,

  BEACH_SAND,

  PLAINS_GRASS, HILLY_GRASS,

  STONE,

  DESERT_SAND, DUNE_SAND,

  FOREST_FLOOR, DARK_FOREST_FLOOR,

  VOID;

  private static final TileID[] PRIORITY_ORDER = new TileID[] {
          VOID,
          ROUGH_WATER, CALM_WATER, SHALLOW_WATER,
          BEACH_SAND, DUNE_SAND, DESERT_SAND,
          DARK_FOREST_FLOOR, FOREST_FLOOR,
          HILLY_GRASS, PLAINS_GRASS,
          STONE
  };

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
