package com.redsquare.citizen.entity.biodiversity;

import com.redsquare.citizen.util.Randoms;
import com.redsquare.citizen.worldgen.WorldCell;

public class PlantSpecies {
  private final int GROWTH_STAGES;
  private final GrowthStyle GROWTH_STYLE;
  private final Habitat HABITAT;
  private final double SPECIES_SIZE;
  private final Classification CLASSIFICATION;

  public enum Classification {
    FRUIT_TREE, NUT_TREE,
    SHRUB, FLOWER,
    GRASS,
    ROOTING_VEGETABLE
  }

  public enum GrowthStyle {
    ANNUAL, // The way a tree gains a ring every year
    STEADY, // Grows steadily over time
    PERENNIAL // Grows and dies within a year only to return
  }

  private PlantSpecies(final Classification classification,
          final int growthStages, final GrowthStyle growthStyle,
          final WorldCell.CellLandType primary, final Habitat.Range range) {
    this.CLASSIFICATION = classification;

    this.GROWTH_STAGES = growthStages;
    this.GROWTH_STYLE = growthStyle;

    this.HABITAT = Habitat.generate(primary, range, false);
    this.SPECIES_SIZE = Randoms.bounded(0., 1.);
  }

  public boolean isTree() {
    return this.getClassification() == Classification.FRUIT_TREE ||
            this.getClassification() == Classification.NUT_TREE;
  }

  public static PlantSpecies generateFruitTree(
          final WorldCell.CellLandType primary, final Habitat.Range range) {
    return new PlantSpecies(Classification.FRUIT_TREE,
            Randoms.bounded(60, 120), GrowthStyle.ANNUAL, primary, range);
  }

  public static PlantSpecies generateGrass(
          final WorldCell.CellLandType primary,
          final Habitat.Range range) {
    return new PlantSpecies(Classification.GRASS,
            Randoms.bounded(4, 7), GrowthStyle.STEADY, primary, range);
  }

  public Classification getClassification() {
    return CLASSIFICATION;
  }

  public double getSize() {
    return SPECIES_SIZE;
  }

  public int getGrowthStages() {
    return GROWTH_STAGES;
  }
}
