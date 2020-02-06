package com.redsquare.citizen.worldgen;

import com.redsquare.citizen.debug.GameDebug;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class WorldSubCell {
  private final Point location;
  private final WorldCell cell;

  // TODO: Don't delete; could be relevant later if tile CHANGES
  //  (ex. tearing the floors out of a building)
  private final WorldCell.CellLandType cellLandType;
  private final TileID tileID;

  private static final int N = 0, W = 1, S = 2, E = 3;

  WorldSubCell(Point location, WorldCell cell, WorldCell.CellLandType cellLandType) {
    this.location = location;
    this.cell = cell;
    this.cellLandType = cellLandType;

    this.tileID = TileID.fromCellLandType(cellLandType);
  }

  void generate() {
    // TODO
  }

  private WorldSubCell[] getNeighbours() {
    WorldSubCell[] neighbours = new WorldSubCell[4];

    neighbours[N] = cell.getSubCell(location.x, location.y - 1);
    neighbours[W] = cell.getSubCell(location.x - 1, location.y);
    neighbours[S] = cell.getSubCell(location.x, location.y + 1);
    neighbours[E] = cell.getSubCell(location.x + 1, location.y);

    return neighbours;
  }

  private void drawNeighbouring(final Graphics2D g,
                                final int WIDTH, final int HEIGHT) {
    Set<TileID> neighbourTiles = new HashSet<>();
    WorldSubCell[] neighbours = getNeighbours();

    for (WorldSubCell neighbour : neighbours)
      if (neighbour != null) neighbourTiles.add(neighbour.tileID);

    neighbourTiles.remove(this.tileID);

    List<TileID> neighbourTileList = new ArrayList<>(neighbourTiles);
    neighbourTileList.sort(Comparator.comparingInt(TileID::priorityRank));

    int thisPriority = this.tileID.priorityRank();

    for (TileID neighbour : neighbourTileList) {
      if (neighbour.priorityRank() < thisPriority) {
        String offCode = generateCode(false, neighbour);

        g.drawImage(neighbour.getSprite(offCode), 0, 0,
                WIDTH, HEIGHT, null);
      }
    }
  }

  private String generateCode(final boolean IS_ON, final TileID REFERENCE) {
    StringBuilder code = IS_ON ?
            new StringBuilder("ON_") : new StringBuilder("OFF_");

    for (WorldSubCell neighbour : getNeighbours()) {
      if (neighbour == null ||
              (!neighbour.tileID.equals(REFERENCE) &&
                      IS_ON ? 
                      neighbour.tileID.priorityRank() < REFERENCE.priorityRank() :
                      neighbour.tileID.priorityRank() > REFERENCE.priorityRank()))
        code.append("0");
      else
        code.append("1");
    }

    return code.toString();
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

    drawNeighbouring(g, subCell.getWidth(), subCell.getHeight());

    g.drawImage(tileID.getSprite(generateCode(true, tileID)), 0, 0,
            subCell.getWidth(), subCell.getHeight(), null);

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
