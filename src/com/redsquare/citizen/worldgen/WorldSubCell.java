package com.redsquare.citizen.worldgen;

import com.redsquare.citizen.debug.GameDebug;

import java.awt.*;
import java.awt.image.BufferedImage;

public class WorldSubCell {
  private final Point location;
  private final WorldCell cell;

  // TODO: Don't delete; could be relevant later if tile CHANGES
  //  (ex. tearing the floors out of a building)
  private final WorldCell.CellLandType cellLandType;
  private final TileID tileID;
  private String onCode;

  private static final int N = 0, W = 1, S = 2, E = 3;

  WorldSubCell(Point location, WorldCell cell, WorldCell.CellLandType cellLandType) {
    this.location = location;
    this.cell = cell;
    this.cellLandType = cellLandType;

    this.tileID = TileID.fromCellLandType(cellLandType);
  }

  void generate() {
    generateOnCode();
  }

  private WorldSubCell[] getNeighbours() {
    WorldSubCell[] neighbours = new WorldSubCell[4];

    neighbours[N] = cell.getSubCell(location.x, location.y - 1);
    neighbours[W] = cell.getSubCell(location.x - 1, location.y);
    neighbours[S] = cell.getSubCell(location.x, location.y + 1);
    neighbours[E] = cell.getSubCell(location.x + 1, location.y);

    return neighbours;
  }

  // TODO: OFF code

  private void generateOnCode() {
    StringBuilder code = new StringBuilder("ON_");

    for (WorldSubCell neighbour : getNeighbours()) {
      if (neighbour == null || !neighbour.tileID.equals(tileID))
        code.append("0");
      else
        code.append("1");
    }

    onCode = code.toString();
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

    g.drawImage(tileID.getSprite(onCode), 0, 0,
            subCell.getWidth(), subCell.getHeight(), null);

    // if (GameDebug.isActive())  drawBorder(g, subCell.getWidth(), subCell.getHeight());

    return subCell;
  }

  private void drawBorder(Graphics2D g, final int WIDTH, final int HEIGHT) {
    g.setStroke(new BasicStroke(1));
    g.setColor(new Color(255, 100, 0));
    g.drawRect(0, 0, WIDTH, HEIGHT);
  }
}
