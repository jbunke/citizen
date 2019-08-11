package com.redsquare.citizen.worldgen;

import com.redsquare.citizen.entity.Entity;
import com.redsquare.citizen.util.FloatPoint;

import java.awt.*;

public class WorldPosition {
  public static final int CELLS_IN_WORLD_CELL_DIM = 384;
  public static final double CELL_DIMENSION_LENGTH = 200.;

  private final World world;
  private final Entity associated;

  private Point worldPos;
  private Point cellPos;
  private FloatPoint subCellPos;

  public WorldPosition(Point worldPos, Point cellPos, FloatPoint subCellPos,
                       World world, Entity associated) {
    this.worldPos = worldPos;
    this.cellPos = cellPos;
    this.subCellPos = subCellPos;

    this.world = world;
    this.associated = associated;

    if (isEntity())
      world.getCell(worldPos.x, worldPos.y).addEntity(associated);
  }

  public static WorldPosition copy(WorldPosition ref) {
    return new WorldPosition(ref.worldPos, ref.cellPos,
            ref.subCellPos, ref.world, null);
  }

  public static FloatPoint diff(WorldPosition a, WorldPosition b) {
    final double WORLD_CELL_LENGTH =
            CELLS_IN_WORLD_CELL_DIM * CELL_DIMENSION_LENGTH;

    double x = ((b.worldPos.x - a.worldPos.x) * WORLD_CELL_LENGTH) +
            ((b.cellPos.x - a.cellPos.x) * CELL_DIMENSION_LENGTH) +
            (b.subCellPos.x - a.subCellPos.x);
    double y = ((b.worldPos.y - a.worldPos.y) * WORLD_CELL_LENGTH) +
            ((b.cellPos.y - a.cellPos.y) * CELL_DIMENSION_LENGTH) +
            (b.subCellPos.y - a.subCellPos.y);

    return new FloatPoint(x, y);
  }

  public Point world() {
    return worldPos;
  }

  public Point cell() {
    return cellPos;
  }

  public FloatPoint subCell() {
    return subCellPos;
  }

  public void move(double changeX, double changeY) {
    subCellPos = new FloatPoint(subCellPos.x + changeX, subCellPos.y + changeY);
    wrapFix();
  }

  private void wrapFix() {
    // X adjustment
    if (subCellPos.x < 0.) {
      subCellPos = new FloatPoint(CELL_DIMENSION_LENGTH + subCellPos.x, subCellPos.y);
      cellPos = new Point(cellPos.x - 1, cellPos.y);
      if (cellPos.x < 0) {
        cellPos = new Point(CELLS_IN_WORLD_CELL_DIM + cellPos.x, cellPos.y);

        // Remove entity from previously occupied world cell
        if (isEntity()) world.getCell(worldPos.x, worldPos.y).removeEntity(associated);

        worldPos = new Point(worldPos.x - 1, worldPos.y);

        if (isEntity()) world.getCell(worldPos.x, worldPos.y).addEntity(associated);
      }
    } else if (subCellPos.x >= CELL_DIMENSION_LENGTH) {
      subCellPos = new FloatPoint(subCellPos.x - CELL_DIMENSION_LENGTH, subCellPos.y);
      cellPos = new Point(cellPos.x + 1, cellPos.y);
      if (cellPos.x >= CELLS_IN_WORLD_CELL_DIM) {
        cellPos = new Point(cellPos.x - CELLS_IN_WORLD_CELL_DIM, cellPos.y);

        // Remove entity from previously occupied world cell
        if (isEntity()) world.getCell(worldPos.x, worldPos.y).removeEntity(associated);

        worldPos = new Point(worldPos.x + 1, worldPos.y);

        if (isEntity()) world.getCell(worldPos.x, worldPos.y).addEntity(associated);
      }
    }

    // Y adjustment
    if (subCellPos.y < 0.) {
      subCellPos = new FloatPoint(subCellPos.x, CELL_DIMENSION_LENGTH + subCellPos.y);
      cellPos = new Point(cellPos.x, cellPos.y - 1);
      if (cellPos.y < 0) {
        cellPos = new Point(cellPos.x, CELLS_IN_WORLD_CELL_DIM + cellPos.y);

        // Remove entity from previously occupied world cell
        if (isEntity()) world.getCell(worldPos.x, worldPos.y).removeEntity(associated);

        worldPos = new Point(worldPos.x, worldPos.y - 1);

        if (isEntity()) world.getCell(worldPos.x, worldPos.y).addEntity(associated);
      }
    } else if (subCellPos.y >= CELL_DIMENSION_LENGTH) {
      subCellPos = new FloatPoint(subCellPos.x, subCellPos.y - CELL_DIMENSION_LENGTH);
      cellPos = new Point(cellPos.x, cellPos.y + 1);
      if (cellPos.y >= CELLS_IN_WORLD_CELL_DIM) {
        cellPos = new Point(cellPos.x, cellPos.y - CELLS_IN_WORLD_CELL_DIM);

        // Remove entity from previously occupied world cell
        if (isEntity()) world.getCell(worldPos.x, worldPos.y).removeEntity(associated);

        worldPos = new Point(worldPos.x, worldPos.y + 1);

        if (isEntity()) world.getCell(worldPos.x, worldPos.y).addEntity(associated);
      }
    }
  }

  private boolean isEntity() {
    return (world != null & associated != null);
  }

  @Override
  public String toString() {
    return "WORLD: (" + worldPos.x + ", " + worldPos.y + "); CELL: (" +
            cellPos.x + ", " + cellPos.y + "); SUB-CELL: (" +
            (int)subCellPos.x + ", " + (int)subCellPos.y + ")";
  }
}
