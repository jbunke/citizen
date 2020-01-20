package com.redsquare.citizen.worldgen;

import com.redsquare.citizen.debug.GameDebug;

import java.awt.*;
import java.awt.image.BufferedImage;

public class WorldSubCell {
  private final Point location;
  private final WorldCell cell;
  private final Color testColor;

  private final WorldCell.Type type;

  WorldSubCell(Point location, WorldCell cell, WorldCell.Type type) {
    this.location = location;
    this.cell = cell;
    this.type = type;

    switch (type) {
      case PLAIN:
        this.testColor = (location.x + location.y) % 2 == 0 ?
                new Color(100, 150, 50) :
                new Color(120, 120, 50);
        break;
      case DESERT:
        this.testColor = (location.x + location.y) % 2 == 0 ?
                new Color(200, 110, 80) :
                new Color(220, 120, 80);
        break;
      case HILL:
        this.testColor = (location.x + location.y) % 2 == 0 ?
                new Color(100, 100, 50) :
                new Color(80, 80, 50);
        break;
      case SHALLOW:
        this.testColor = new Color(100, 120, 200);
        break;
      case SEA:
        this.testColor = new Color(70, 80, 150);
        break;
      case BEACH:
        this.testColor = (location.x + location.y) % 2 == 0 ?
                new Color(200, 150, 100) :
                new Color(220, 170, 80);
        break;
      case MOUNTAIN:
        this.testColor = (location.x + location.y) % 2 == 0 ?
                new Color(100, 100, 100) :
                new Color(80, 80, 80);
        break;
      case FOREST:
        this.testColor = (location.x + location.y) % 2 == 0 ?
                new Color(50, 100, 0) :
                new Color(50, 70, 30);
        break;
      case NONE:
      default:
        this.testColor = new Color(0, 0, 0);
        break;
    }
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

    if (GameDebug.isActive()) {
      g.setStroke(new BasicStroke(1));
      g.setColor(new Color(255, 100, 0));
      g.drawRect(0, 0, subCell.getWidth(), subCell.getHeight());
    }

    return subCell;
  }
}
