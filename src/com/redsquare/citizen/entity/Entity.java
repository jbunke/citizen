package com.redsquare.citizen.entity;

import com.redsquare.citizen.graphics.Sprite;
import com.redsquare.citizen.util.FloatPoint;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Entity implements Comparable<Entity> {

  String ID;

  Sprite[] layers;

  /**
   * @return The (x, y) coordinates of the cell in the WorldCell[][] that
   * the entity is located
   * */
  public abstract Point worldLocation();

  /**
   * @return The (x, y) coordinates (potentially rounded) of the location in
   * the cell that the entity is located
   * */
  public abstract Point cellLocation();

  public abstract FloatPoint subCellLocation();

  @Override
  public int compareTo(Entity other) {
    if (worldLocation().y > other.worldLocation().y) return -1;
    else if (worldLocation().y < other.worldLocation().y) return 1;
    else {
      if (cellLocation().y > other.cellLocation().y) return -1;
      else if (cellLocation().y < other.cellLocation().y) return 1;
      else return Double.compare(subCellLocation().y, other.subCellLocation().y);
    }
  }

  public abstract BufferedImage getSprite();

  public abstract Point getSpriteOffset();

  public void update() {  }
}
