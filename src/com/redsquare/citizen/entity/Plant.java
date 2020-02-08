package com.redsquare.citizen.entity;

import com.redsquare.citizen.entity.biodiversity.PlantSpecies;
import com.redsquare.citizen.entity.collision.Collider;
import com.redsquare.citizen.systems.time.GameDate;
import com.redsquare.citizen.util.Randoms;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Plant extends LivingEntity {
  private final PlantSpecies PLANT_SPECIES;
  private int growthStage;

  private Plant(final PlantSpecies plantSpecies) {
    this.PLANT_SPECIES = plantSpecies;

    this.growthStage = 1;
    this.collider = generateCollider();
  }

  private Plant(final PlantSpecies plantSpecies, int growthStage) {
    this.PLANT_SPECIES = plantSpecies;

    this.growthStage = growthStage;
    this.collider = generateCollider();
  }

  public static Plant generateYoungPlant(final PlantSpecies plantSpecies) {
    return new Plant(plantSpecies);
  }

  public static Plant generateMaturePlant(final PlantSpecies plantSpecies) {
    int stages = plantSpecies.getGrowthStages();

    if (plantSpecies.isTree())
      return new Plant(plantSpecies,
              Randoms.bounded((int)(stages * 0.4), (int)(stages * 0.6)));

    return new Plant(plantSpecies, stages);
  }

  private Collider generateCollider() {
    switch (PLANT_SPECIES.getClassification()) {
      case GRASS:
      case SHRUB:
      case FLOWER:
      case ROOTING_VEGETABLE:
        return Collider.getColliderFromType(Collider.EntityType.NO_COLLISION);
      default:
        return Collider.getTreeCollider(growthStage, PLANT_SPECIES.getSize());
    }
  }

  @Override
  public int age(GameDate now) {
    return 0;
  }

  @Override
  public boolean isAlive() {
    return false;
  }

  @Override
  public BufferedImage getSprite() {
    BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) image.getGraphics();

    drawDebug(g);

    return image;
  }

  @Override
  public Point getSpriteOffset() {
    return new Point(-50, -50);
  }
}
