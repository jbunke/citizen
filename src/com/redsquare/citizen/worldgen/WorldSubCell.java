package com.redsquare.citizen.worldgen;

import java.awt.*;

public class WorldSubCell {
  private final Point location;
  private final WorldCell cell;

  private final Type baseType;

  public WorldSubCell(Point location, WorldCell cell, Type baseType) {
    this.location = location;
    this.cell = cell;
    this.baseType = baseType;
  }

  public enum Type {
    GRASS,
  }
}
