package com.redsquare.citizen.worldgen;

import com.redsquare.citizen.debug.GameDebug;
import com.redsquare.citizen.util.Randoms;

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
  private static final int COUNTS_AS_BORDER = 5;

  WorldSubCell(Point location, WorldCell cell, WorldCell.CellLandType cellLandType) {
    this.location = location;
    this.cell = cell;
    this.cellLandType = cellLandType;

    this.tileID = generateTileID();
  }

  private TileID generateTileID() {
    // Literal edge case
    if (location.x < COUNTS_AS_BORDER) {
      WorldCell xNeighbour = cell.getWorld().getCell(cell.getLocation().x - 1, cell.getLocation().y);

      if (location.y < COUNTS_AS_BORDER) {
        WorldCell diagNeighbour = cell.getWorld().getCell(cell.getLocation().x - 1, cell.getLocation().y - 1);
        WorldCell yNeighbour = cell.getWorld().getCell(cell.getLocation().x, cell.getLocation().y - 1);

        return getCornerTileID(xNeighbour, yNeighbour, diagNeighbour,
                location.x, location.y);
      } else if (location.y >= WorldPosition.CELLS_IN_WORLD_CELL_DIM - COUNTS_AS_BORDER) {
        WorldCell diagNeighbour = cell.getWorld().getCell(cell.getLocation().x - 1, cell.getLocation().y + 1);
        WorldCell yNeighbour = cell.getWorld().getCell(cell.getLocation().x, cell.getLocation().y + 1);

        return getCornerTileID(xNeighbour, yNeighbour, diagNeighbour,
                location.x,
                WorldPosition.CELLS_IN_WORLD_CELL_DIM - (location.y + 1));
      } else {
        return getEdgeTileID(xNeighbour, location.x);
      }
    } else if (location.x >= WorldPosition.CELLS_IN_WORLD_CELL_DIM - COUNTS_AS_BORDER) {
      WorldCell xNeighbour = cell.getWorld().getCell(cell.getLocation().x + 1, cell.getLocation().y);

      if (location.y < COUNTS_AS_BORDER) {
        WorldCell diagNeighbour = cell.getWorld().getCell(cell.getLocation().x + 1, cell.getLocation().y - 1);
        WorldCell yNeighbour = cell.getWorld().getCell(cell.getLocation().x, cell.getLocation().y - 1);

        return getCornerTileID(xNeighbour, yNeighbour, diagNeighbour,
                WorldPosition.CELLS_IN_WORLD_CELL_DIM - (location.x + 1),
                location.y);
      } else if (location.y >= WorldPosition.CELLS_IN_WORLD_CELL_DIM - COUNTS_AS_BORDER) {
        WorldCell diagNeighbour = cell.getWorld().getCell(cell.getLocation().x + 1, cell.getLocation().y + 1);
        WorldCell yNeighbour = cell.getWorld().getCell(cell.getLocation().x, cell.getLocation().y + 1);

        return getCornerTileID(xNeighbour, yNeighbour, diagNeighbour,
                WorldPosition.CELLS_IN_WORLD_CELL_DIM - (location.x + 1),
                WorldPosition.CELLS_IN_WORLD_CELL_DIM - (location.y + 1));
      } else {
        return getEdgeTileID(xNeighbour,
                WorldPosition.CELLS_IN_WORLD_CELL_DIM - (location.x + 1));
      }
    } else if (location.y < COUNTS_AS_BORDER) {
      WorldCell yNeighbour = cell.getWorld().getCell(cell.getLocation().x, cell.getLocation().y - 1);
      return getEdgeTileID(yNeighbour, location.y);
    } else if (location.y >= WorldPosition.CELLS_IN_WORLD_CELL_DIM - COUNTS_AS_BORDER) {
      WorldCell yNeighbour = cell.getWorld().getCell(cell.getLocation().x, cell.getLocation().y + 1);
      return getEdgeTileID(yNeighbour,
              WorldPosition.CELLS_IN_WORLD_CELL_DIM - (location.y + 1));
    }

    return TileID.fromCellLandType(cellLandType);
  }

  private TileID getEdgeTileID(final WorldCell NEIGHBOUR, final int DEPTH) {
    if (NEIGHBOUR == null) return TileID.fromCellLandType(cellLandType);

    double odds = (COUNTS_AS_BORDER - DEPTH) / (double)COUNTS_AS_BORDER;
    double sum = odds + 1. + ((DEPTH) / (double)COUNTS_AS_BORDER);

    double prob = Randoms.bounded(0., sum);

    if (prob < odds)
      return TileID.fromCellLandType(NEIGHBOUR.getCellLandType());

    return TileID.fromCellLandType(cellLandType);
  }

  private TileID getCornerTileID(final WorldCell X_NEIGHBOUR,
                                 final WorldCell Y_NEIGHBOUR, final WorldCell DIAG_NEIGHBOUR,
                                 final int X_DEPTH, final int Y_DEPTH) {
    if (X_NEIGHBOUR == null || Y_NEIGHBOUR == null || DIAG_NEIGHBOUR == null)
      return TileID.fromCellLandType(cellLandType);

    double[] odds = new double[] {
            ((COUNTS_AS_BORDER - X_DEPTH) / (double)COUNTS_AS_BORDER) * 0.5,
            ((COUNTS_AS_BORDER - Y_DEPTH) / (double)COUNTS_AS_BORDER) * 0.5,
            Math.sqrt((Math.pow(COUNTS_AS_BORDER, 2.) -
                    Math.pow(X_DEPTH + Y_DEPTH, 2.)) /
                    Math.pow(COUNTS_AS_BORDER, 2.)) * 0.5,
            0.5 + ((X_DEPTH + Y_DEPTH) / (double)COUNTS_AS_BORDER)
    };

    double[] thresholds = new double[] {
            odds[0],
            odds[0] + odds[1],
            odds[0] + odds[1] + odds[2],
            odds[0] + odds[1] + odds[2] + odds[3]
    };

    double prob = Randoms.bounded(0., thresholds[3]);

    if (prob < thresholds[0])
      return TileID.fromCellLandType(X_NEIGHBOUR.getCellLandType());
    else if (prob < thresholds[1])
      return TileID.fromCellLandType(Y_NEIGHBOUR.getCellLandType());
    else if (prob < thresholds[2])
      return TileID.fromCellLandType(DIAG_NEIGHBOUR.getCellLandType());

    return TileID.fromCellLandType(cellLandType);
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

    g.drawImage(tileID.getSprite(generateCode(true, tileID)), 0, 0,
            subCell.getWidth(), subCell.getHeight(), null);

    drawNeighbouring(g, subCell.getWidth(), subCell.getHeight());

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
