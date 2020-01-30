package com.redsquare.citizen.entity.animal;

import com.redsquare.citizen.worldgen.WorldCell;

public class Habitat {
  private final WorldCell.CellLandType[] landTypes;
  private final Range range;

  private Habitat(final WorldCell.CellLandType[] landTypes, final Range range) {
    this.landTypes = landTypes;
    this.range = range;
  }

  static Habitat generate() {
    return new Habitat(new WorldCell.CellLandType[] {}, Range.LOCAL);
  }

  public enum Range {
    LOCAL, CONTINENTAL, GLOBAL
  }

  public WorldCell.CellLandType[] getLandTypes() {
    return landTypes;
  }

  public Range getRange() {
    return range;
  }
}
