package com.redsquare.citizen.entity.biodiversity;

import com.redsquare.citizen.worldgen.WorldCell;

public abstract class PlantSpecies  {
  private final int growthStages;
  private final GrowthStyle growthStyle;
  private final Habitat habitat;

  public enum GrowthStyle {
    ANNUAL, STEADY, PERENNIAL
  }

  PlantSpecies(final int growthStages, final GrowthStyle growthStyle,
               final WorldCell.CellLandType primary, final Habitat.Range range) {
    this.growthStages = growthStages;
    this.growthStyle = growthStyle;

    this.habitat = Habitat.generate(primary, range, false);
  }
}
