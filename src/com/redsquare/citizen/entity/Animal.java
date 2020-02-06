package com.redsquare.citizen.entity;

import com.redsquare.citizen.entity.biodiversity.AnimalSpecies;
import com.redsquare.citizen.systems.time.GameDate;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Animal extends LivingMoving {
  private AnimalSpecies animalSpecies;

  @Override
  public int age(GameDate now) {
    return 0;
  }

  @Override
  public boolean isAlive() {
    // TODO
    return true;
  }

  @Override
  public BufferedImage getSprite() {
    return null;
  }

  @Override
  public Point getSpriteOffset() {
    return null;
  }
}
