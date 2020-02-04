package com.redsquare.citizen.entity.building;

import com.redsquare.citizen.entity.Building;
import com.redsquare.citizen.entity.Entity;
import com.redsquare.citizen.worldgen.WorldPosition;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class BuildingComponent extends Entity {
  private final Building building;

  BuildingComponent(final Building building) {
    this.building = building;
  }

  public void setPosition(WorldPosition position) {
    this.position = position;
  }

  public BufferedImage getSprite() {
    BufferedImage sprite = new BufferedImage(72, 144, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) sprite.getGraphics();

    drawCollision(g);

    return sprite;
  }

  @Override
  public Point getSpriteOffset() {
    return new Point(-36, -144);
  }
}
