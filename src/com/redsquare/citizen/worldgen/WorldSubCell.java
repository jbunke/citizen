package com.redsquare.citizen.worldgen;

import com.redsquare.citizen.debug.GameDebug;

import java.awt.*;
import java.awt.image.BufferedImage;

public class WorldSubCell {
  private final Point location;
  private final WorldCell cell;
  private final Color testColor;

  private final WorldCell.CellLandType cellLandType;
  private final TileID tileID;

  WorldSubCell(Point location, WorldCell cell, WorldCell.CellLandType cellLandType) {
    this.location = location;
    this.cell = cell;
    this.cellLandType = cellLandType;

    this.testColor = tempColorFunction();
    this.tileID = TileID.fromCellLandType(cellLandType);
  }

  // TODO: temp; to be deleted
  private Color tempColorFunction() {
    switch (cellLandType) {
      case PLAIN:
        return (location.x + location.y) % 2 == 0 ?
                new Color(100, 150, 50) :
                new Color(120, 120, 50);
      case DESERT:
        return (location.x + location.y) % 2 == 0 ?
                new Color(200, 110, 80) :
                new Color(220, 120, 80);
      case HILL:
        return (location.x + location.y) % 2 == 0 ?
                new Color(100, 100, 50) :
                new Color(80, 80, 50);
      case SHALLOW:
        return new Color(100, 120, 200);
      case SEA:
        return new Color(70, 80, 150);
      case BEACH:
        return (location.x + location.y) % 2 == 0 ?
                new Color(200, 150, 100) :
                new Color(220, 170, 80);
      case MOUNTAIN:
        return (location.x + location.y) % 2 == 0 ?
                new Color(100, 100, 100) :
                new Color(80, 80, 80);
      case FOREST:
        return (location.x + location.y) % 2 == 0 ?
                new Color(50, 100, 0) :
                new Color(50, 70, 30);
      case NONE:
      default:
        return new Color(0, 0, 0);
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

    if (GameDebug.isActive())
      drawBorder(g, subCell.getWidth(), subCell.getHeight());

    return subCell;
  }

  private void drawBorder(Graphics2D g, final int WIDTH, final int HEIGHT) {
    g.setStroke(new BasicStroke(1));
    g.setColor(new Color(255, 100, 0));
    g.drawRect(0, 0, WIDTH, HEIGHT);
  }
}
