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
    BufferedImage sprite = new BufferedImage(72, 144, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) sprite.getGraphics();

    g.setColor(new Color(100, 100, 100));
    g.fillRect(0, 0, 72, 72);

    g.setColor(new Color(0, 0, 0));
    g.fillRect(0, 72, 72, 72);

    drawCollision(g);
    drawCoordinate(g);

    return sprite;
  }
}
