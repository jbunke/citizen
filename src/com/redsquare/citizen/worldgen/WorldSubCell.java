package com.redsquare.citizen.worldgen;

import java.awt.*;
import java.awt.image.BufferedImage;

public class WorldSubCell {
  private final Point location;
  private final WorldCell cell;
  private final Color testColor;

  private final Type baseType;

  WorldSubCell(Point location, WorldCell cell, Type baseType) {
    this.location = location;
    this.cell = cell;
    this.baseType = baseType;

    this.testColor = (location.x + location.y) % 2 == 0 ?
            new Color(100, 150, 50) :
            new Color(120, 120, 50);
  }

  public enum Type {
    GRASS,
  }

  public WorldCell getCell() {
    return cell;
  }

  public Point getLocation() {
    return location;
  }

  public BufferedImage draw(int zoomLevel) {
    BufferedImage subCell = new BufferedImage(
            (int)WorldPosition.CELL_DIMENSION_LENGTH / zoomLevel,
            (int)WorldPosition.CELL_DIMENSION_LENGTH / zoomLevel,
            BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) subCell.getGraphics();

    g.setColor(testColor);
    g.fillRect(0, 0, subCell.getWidth(), subCell.getHeight());

    return subCell;
  }
}
