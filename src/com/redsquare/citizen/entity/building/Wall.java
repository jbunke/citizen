package com.redsquare.citizen.entity.building;

import com.redsquare.citizen.entity.Building;
import com.redsquare.citizen.entity.collision.Collider;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Wall extends BuildingComponent {
  public Wall(final Building building) {
    super(building);

    this.collider = Collider.getColliderFromType(Collider.EntityType.WALL);
  }

  @Override
  public BufferedImage getSprite() {
    BufferedImage sprite = new BufferedImage(72, 72, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) sprite.getGraphics();

    g.setColor(new Color(0, 0, 0));
    g.fillRect(0, 0, 73, 73);

    drawCollision(g);

    return sprite;
  }

  @Override
  public Point getSpriteOffset() {
    return new Point(-36, -36);
  }
}
