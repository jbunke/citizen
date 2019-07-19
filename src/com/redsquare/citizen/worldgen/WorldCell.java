package com.redsquare.citizen.worldgen;

import com.redsquare.citizen.systems.politics.Settlement;

public class WorldCell {
  private boolean generated;
  private int elevation = 0;
  private Type type;
  private Region region;
  private Settlement settlement = null;
  private Settlement province = null;

  public WorldCell(Type type) {
    generated = false;
    this.type = type;
    region = Region.TEMPERATE;
  }

  void populateProvince(Settlement province) {
    this.province = province;
  }

  void populateSettlement(Settlement settlement) {
    this.settlement = settlement;
  }

  boolean hasSettlement() {
    return settlement != null;
  }

  Settlement getSettlement() {
    return settlement;
  }

  Settlement getProvince() {
    return province;
  }

  void setRegion(Region region) {
    this.region = region;
  }

  void setElevationAndType(int elevation, Type type) {
    this.elevation = elevation;
    this.type = type;
  }

  public enum Region {
    POLAR, TEMPERATE, SUBTROPICAL, TROPICAL
  }

  public enum Type {
    PLAIN, DESERT, SEA, SHALLOW, BEACH, MOUNTAIN, HILL, FOREST;

    boolean isLand() {
      switch (this) {
        case PLAIN:
        case DESERT:
        case FOREST:
        case MOUNTAIN:
        case HILL:
        case BEACH:
          return true;
        case SHALLOW:
        case SEA:
        default:
          return false;
      }
    }
  }

  Type getType() {
    return type;
  }

  Region getRegion() {
    return region;
  }

  int getElevation() {
    return elevation;
  }

  boolean isLand() {
    return type != Type.SEA && type != Type.SHALLOW;
  }
}
