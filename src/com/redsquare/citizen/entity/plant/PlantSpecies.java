package com.redsquare.citizen.entity.plant;

public abstract class PlantSpecies  {
  private final int growthStages;
  private final GrowthStyle growthStyle;

  public enum GrowthStyle {
    ANNUAL, STEADY, PERENNIAL
  }

  PlantSpecies(final int growthStages, final GrowthStyle growthStyle) {
    this.growthStages = growthStages;
    this.growthStyle = growthStyle;
  }
}
