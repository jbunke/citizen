package com.redsquare.citizen.entity.biodiversity;

import com.redsquare.citizen.worldgen.WorldCell;

import java.util.HashSet;
import java.util.Set;

public class Habitat {
  private final Set<WorldCell.CellLandType> landTypes;
  private final Range range;

  private Habitat(final Set<WorldCell.CellLandType> landTypes, final Range range) {
    this.landTypes = landTypes;
    this.range = range;
  }

  static Habitat generate(WorldCell.CellLandType mustInclude, Range range,
                          final boolean IS_ANIMAL) {
    Set<WorldCell.CellLandType> landTypes = new HashSet<>();
    landTypes.add(mustInclude);
    landTypes.addAll(canAdapt(mustInclude, IS_ANIMAL));
    return new Habitat(landTypes, range);
  }

  private static Set<WorldCell.CellLandType> canAdapt(
          final WorldCell.CellLandType nativeRange, final boolean IS_ANIMAL) {
    if (!IS_ANIMAL)
      return Set.of(nativeRange);

    switch (nativeRange) {
      case BEACH:
        return Set.of(WorldCell.CellLandType.SHALLOW);
      case SHALLOW:
        return Set.of(WorldCell.CellLandType.BEACH);
      case PLAIN:
      case MOUNTAIN:
        return Set.of(WorldCell.CellLandType.HILL);
      case HILL:
        return Set.of(WorldCell.CellLandType.MOUNTAIN, WorldCell.CellLandType.PLAIN);
      case NONE:
      case FOREST:
      case SEA:
      case DESERT:
      default:
        return new HashSet<>();
    }
  }

  public enum Range {
    LOCAL, CONTINENTAL, GLOBAL
  }

  public Set<WorldCell.CellLandType> getLandTypes() {
    return landTypes;
  }

  public Range getRange() {
    return range;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("[");

    for (WorldCell.CellLandType cellLandType : landTypes) {
      sb.append(" ");
      sb.append(cellLandType);
    }

    sb.append(" ]");

    return "(" + range + ", " + sb.toString() + " )";
  }
}
