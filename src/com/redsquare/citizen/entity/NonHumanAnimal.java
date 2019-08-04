package com.redsquare.citizen.entity;

import com.redsquare.citizen.systems.time.GameDate;

import java.awt.*;
import java.awt.image.BufferedImage;

public class NonHumanAnimal extends Animal {

  @Override
  int age(GameDate now) {
    return 0;
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
