package com.redsquare.citizen.worldgen;

import com.redsquare.citizen.systems.politics.Settlement;

public class WorldCell {
  private boolean generated = false;
  private int elevation = 0;
  private Type type;
  private Region region;
  private Settlement settlement = null;

  public WorldCell(Type type) {
    generated = false;
    this.type = type;
    region = Region.TEMPERATE;
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
    PLAIN, DESERT, SEA, SHALLOW, BEACH, MOUNTAIN, HILL, FOREST
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
